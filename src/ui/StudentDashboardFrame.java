package ui;

import test.database.JsonDatabaseManager;
import test.model.Course;
import test.model.Lesson;
import test.model.Student;
import test.service.AuthenticationService;
import test.service.CourseService;
import test.service.UserService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class StudentDashboardFrame extends JFrame {
    private AuthenticationService authService;
    private CourseService courseService;
    private UserService userService;
    private Student currentStudent;
    private JTable availableCoursesTable;
    private JTable enrolledCoursesTable;
    private JTable lessonsTable;
    private DefaultTableModel availableCoursesModel;
    private DefaultTableModel enrolledCoursesModel;
    private DefaultTableModel lessonsModel;
    private JButton enrollButton;
    private JButton viewLessonsButton;
    private JButton markCompletedButton;
    private JButton logoutButton;
    private Course selectedCourse;
    
    public StudentDashboardFrame(AuthenticationService authService, JsonDatabaseManager dbManager) {
        this.authService = authService;
        this.courseService = new CourseService(dbManager);
        this.userService = new UserService(dbManager);
        this.currentStudent = (Student) authService.getCurrentUser();
        initializeUI();
        loadData();
    }
    
    private void refreshStudentData() {
        currentStudent = (Student) userService.getUserById(currentStudent.getUserId());
    }
    
    private void initializeUI() {
        setTitle("Student Dashboard - " + currentStudent.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Top Panel - Welcome and Logout
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + currentStudent.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        logoutButton = new JButton("Logout");
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Main Panel with Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Available Courses Tab
        JPanel availableCoursesPanel = createAvailableCoursesPanel();
        tabbedPane.addTab("Available Courses", availableCoursesPanel);
        
        // Enrolled Courses Tab
        JPanel enrolledCoursesPanel = createEnrolledCoursesPanel();
        tabbedPane.addTab("My Courses", enrolledCoursesPanel);
        
        add(tabbedPane, BorderLayout.CENTER);

        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
    }
    
    private JPanel createAvailableCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table
        String[] columns = {"Course ID", "Title", "Description", "Instructor ID"};
        availableCoursesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableCoursesTable = new JTable(availableCoursesModel);
        availableCoursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(availableCoursesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        enrollButton = new JButton("Enroll in Selected Course");
        enrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEnroll();
            }
        });
        buttonPanel.add(enrollButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createEnrolledCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Left: Enrolled Courses Table
        String[] courseColumns = {"Course ID", "Title", "Description"};
        enrolledCoursesModel = new DefaultTableModel(courseColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrolledCoursesTable = new JTable(enrolledCoursesModel);
        enrolledCoursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane coursesScrollPane = new JScrollPane(enrolledCoursesTable);
        coursesScrollPane.setPreferredSize(new Dimension(400, 0));
        
        // Right: Lessons Table
        String[] lessonColumns = {"Lesson ID", "Title", "Completed"};
        lessonsModel = new DefaultTableModel(lessonColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lessonsTable = new JTable(lessonsModel);
        lessonsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane lessonsScrollPane = new JScrollPane(lessonsTable);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                coursesScrollPane, lessonsScrollPane);
        splitPane.setDividerLocation(400);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        viewLessonsButton = new JButton("View Lessons");
        viewLessonsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewLessons();
            }
        });
        markCompletedButton = new JButton("Mark Lesson as Completed");
        markCompletedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleMarkCompleted();
            }
        });
        buttonPanel.add(viewLessonsButton);
        buttonPanel.add(markCompletedButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        

        enrolledCoursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleViewLessons();
            }
        });
        
        return panel;
    }
    
    private void loadData() {
        loadAvailableCourses();
        loadEnrolledCourses();
    }
    
    private void loadAvailableCourses() {
        availableCoursesModel.setRowCount(0);
        List<Course> availableCourses = courseService.getAvailableCourses(currentStudent.getUserId());
        for (Course course : availableCourses) {
            availableCoursesModel.addRow(new Object[]{
                course.getCourseId(),
                course.getTitle(),
                course.getDescription(),
                course.getInstructorId()
            });
        }
    }
    
    private void loadEnrolledCourses() {
        enrolledCoursesModel.setRowCount(0);
        List<String> enrolledCourseIds = currentStudent.getEnrolledCourses();
        for (String courseId : enrolledCourseIds) {
            Course course = courseService.getCourseById(courseId);
            if (course != null) {
                enrolledCoursesModel.addRow(new Object[]{
                    course.getCourseId(),
                    course.getTitle(),
                    course.getDescription()
                });
            }
        }
    }
    
    private void handleEnroll() {
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String courseId = (String) availableCoursesModel.getValueAt(selectedRow, 0);
        
        try {
            courseService.enrollStudent(courseId, currentStudent.getUserId());
            // Refresh student data from database
            refreshStudentData();
            JOptionPane.showMessageDialog(this, "Successfully enrolled in course!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                    "Enrollment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleViewLessons() {
        int selectedRow = enrolledCoursesTable.getSelectedRow();
        if (selectedRow < 0) {
            lessonsModel.setRowCount(0);
            selectedCourse = null;
            return;
        }
        
        String courseId = (String) enrolledCoursesModel.getValueAt(selectedRow, 0);
        selectedCourse = courseService.getCourseById(courseId);
        
        if (selectedCourse == null) {
            lessonsModel.setRowCount(0);
            return;
        }
        
        lessonsModel.setRowCount(0);
        List<Lesson> lessons = selectedCourse.getLessons();
        for (Lesson lesson : lessons) {
            boolean completed = currentStudent.isLessonCompleted(courseId, lesson.getLessonId());
            lessonsModel.addRow(new Object[]{
                lesson.getLessonId(),
                lesson.getTitle(),
                completed ? "Yes" : "No"
            });
        }
    }
    
    private void handleMarkCompleted() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to mark as completed.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String lessonId = (String) lessonsModel.getValueAt(selectedRow, 0);
        
        try {
            courseService.markLessonCompleted(selectedCourse.getCourseId(), lessonId, 
                    currentStudent.getUserId());
            // Refresh student data from database
            refreshStudentData();
            JOptionPane.showMessageDialog(this, "Lesson marked as completed!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            handleViewLessons(); // Refresh lessons table
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleLogout() {
        authService.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }
}
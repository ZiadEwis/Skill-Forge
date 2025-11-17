package ui;

import database.JsonDatabaseManager;
import model.Course;
import model.Lesson;
import model.Instructor;
import model.Student;
import service.AuthenticationService;
import service.CourseService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Instructor Dashboard Frame
 */
public class InstructorDashboardFrame extends JFrame {
    private AuthenticationService authService;
    private CourseService courseService;
    private Instructor currentInstructor;
    private JTable coursesTable;
    private JTable lessonsTable;
    private JTable studentsTable;
    private DefaultTableModel coursesModel;
    private DefaultTableModel lessonsModel;
    private DefaultTableModel studentsModel;
    private JButton createCourseButton;
    private JButton editCourseButton;
    private JButton deleteCourseButton;
    private JButton addLessonButton;
    private JButton editLessonButton;
    private JButton deleteLessonButton;
    private JButton viewStudentsButton;
    private JButton logoutButton;
    private Course selectedCourse;
    
    public InstructorDashboardFrame(AuthenticationService authService, JsonDatabaseManager dbManager) {
        this.authService = authService;
        this.courseService = new CourseService(dbManager);
        this.currentInstructor = (Instructor) authService.getCurrentUser();
        initializeUI();
        loadCourses();
    }
    
    private void initializeUI() {
        setTitle("Instructor Dashboard - " + currentInstructor.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + currentInstructor.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        logoutButton = new JButton("Logout");
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Main Panel with Split Pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left: Courses Panel
        JPanel coursesPanel = createCoursesPanel();
        mainSplitPane.setLeftComponent(coursesPanel);
        
        // Right: Lessons and Students Panel
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel lessonsPanel = createLessonsPanel();
        JPanel studentsPanel = createStudentsPanel();
        rightSplitPane.setTopComponent(lessonsPanel);
        rightSplitPane.setBottomComponent(studentsPanel);
        rightSplitPane.setDividerLocation(300);
        mainSplitPane.setRightComponent(rightSplitPane);
        mainSplitPane.setDividerLocation(400);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Event Handlers
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        
        coursesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleCourseSelection();
            }
        });
    }
    
    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Table
        String[] columns = {"Course ID", "Title", "Description"};
        coursesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        coursesTable = new JTable(coursesModel);
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        createCourseButton = new JButton("Create New Course");
        editCourseButton = new JButton("Edit Course");
        deleteCourseButton = new JButton("Delete Course");
        
        createCourseButton.addActionListener(e -> handleCreateCourse());
        editCourseButton.addActionListener(e -> handleEditCourse());
        deleteCourseButton.addActionListener(e -> handleDeleteCourse());
        
        buttonPanel.add(createCourseButton);
        buttonPanel.add(editCourseButton);
        buttonPanel.add(deleteCourseButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLessonsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lessons"));
        
        // Table
        String[] columns = {"Lesson ID", "Title"};
        lessonsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lessonsTable = new JTable(lessonsModel);
        lessonsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(lessonsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        addLessonButton = new JButton("Add Lesson");
        editLessonButton = new JButton("Edit Lesson");
        deleteLessonButton = new JButton("Delete Lesson");
        
        addLessonButton.addActionListener(e -> handleAddLesson());
        editLessonButton.addActionListener(e -> handleEditLesson());
        deleteLessonButton.addActionListener(e -> handleDeleteLesson());
        
        buttonPanel.add(addLessonButton);
        buttonPanel.add(editLessonButton);
        buttonPanel.add(deleteLessonButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Enrolled Students"));
        
        // Table
        String[] columns = {"Student ID", "Username", "Email"};
        studentsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new JTable(studentsModel);
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button
        JPanel buttonPanel = new JPanel();
        viewStudentsButton = new JButton("Refresh Students");
        viewStudentsButton.addActionListener(e -> handleViewStudents());
        buttonPanel.add(viewStudentsButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadCourses() {
        coursesModel.setRowCount(0);
        List<Course> courses = courseService.getCoursesByInstructor(currentInstructor.getUserId());
        for (Course course : courses) {
            coursesModel.addRow(new Object[]{
                course.getCourseId(),
                course.getTitle(),
                course.getDescription()
            });
        }
    }
    
    private void handleCourseSelection() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow < 0) {
            selectedCourse = null;
            lessonsModel.setRowCount(0);
            studentsModel.setRowCount(0);
            return;
        }
        
        String courseId = (String) coursesModel.getValueAt(selectedRow, 0);
        selectedCourse = courseService.getCourseById(courseId);
        
        if (selectedCourse != null) {
            loadLessons();
            loadStudents();
        }
    }
    
    private void loadLessons() {
        lessonsModel.setRowCount(0);
        if (selectedCourse != null) {
            for (Lesson lesson : selectedCourse.getLessons()) {
                lessonsModel.addRow(new Object[]{
                    lesson.getLessonId(),
                    lesson.getTitle()
                });
            }
        }
    }
    
    private void loadStudents() {
        studentsModel.setRowCount(0);
        if (selectedCourse != null) {
            List<Student> students = courseService.getEnrolledStudents(selectedCourse.getCourseId());
            for (Student student : students) {
                studentsModel.addRow(new Object[]{
                    student.getUserId(),
                    student.getUsername(),
                    student.getEmail()
                });
            }
        }
    }
    
    private void handleCreateCourse() {
        JTextField titleField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(descriptionScroll, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Create New Course", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            try {
                courseService.createCourse(title, description, currentInstructor.getUserId());
                JOptionPane.showMessageDialog(this, "Course created successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourses();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleEditCourse() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JTextField titleField = new JTextField(selectedCourse.getTitle(), 20);
        JTextArea descriptionArea = new JTextArea(selectedCourse.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(descriptionScroll, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Course", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            selectedCourse.setTitle(titleField.getText().trim());
            selectedCourse.setDescription(descriptionArea.getText().trim());
            
            try {
                courseService.updateCourse(selectedCourse);
                JOptionPane.showMessageDialog(this, "Course updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourses();
                handleCourseSelection();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating course: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleDeleteCourse() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this course? This action cannot be undone.", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                courseService.deleteCourse(selectedCourse.getCourseId(), currentInstructor.getUserId());
                JOptionPane.showMessageDialog(this, "Course deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                selectedCourse = null;
                loadCourses();
                lessonsModel.setRowCount(0);
                studentsModel.setRowCount(0);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleAddLesson() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JTextField titleField = new JTextField(20);
        JTextArea contentArea = new JTextArea(5, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Content:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(contentScroll, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Lesson", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            
            try {
                courseService.addLesson(selectedCourse.getCourseId(), title, content, 
                        currentInstructor.getUserId());
                JOptionPane.showMessageDialog(this, "Lesson added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLessons();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleEditLesson() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to edit.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String lessonId = (String) lessonsModel.getValueAt(selectedRow, 0);
        Lesson lesson = selectedCourse.getLessonById(lessonId);
        
        if (lesson == null) {
            JOptionPane.showMessageDialog(this, "Lesson not found.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JTextField titleField = new JTextField(lesson.getTitle(), 20);
        JTextArea contentArea = new JTextArea(lesson.getContent(), 5, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Content:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(contentScroll, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Lesson", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                courseService.updateLesson(selectedCourse.getCourseId(), lessonId, 
                        titleField.getText().trim(), contentArea.getText().trim(), 
                        currentInstructor.getUserId());
                JOptionPane.showMessageDialog(this, "Lesson updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLessons();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleDeleteLesson() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to delete.", 
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String lessonId = (String) lessonsModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this lesson?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                courseService.deleteLesson(selectedCourse.getCourseId(), lessonId, 
                        currentInstructor.getUserId());
                JOptionPane.showMessageDialog(this, "Lesson deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLessons();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleViewStudents() {
        loadStudents();
    }
    
    private void handleLogout() {
        authService.logout();
        dispose();
        new LoginFrame().setVisible(true);
    }
}


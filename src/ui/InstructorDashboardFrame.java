package ui.instructor;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import models.Instructor;
import services.InstructorService;
import models.Course;
import ui.LoginFrame; 
import ui.components.RoundedButton; 

public class InstructorDashboardFrame extends JFrame {

    private Instructor currentInstructor;
    private InstructorService instructorService;
    private JTable coursesTable; 

    public InstructorDashboardFrame(Instructor instructor, InstructorService service) {
        this.currentInstructor = instructor;
        this.instructorService = service;
        
        setTitle("Instructor Dashboard - " + instructor.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        initComponents();
        loadCourses(); 
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Your Created Courses", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        JButton logoutButton = new RoundedButton("Logout"); 
        logoutButton.addActionListener(e -> logoutAction());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        coursesTable = new JTable(); 
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton createCourseButton = new RoundedButton("Create New Course");
        createCourseButton.addActionListener(e -> openCourseEditor(null));
        controlsPanel.add(createCourseButton);

        JButton editCourseButton = new RoundedButton("Edit Course");
        editCourseButton.addActionListener(e -> editSelectedCourse());
        controlsPanel.add(editCourseButton);
        
        JButton viewStudentsButton = new RoundedButton("View Enrolled Students");
        viewStudentsButton.addActionListener(e -> viewEnrolledStudentsAction());
        controlsPanel.add(viewStudentsButton);

        add(controlsPanel, BorderLayout.SOUTH);
    }

    private void loadCourses() {
        List<Course> courses = instructorService.getCoursesByInstructorId(currentInstructor.getUserId());
        
        String[] columnNames = {"Course ID", "Title", "Lessons", "Students"};
        Object[][] data = new Object[courses.size()][4];

        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            data[i][0] = c.getCourseId();
            data[i][1] = c.getTitle();
            data[i][2] = c.getLessons().size();
            data[i][3] = c.getStudents().size();
        }

        coursesTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    private void openCourseEditor(Course course) {
        this.dispose();
        new CourseEditorFrame(currentInstructor, instructorService, course);
    }

    private void editSelectedCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to edit.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseId = (String) coursesTable.getValueAt(selectedRow, 0);
        
        Course selectedCourse = instructorService.dbManager.getCourseById(courseId);
        
        if (selectedCourse != null) {
            openCourseEditor(selectedCourse);
        } else {
            JOptionPane.showMessageDialog(this, "Course not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewEnrolledStudentsAction() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to view students.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseId = (String) coursesTable.getValueAt(selectedRow, 0);
        String courseTitle = (String) coursesTable.getValueAt(selectedRow, 1);
        
        List<String> studentIds = instructorService.getEnrolledStudents(courseId);
        
        String studentList = studentIds.isEmpty() ? "No students enrolled yet." : String.join("\n", studentIds);

        JOptionPane.showMessageDialog(this, 
            "Students in " + courseTitle + ":\n\n" + studentList, 
            "Enrolled Students", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void logoutAction() {
        new LoginFrame();
        this.dispose();
    }
}
package ui;

import database.JsonDatabaseManager;
import model.User;
import service.AuthenticationService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Signup Frame for user registration
 */
public class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton signupButton;
    private JButton cancelButton;
    private AuthenticationService authService;
    private JsonDatabaseManager dbManager;
    
    public SignupFrame(AuthenticationService authService, JsonDatabaseManager dbManager) {
        this.authService = authService;
        this.dbManager = dbManager;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Course Management System - Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("Create New Account", SwingConstants.CENTER));
        titlePanel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Role
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        roleComboBox = new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR"});
        formPanel.add(roleComboBox, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        signupButton = new JButton("Sign Up");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);
        
        // Add components
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event Handlers
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void handleSignup() {
        String role = (String) roleComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!authService.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            User user = authService.signup(role, username, email, password);
            JOptionPane.showMessageDialog(this, 
                    "Account created successfully! Please login.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                    "Signup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
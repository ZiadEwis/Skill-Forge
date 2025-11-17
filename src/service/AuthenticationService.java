package service;

import database.JsonDatabaseManager;
import model.User;
import model.Student;
import model.Instructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service for user authentication and management
 */
public class AuthenticationService {
    private final JsonDatabaseManager dbManager;
    private User currentUser;
    
    public AuthenticationService(JsonDatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.currentUser = null;
    }
    
    /**
     * Hash password using SHA-256
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    /**
     * Validate required fields
     */
    public boolean validateSignupFields(String username, String email, String password) {
        return username != null && !username.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               password != null && password.length() >= 6 &&
               isValidEmail(email);
    }
    
    /**
     * Sign up a new user
     */
    public User signup(String role, String username, String email, String password) throws IllegalArgumentException {
        if (!validateSignupFields(username, email, password)) {
            throw new IllegalArgumentException("Invalid signup fields. Email must be valid and password must be at least 6 characters.");
        }
        
        if (dbManager.emailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        String userId = UUID.randomUUID().toString();
        String passwordHash = hashPassword(password);
        
        User user;
        if ("STUDENT".equals(role)) {
            user = new Student(userId, username, email, passwordHash);
        } else if ("INSTRUCTOR".equals(role)) {
            user = new Instructor(userId, username, email, passwordHash);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
        
        dbManager.addUser(user);
        return user;
    }
    
    /**
     * Login user
     */
    public User login(String email, String password) throws IllegalArgumentException {
        if (email == null || email.trim().isEmpty() || password == null) {
            throw new IllegalArgumentException("Email and password are required");
        }
        
        User user = dbManager.getUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        String passwordHash = hashPassword(password);
        if (!user.getPasswordHash().equals(passwordHash)) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        this.currentUser = user;
        return user;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Get current logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get user role
     */
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
}


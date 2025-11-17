package models;

import java.util.List;
import java.util.ArrayList;

public class Instructor extends User {
    private List<String> createdCourses; 

    public Instructor(String userId, String username, String email, String passwordHash) {
        super(userId, "Instructor", username, email, passwordHash); 
        this.createdCourses = new ArrayList<>();
    }
    
    public Instructor() {
        super();
        this.createdCourses = new ArrayList<>();
    }

    public List<String> getCreatedCourses() {
        return createdCourses;
    }

    public void setCreatedCourses(List<String> createdCourses) {
        this.createdCourses = createdCourses;
    }

    public void addCreatedCourse(String courseId) {
        if (courseId != null && !this.createdCourses.contains(courseId)) {
            this.createdCourses.add(courseId);
        }
    }
}
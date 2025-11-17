/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User {
    private List<String> enrolledCourses;
    private Map<String, List<String>> progress; // courseId -> list of completed lessonIds
    
    public Student() {
        super();
        this.enrolledCourses = new ArrayList<>();
        this.progress = new HashMap<>();
        this.role = "STUDENT";
    }
    
    public Student(String userId, String username, String email, String passwordHash) {
        super(userId, "STUDENT", username, email, passwordHash);
        this.enrolledCourses = new ArrayList<>();
        this.progress = new HashMap<>();
    }
    
    public List<String> getEnrolledCourses() {
        return enrolledCourses;
    }
    
    public void setEnrolledCourses(List<String> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
    
    public Map<String, List<String>> getProgress() {
        return progress;
    }
    
    public void setProgress(Map<String, List<String>> progress) {
        this.progress = progress;
    }
    
    public void enrollCourse(String courseId) {
        if (!enrolledCourses.contains(courseId)) {
            enrolledCourses.add(courseId);
            if (!progress.containsKey(courseId)) {
                progress.put(courseId, new ArrayList<>());
            }
        }
    }
    
    public void markLessonCompleted(String courseId, String lessonId) {
        if (!progress.containsKey(courseId)) {
            progress.put(courseId, new ArrayList<>());
        }
        List<String> completedLessons = progress.get(courseId);
        if (!completedLessons.contains(lessonId)) {
            completedLessons.add(lessonId);
        }
    }
    
    public boolean isLessonCompleted(String courseId, String lessonId) {
        if (!progress.containsKey(courseId)) {
            return false;
        }
        return progress.get(courseId).contains(lessonId);
    }
}
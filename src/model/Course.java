package models;

import java.util.List;
import java.util.ArrayList;

public class Course {
    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private List<Lesson> lessons; 
    private List<String> students; 

    public Course(String courseId, String title, String description, String instructorId) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public Course() {
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    // Getters
    public String getCourseId() {
        return courseId;
    }
    
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public List<String> getStudents() {
        return students;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
    
    public void setStudents(List<String> students) {
        this.students = students;
    }

    // Helper Methods
    public void addStudent(String studentId) {
        if (studentId != null && !students.contains(studentId)) {
            students.add(studentId);
        }
    }
    
    public void addLesson(Lesson lesson) {
        if (lesson != null) {
            this.lessons.add(lesson);
        }
    }
    
    public boolean removeLesson(String lessonId) {
        return this.lessons.removeIf(lesson -> lesson.getLessonId().equals(lessonId));
    }
}
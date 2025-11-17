package service;

import database.JsonDatabaseManager;
import model.Course;
import model.Lesson;
import model.Student;
import model.Instructor;
import model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for course and lesson management
 */
public class CourseService {
    private final JsonDatabaseManager dbManager;
    
    public CourseService(JsonDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    
    public Course createCourse(String title, String description, String instructorId) throws IllegalArgumentException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title is required");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Course description is required");
        }
        
        String courseId = UUID.randomUUID().toString();
        Course course = new Course(courseId, title, description, instructorId);
        
        dbManager.addCourse(course);
        
        User user = dbManager.getUserById(instructorId);
        if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            instructor.addCourse(courseId);
            dbManager.updateUser(instructor);
        }
        
        return course;
    }
    
    
    public void updateCourse(Course course) {
        dbManager.updateCourse(course);
    }
    
    
    public void deleteCourse(String courseId, String instructorId) throws IllegalArgumentException {
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        if (!course.getInstructorId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can delete this course");
        }
        
        User user = dbManager.getUserById(instructorId);
        if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            instructor.removeCourse(courseId);
            dbManager.updateUser(instructor);
        }
        
        for (String studentId : course.getStudents()) {
            User studentUser = dbManager.getUserById(studentId);
            if (studentUser instanceof Student) {
                Student student = (Student) studentUser;
                student.getEnrolledCourses().remove(courseId);
                student.getProgress().remove(courseId);
                dbManager.updateUser(student);
            }
        }
        
        dbManager.deleteCourse(courseId);
    }
    
    
    public void addLesson(String courseId, String title, String content, String instructorId) throws IllegalArgumentException {
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        if (!course.getInstructorId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can add lessons");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Lesson title is required");
        }
        
        String lessonId = UUID.randomUUID().toString();
        Lesson lesson = new Lesson(lessonId, title, content);
        course.addLesson(lesson);
        dbManager.updateCourse(course);
    }
    
    
    public void updateLesson(String courseId, String lessonId, String title, String content, String instructorId) throws IllegalArgumentException {
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        if (!course.getInstructorId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can update lessons");
        }
        
        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson not found");
        }
        
        if (title != null && !title.trim().isEmpty()) {
            lesson.setTitle(title);
        }
        if (content != null) {
            lesson.setContent(content);
        }
        
        dbManager.updateCourse(course);
    }
    
    
    public void deleteLesson(String courseId, String lessonId, String instructorId) throws IllegalArgumentException {
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        if (!course.getInstructorId().equals(instructorId)) {
            throw new IllegalArgumentException("Only the course instructor can delete lessons");
        }
        
        course.removeLesson(lessonId);
        
        for (String studentId : course.getStudents()) {
            User studentUser = dbManager.getUserById(studentId);
            if (studentUser instanceof Student) {
                Student student = (Student) studentUser;
                if (student.getProgress().containsKey(courseId)) {
                    student.getProgress().get(courseId).remove(lessonId);
                    dbManager.updateUser(student);
                }
            }
        }
        
        dbManager.updateCourse(course);
    }
    
    
    public List<Course> getAllCourses() {
        return dbManager.loadCourses();
    }
    
    
    public Course getCourseById(String courseId) {
        return dbManager.getCourseById(courseId);
    }
    
    
    public List<Course> getCoursesByInstructor(String instructorId) {
        List<Course> allCourses = dbManager.loadCourses();
        List<Course> instructorCourses = new ArrayList<>();
        for (Course course : allCourses) {
            if (course.getInstructorId().equals(instructorId)) {
                instructorCourses.add(course);
            }
        }
        return instructorCourses;
    }
    
    
    public List<Student> getEnrolledStudents(String courseId) {
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            return new ArrayList<>();
        }
        
        List<Student> students = new ArrayList<>();
        for (String studentId : course.getStudents()) {
            User user = dbManager.getUserById(studentId);
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        return students;
    }
    
    
    public void enrollStudent(String courseId, String studentId) throws IllegalArgumentException {
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        
        User user = dbManager.getUserById(studentId);
        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("User is not a student");
        }
        
        Student student = (Student) user;
        if (student.getEnrolledCourses().contains(courseId)) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }
        
        student.enrollCourse(courseId);
        course.enrollStudent(studentId);
        
        dbManager.updateUser(student);
        dbManager.updateCourse(course);
    }
    
    
    public List<Lesson> getLessonsByCourse(String courseId) {
        Course course = dbManager.getCourseById(courseId);
        return course != null ? course.getLessons() : new ArrayList<>();
    }
    
    
    public void markLessonCompleted(String courseId, String lessonId, String studentId) throws IllegalArgumentException {
        Course course = dbManager.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        
        if (course.getLessonById(lessonId) == null) {
            throw new IllegalArgumentException("Lesson not found");
        }
        
        User user = dbManager.getUserById(studentId);
        if (!(user instanceof Student)) {
            throw new IllegalArgumentException("User is not a student");
        }
        
        Student student = (Student) user;
        if (!student.getEnrolledCourses().contains(courseId)) {
            throw new IllegalArgumentException("Student is not enrolled in this course");
        }
        
        student.markLessonCompleted(courseId, lessonId);
        dbManager.updateUser(student);
    }
    
    
    public List<Course> getAvailableCourses(String studentId) {
        List<Course> allCourses = dbManager.loadCourses();
        User user = dbManager.getUserById(studentId);
        
        if (!(user instanceof Student)) {
            return allCourses;
        }
        
        Student student = (Student) user;
        List<Course> availableCourses = new ArrayList<>();
        for (Course course : allCourses) {
            if (!student.getEnrolledCourses().contains(course.getCourseId())) {
                availableCourses.add(course);
            }
        }
        return availableCourses;
    }
}


package database;

import model.Course;
import model.Lesson;
import model.Student;
import model.Instructor;
import model.User;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonDatabaseManager {
    private static final String USERS_FILE = "users.json";
    private static final String COURSES_FILE = "courses.json";

    public JsonDatabaseManager() {
        initializeFiles();
    }

    private void initializeFiles() {
        try {
            File usersFile = new File(USERS_FILE);
            File coursesFile = new File(COURSES_FILE);

            if (!usersFile.exists()) {
                usersFile.createNewFile();
                saveUsers(new ArrayList<>());
            }

            if (!coursesFile.exists()) {
                coursesFile.createNewFile();
                saveCourses(new ArrayList<>());
            }
        } catch (IOException e) {
            System.err.println("Error initializing database files: " + e.getMessage());
        }
    }

    private String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (FileReader reader = new FileReader(filename)) {
            int ch;
            while ((ch = reader.read()) != -1) {
                content.append((char) ch);
            }
        }
        return content.toString();
    }

    // User Management
    public List<User> loadUsers() {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            String json = readFile(USERS_FILE);
            List<Map<String, Object>> userMaps = SimpleJsonHandler.parseJsonArray(json);

            List<User> users = new ArrayList<>();
            for (Map<String, Object> userMap : userMaps) {
                String role = (String) userMap.get("role");
                if ("STUDENT".equals(role)) {
                    Student student = new Student();
                    student.setUserId((String) userMap.get("userId"));
                    student.setRole((String) userMap.get("role"));
                    student.setUsername((String) userMap.get("username"));
                    student.setEmail((String) userMap.get("email"));
                    student.setPasswordHash((String) userMap.get("passwordHash"));
                    if (userMap.containsKey("enrolledCourses")) {
                        @SuppressWarnings("unchecked")
                        List<String> courses = (List<String>) userMap.get("enrolledCourses");
                        student.setEnrolledCourses(courses != null ? courses : new ArrayList<>());
                    }
                    if (userMap.containsKey("progress")) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> progress = (Map<String, List<String>>) userMap.get("progress");
                        student.setProgress(progress != null ? progress : new HashMap<>());
                    }
                    users.add(student);
                } else if ("INSTRUCTOR".equals(role)) {
                    Instructor instructor = new Instructor();
                    instructor.setUserId((String) userMap.get("userId"));
                    instructor.setRole((String) userMap.get("role"));
                    instructor.setUsername((String) userMap.get("username"));
                    instructor.setEmail((String) userMap.get("email"));
                    instructor.setPasswordHash((String) userMap.get("passwordHash"));
                    if (userMap.containsKey("createdCourses")) {
                        @SuppressWarnings("unchecked")
                        List<String> courses = (List<String>) userMap.get("createdCourses");
                        instructor.setCreatedCourses(courses != null ? courses : new ArrayList<>());
                    }
                    users.add(instructor);
                }
            }
            return users;
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveUsers(List<User> users) {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            writer.write(SimpleJsonHandler.toJsonUsers(users));
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public boolean userExists(String userId) {
        List<User> users = loadUsers();
        return users.stream().anyMatch(u -> u.getUserId().equals(userId));
    }

    public boolean emailExists(String email) {
        List<User> users = loadUsers();
        return users.stream().anyMatch(u -> u.getEmail().equals(email));
    }

    public void addUser(User user) throws IllegalArgumentException {
        if (userExists(user.getUserId())) {
            throw new IllegalArgumentException("User ID already exists: " + user.getUserId());
        }
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    public void updateUser(User updatedUser) {
        List<User> users = loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(updatedUser.getUserId())) {
                users.set(i, updatedUser);
                break;
            }
        }
        saveUsers(users);
    }

    public User getUserById(String userId) {
        List<User> users = loadUsers();
        return users.stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public User getUserByEmail(String email) {
        List<User> users = loadUsers();
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    // Course Management
    public List<Course> loadCourses() {
        try {
            File file = new File(COURSES_FILE);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }

            String json = readFile(COURSES_FILE);
            List<Map<String, Object>> courseMaps = SimpleJsonHandler.parseJsonArray(json);

            List<Course> courses = new ArrayList<>();
            for (Map<String, Object> courseMap : courseMaps) {
                Course course = new Course();
                course.setCourseId((String) courseMap.get("courseId"));
                course.setTitle((String) courseMap.get("title"));
                course.setDescription((String) courseMap.get("description"));
                course.setInstructorId((String) courseMap.get("instructorId"));

                if (courseMap.containsKey("lessons")) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> lessonMaps = (List<Map<String, Object>>) courseMap.get("lessons");
                    List<Lesson> lessons = new ArrayList<>();
                    if (lessonMaps != null) {
                        for (Map<String, Object> lessonMap : lessonMaps) {
                            Lesson lesson = new Lesson();
                            lesson.setLessonId((String) lessonMap.get("lessonId"));
                            lesson.setTitle((String) lessonMap.get("title"));
                            lesson.setContent((String) lessonMap.get("content"));
                            if (lessonMap.containsKey("resources")) {
                                @SuppressWarnings("unchecked")
                                List<String> resources = (List<String>) lessonMap.get("resources");
                                lesson.setResources(resources != null ? resources : new ArrayList<>());
                            }
                            lessons.add(lesson);
                        }
                    }
                    course.setLessons(lessons);
                }

                if (courseMap.containsKey("students")) {
                    @SuppressWarnings("unchecked")
                    List<String> students = (List<String>) courseMap.get("students");
                    course.setStudents(students != null ? students : new ArrayList<>());
                }

                courses.add(course);
            }
            return courses;
        } catch (IOException e) {
            System.err.println("Error loading courses: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveCourses(List<Course> courses) {
        try (FileWriter writer = new FileWriter(COURSES_FILE)) {
            writer.write(SimpleJsonHandler.toJsonCourses(courses));
        } catch (IOException e) {
            System.err.println("Error saving courses: " + e.getMessage());
        }
    }

    public boolean courseExists(String courseId) {
        List<Course> courses = loadCourses();
        return courses.stream().anyMatch(c -> c.getCourseId().equals(courseId));
    }

    public void addCourse(Course course) throws IllegalArgumentException {
        if (courseExists(course.getCourseId())) {
            throw new IllegalArgumentException("Course ID already exists: " + course.getCourseId());
        }

        List<Course> courses = loadCourses();
        courses.add(course);
        saveCourses(courses);
    }

    public void updateCourse(Course updatedCourse) {
        List<Course> courses = loadCourses();
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseId().equals(updatedCourse.getCourseId())) {
                courses.set(i, updatedCourse);
                break;
            }
        }
        saveCourses(courses);
    }

    public Course getCourseById(String courseId) {
        List<Course> courses = loadCourses();
        return courses.stream()
                .filter(c -> c.getCourseId().equals(courseId))
                .findFirst()
                .orElse(null);
    }

    public void deleteCourse(String courseId) {
        List<Course> courses = loadCourses();
        courses.removeIf(c -> c.getCourseId().equals(courseId));
        saveCourses(courses);
    }
}

package database;

import model.Course;
import model.Lesson;
import model.Student;
import model.Instructor;
import model.User;
import java.util.*;


public class SimpleJsonHandler {
    
    public static String escapeJson(String str) {
        if (str == null) return "null";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    public static String toJsonUsers(List<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            sb.append("  {\n");
            sb.append("    \"userId\": \"").append(escapeJson(user.getUserId())).append("\",\n");
            sb.append("    \"role\": \"").append(escapeJson(user.getRole())).append("\",\n");
            sb.append("    \"username\": \"").append(escapeJson(user.getUsername())).append("\",\n");
            sb.append("    \"email\": \"").append(escapeJson(user.getEmail())).append("\",\n");
            sb.append("    \"passwordHash\": \"").append(escapeJson(user.getPasswordHash())).append("\"");
            
            if (user instanceof Student) {
                Student student = (Student) user;
                sb.append(",\n    \"enrolledCourses\": [");
                List<String> courses = student.getEnrolledCourses();
                for (int j = 0; j < courses.size(); j++) {
                    sb.append("\"").append(escapeJson(courses.get(j))).append("\"");
                    if (j < courses.size() - 1) sb.append(", ");
                }
                sb.append("],\n    \"progress\": {");
                Map<String, List<String>> progress = student.getProgress();
                int pIdx = 0;
                for (Map.Entry<String, List<String>> entry : progress.entrySet()) {
                    sb.append("\"").append(escapeJson(entry.getKey())).append("\": [");
                    List<String> lessons = entry.getValue();
                    for (int j = 0; j < lessons.size(); j++) {
                        sb.append("\"").append(escapeJson(lessons.get(j))).append("\"");
                        if (j < lessons.size() - 1) sb.append(", ");
                    }
                    sb.append("]");
                    if (pIdx < progress.size() - 1) sb.append(", ");
                    pIdx++;
                }
                sb.append("}");
            } else if (user instanceof Instructor) {
                Instructor instructor = (Instructor) user;
                sb.append(",\n    \"createdCourses\": [");
                List<String> courses = instructor.getCreatedCourses();
                for (int j = 0; j < courses.size(); j++) {
                    sb.append("\"").append(escapeJson(courses.get(j))).append("\"");
                    if (j < courses.size() - 1) sb.append(", ");
                }
                sb.append("]");
            }
            sb.append("\n  }");
            if (i < users.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
    
    public static String toJsonCourses(List<Course> courses) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            sb.append("  {\n");
            sb.append("    \"courseId\": \"").append(escapeJson(course.getCourseId())).append("\",\n");
            sb.append("    \"title\": \"").append(escapeJson(course.getTitle())).append("\",\n");
            sb.append("    \"description\": \"").append(escapeJson(course.getDescription())).append("\",\n");
            sb.append("    \"instructorId\": \"").append(escapeJson(course.getInstructorId())).append("\",\n");
            sb.append("    \"lessons\": [\n");
            List<Lesson> lessons = course.getLessons();
            for (int j = 0; j < lessons.size(); j++) {
                Lesson lesson = lessons.get(j);
                sb.append("      {\n");
                sb.append("        \"lessonId\": \"").append(escapeJson(lesson.getLessonId())).append("\",\n");
                sb.append("        \"title\": \"").append(escapeJson(lesson.getTitle())).append("\",\n");
                sb.append("        \"content\": \"").append(escapeJson(lesson.getContent())).append("\",\n");
                sb.append("        \"resources\": [");
                List<String> resources = lesson.getResources();
                for (int k = 0; k < resources.size(); k++) {
                    sb.append("\"").append(escapeJson(resources.get(k))).append("\"");
                    if (k < resources.size() - 1) sb.append(", ");
                }
                sb.append("]\n      }");
                if (j < lessons.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("    ],\n    \"students\": [");
            List<String> students = course.getStudents();
            for (int j = 0; j < students.size(); j++) {
                sb.append("\"").append(escapeJson(students.get(j))).append("\"");
                if (j < students.size() - 1) sb.append(", ");
            }
            sb.append("]\n  }");
            if (i < courses.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
    
    // Simple JSON parser (basic implementation)
    public static List<Map<String, Object>> parseJsonArray(String json) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (json == null || json.trim().isEmpty()) return result;
        
        json = json.trim();
        if (!json.startsWith("[") || !json.endsWith("]")) return result;
        
        // Remove brackets
        json = json.substring(1, json.length() - 1).trim();
        if (json.isEmpty()) return result;
        
        // Split objects (simple approach)
        int depth = 0;
        int start = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) {
                    String objStr = json.substring(start, i + 1).trim();
                    if (!objStr.isEmpty()) {
                        result.add(parseJsonObject(objStr));
                    }
                    // Find next object start
                    while (i < json.length() - 1 && (json.charAt(i + 1) == ',' || Character.isWhitespace(json.charAt(i + 1)))) {
                        i++;
                    }
                    start = i + 1;
                }
            }
        }
        return result;
    }
    
    public static Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;
        
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) return map;
        json = json.substring(1, json.length() - 1).trim();
        
        // Simple key-value parsing
        String[] pairs = splitJsonPairs(json);
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = unescapeJson(kv[0].trim().replaceAll("^\"|\"$", ""));
                String value = kv[1].trim();
                map.put(key, parseJsonValue(value));
            }
        }
        return map;
    }
    
    private static String[] splitJsonPairs(String json) {
        List<String> pairs = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{' || c == '[') depth++;
            else if (c == '}' || c == ']') depth--;
            else if (c == ',' && depth == 0) {
                pairs.add(json.substring(start, i).trim());
                start = i + 1;
            }
        }
        if (start < json.length()) {
            pairs.add(json.substring(start).trim());
        }
        return pairs.toArray(new String[0]);
    }
    
    private static Object parseJsonValue(String value) {
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return unescapeJson(value.substring(1, value.length() - 1));
        } else if (value.startsWith("[") && value.endsWith("]")) {
            List<Object> list = new ArrayList<>();
            String content = value.substring(1, value.length() - 1).trim();
            if (!content.isEmpty()) {
                String[] items = content.split(",");
                for (String item : items) {
                    list.add(parseJsonValue(item.trim()));
                }
            }
            return list;
        } else if (value.startsWith("{") && value.endsWith("}")) {
            Map<String, Object> map = parseJsonObject(value);
            // Handle progress map specially
            Map<String, List<String>> progressMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof List) {
                    List<String> lessonList = new ArrayList<>();
                    for (Object obj : (List<?>) entry.getValue()) {
                        lessonList.add(obj.toString());
                    }
                    progressMap.put(entry.getKey(), lessonList);
                }
            }
            return progressMap.isEmpty() ? map : progressMap;
        } else if ("null".equals(value)) {
            return null;
        } else if ("true".equals(value) || "false".equals(value)) {
            return Boolean.parseBoolean(value);
        } else {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e2) {
                    return value;
                }
            }
        }
    }
    
    public static String unescapeJson(String str) {
        if (str == null) return null;
        return str.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }
}


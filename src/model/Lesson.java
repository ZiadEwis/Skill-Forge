package model;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private String lessonId;
    private String title;
    private String content;
    private List<String> resources;
    
    public Lesson() {
        this.resources = new ArrayList<>();
    }
    
    public Lesson(String lessonId, String title, String content) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.resources = new ArrayList<>();
    }
    
    public String getLessonId() {
        return lessonId;
    }
    
    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public List<String> getResources() {
        return resources;
    }
    
    public void setResources(List<String> resources) {
        this.resources = resources;
    }
    
    public void addResource(String resource) {
        if (!resources.contains(resource)) {
            resources.add(resource);
        }
    }
    
    public void removeResource(String resource) {
        resources.remove(resource);
    }
}


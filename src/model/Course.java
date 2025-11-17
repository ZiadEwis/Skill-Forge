// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package model;

import java.util.ArrayList;
import java.util.List;

public class Course {
   private String courseId;
   private String title;
   private String description;
   private String instructorId;
   private List<Lesson> lessons;
   private List<String> students;

   public Course() {
      this.lessons = new ArrayList();
      this.students = new ArrayList();
   }

   public Course(String courseId, String title, String description, String instructorId) {
      this.courseId = courseId;
      this.title = title;
      this.description = description;
      this.instructorId = instructorId;
      this.lessons = new ArrayList();
      this.students = new ArrayList();
   }

   public String getCourseId() {
      return this.courseId;
   }

   public void setCourseId(String courseId) {
      this.courseId = courseId;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return this.description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getInstructorId() {
      return this.instructorId;
   }

   public void setInstructorId(String instructorId) {
      this.instructorId = instructorId;
   }

   public List<Lesson> getLessons() {
      return this.lessons;
   }

   public void setLessons(List<Lesson> lessons) {
      this.lessons = lessons;
   }

   public List<String> getStudents() {
      return this.students;
   }

   public void setStudents(List<String> students) {
      this.students = students;
   }

   public void addLesson(Lesson lesson) {
      this.lessons.add(lesson);
   }

   public void removeLesson(String lessonId) {
      this.lessons.removeIf((lesson) -> {
         return lesson.getLessonId().equals(lessonId);
      });
   }

   public Lesson getLessonById(String lessonId) {
      return (Lesson)this.lessons.stream().filter((lesson) -> {
         return lesson.getLessonId().equals(lessonId);
      }).findFirst().orElse(null);
   }

   public void enrollStudent(String studentId) {
      if (!this.students.contains(studentId)) {
         this.students.add(studentId);
      }

   }

   public void unenrollStudent(String studentId) {
      this.students.remove(studentId);
   }
}

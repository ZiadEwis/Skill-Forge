// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package model;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
   private List<String> createdCourses = new ArrayList();

   public Instructor() {
      this.role = "INSTRUCTOR";
   }

   public Instructor(String userId, String username, String email, String passwordHash) {
      super(userId, "INSTRUCTOR", username, email, passwordHash);
   }

   public List<String> getCreatedCourses() {
      return this.createdCourses;
   }

   public void setCreatedCourses(List<String> createdCourses) {
      this.createdCourses = createdCourses;
   }

   public void addCourse(String courseId) {
      if (!this.createdCourses.contains(courseId)) {
         this.createdCourses.add(courseId);
      }

   }

   public void removeCourse(String courseId) {
      this.createdCourses.remove(courseId);
   }
}

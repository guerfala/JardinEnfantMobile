package com.example.jardinenfantmobile.Student;

import java.util.List;

public class Student {
    private String id;  // Firebase will auto-generate this if not provided
    private String firstName;
    private String lastName;
    private String birthDate;
    private String gender;
    private List<String> products; // Assuming products are a list of strings (e.g., product IDs)
    private String image; // Path to image in Firebase Storage or URL
    private String comments;
    private String courseId;  // Foreign key reference to Course
    private String parentId;  // Foreign key reference to Parent

    // No-argument constructor for Firebase
    public Student() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", gender='" + gender + '\'' +
                ", products=" + products +
                ", image='" + image + '\'' +
                ", comments='" + comments + '\'' +
                ", courseId='" + courseId + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    // Constructor with arguments
    public Student(String firstName, String lastName, String birthDate, String gender, List<String> products, String image, String comments, String courseId, String parentId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.products = products;
        this.image = image;
        this.comments = comments;
        this.courseId = courseId;
        this.parentId = parentId;
    }




}


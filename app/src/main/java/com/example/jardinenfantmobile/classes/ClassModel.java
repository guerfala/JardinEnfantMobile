package com.example.jardinenfantmobile.classes;

public class ClassModel {
    private String id;
    private String name;
    private String description;

    // Constructeurs
    public ClassModel() {
        // Firebase nécessite un constructeur vide
    }

    public ClassModel(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}


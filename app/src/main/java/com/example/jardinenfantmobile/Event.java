package com.example.jardinenfantmobile;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private String id;
    private String title;
    private String description;
    private String date;
    private String location;
    private Map<String, Boolean> registered;

    public Event() {}  // Constructeur par défaut pour Firebase

    public Event(String id, String title, String description, String date, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.registered = new HashMap<>();
    }

    // Getters et setters
}

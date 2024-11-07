package com.example.jardinenfantmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {
    private EditText titleInput, descriptionInput, dateInput, locationInput;
    private Button createEventButton;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dateInput = findViewById(R.id.dateInput);
        locationInput = findViewById(R.id.locationInput);
        createEventButton = findViewById(R.id.createEventButton);

        // Firebase Database reference
        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        // Event creation
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                String date = dateInput.getText().toString();
                String location = locationInput.getText().toString();

                if (!title.isEmpty() && !date.isEmpty()) {
                    String eventId = eventsRef.push().getKey();

                    // Create a map to store event details
                    Map<String, Object> eventDetails = new HashMap<>();
                    eventDetails.put("id", eventId);
                    eventDetails.put("title", title);
                    eventDetails.put("description", description);
                    eventDetails.put("date", date);
                    eventDetails.put("location", location);
                    eventDetails.put("registered", new HashMap<String, Boolean>());

                    // Store the event in Firebase
                    eventsRef.child(eventId).setValue(eventDetails)
                            .addOnSuccessListener(aVoid -> Toast.makeText(CreateEventActivity.this, "Événement créé avec succès", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(CreateEventActivity.this, "Échec de la création de l'événement", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(CreateEventActivity.this, "Veuillez remplir tous les champs requis", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
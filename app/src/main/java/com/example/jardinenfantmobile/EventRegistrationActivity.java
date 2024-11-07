package com.example.jardinenfantmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventRegistrationActivity extends AppCompatActivity {
    private TextView eventTitle, eventDescription, eventDate, eventLocation;
    private Button registerButton;
    private DatabaseReference eventsRef;
    private String eventId = "eventId123";  // Remplacer par l'ID de l'événement reçu
    private String parentId = "parentId123";  // Remplacer par l'ID du parent actuel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);

        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        eventDate = findViewById(R.id.eventDate);
        eventLocation = findViewById(R.id.eventLocation);
        registerButton = findViewById(R.id.registerButton);

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        // Récupération de l'événement
        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    eventTitle.setText(snapshot.child("title").getValue(String.class));
                    eventDescription.setText(snapshot.child("description").getValue(String.class));
                    eventDate.setText(snapshot.child("date").getValue(String.class));
                    eventLocation.setText(snapshot.child("location").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EventRegistrationActivity.this, "Erreur de chargement de l'événement", Toast.LENGTH_SHORT).show();
            }
        });

        // Inscription à l'événement
        registerButton.setOnClickListener(v -> {
            eventsRef.child(eventId).child("registered").child(parentId).setValue(true)
                    .addOnSuccessListener(aVoid -> Toast.makeText(EventRegistrationActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(EventRegistrationActivity.this, "Échec de l'inscription", Toast.LENGTH_SHORT).show());
        });
    }
}

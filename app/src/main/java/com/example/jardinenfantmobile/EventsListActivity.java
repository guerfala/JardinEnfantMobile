package com.example.jardinenfantmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsListActivity extends AppCompatActivity {
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventsList;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventsList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(eventsList, event -> {
            // Navigate to EventRegistrationActivity on item click
            Intent intent = new Intent(EventsListActivity.this, EventRegistrationActivity.class);
            intent.putExtra("eventId", event.getId());
            startActivity(intent);
        });
        eventsRecyclerView.setAdapter(eventsAdapter);

        // Add event button click listener to navigate to CreateEventActivity
        findViewById(R.id.createEventButton).setOnClickListener(v -> {
            Intent intent = new Intent(EventsListActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        // Load events from Firebase
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Event event = dataSnapshot.getValue(Event.class);
                    eventsList.add(event);
                }
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}

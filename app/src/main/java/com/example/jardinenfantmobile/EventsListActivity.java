package com.example.jardinenfantmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class EventsListActivity extends AppCompatActivity {
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private ArrayList<Event> eventsList;
    private DatabaseReference eventsRef;
    private static final String CHANNEL_ID = "events_channel";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        // Subscribe to the "events" topic for future FCM messages (if needed)
        FirebaseMessaging.getInstance().subscribeToTopic("events")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("EventsListActivity", "Subscribed to events topic");
                    } else {
                        Log.e("EventsListActivity", "Subscription failed");
                    }
                });

        // Set up RecyclerView for displaying events
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

        // Add button click listener to navigate to CreateEventActivity
        findViewById(R.id.createEventButton).setOnClickListener(v -> {
            Intent intent = new Intent(EventsListActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        // Load existing events and listen for new events to trigger notifications
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
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventsListActivity", "Database error: " + error.getMessage());
            }
        });

        // Add a ChildEventListener to detect new events and send notifications
        eventsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                // A new event has been added
                String title = snapshot.child("title").getValue(String.class);
                String date = snapshot.child("date").getValue(String.class);
                String location = snapshot.child("location").getValue(String.class);

                if (title != null && date != null && location != null) {
                    String message = "New event: " + title + " on " + date + " at " + location;
                    sendNotification("New Event", message);
                } else {
                    Log.e("EventsListActivity", "Event data is incomplete.");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EventsListActivity", "Database error: " + error.getMessage());
            }
        });

        // Create a notification channel for Android 8.0 and higher
        createNotificationChannel();
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("EventsListActivity", "Notification permission granted.");
            } else {
                Log.e("EventsListActivity", "Notification permission denied.");
            }
        }
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Make sure to add a logo drawable in res/drawable
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("EventsListActivity", "Notification permission not granted.");
            return;
        }

        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        // Notification channels are required for notifications on Android 8.0 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Notifications";
            String description = "Notifications for new events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}

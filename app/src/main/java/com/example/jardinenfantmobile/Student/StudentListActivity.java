package com.example.jardinenfantmobile.Student;

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
import android.widget.Toast;

import com.example.jardinenfantmobile.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminStudentsActivity extends AppCompatActivity implements StudentsAdapter.OnStudentClickListener {
    private RecyclerView studentsRecyclerView;
    private StudentsAdapter studentsAdapter;
    private ArrayList<Student> studentsList;
    private DatabaseReference studentsRef;
    private static final String CHANNEL_ID = "students_channel";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_students);

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        // Set up RecyclerView for displaying students
        studentsRecyclerView = findViewById(R.id.adminStudentsRecyclerView);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        studentsList = new ArrayList<>();
        studentsAdapter = new StudentsAdapter(studentsList, this, this, true); // isAdmin = true
        studentsRecyclerView.setAdapter(studentsAdapter);

        // Floating Action Button to add a new student
        findViewById(R.id.addStudentButton).setOnClickListener(v -> {
            Intent intent = new Intent(AdminStudentsActivity.this, CreateStudentActivity.class);
            startActivity(intent);
        });

        // Set up Firebase reference
        studentsRef = FirebaseDatabase.getInstance().getReference("students");

        // Load existing students and listen for new students to trigger notifications
        studentsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Student student = snapshot.getValue(Student.class);
                if (student != null) {
                    studentsList.add(student);
                    studentsAdapter.notifyDataSetChanged();
                    sendNotification("New Student Added", student.getFirstName() + " " + student.getLastName());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Handle updates if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String studentId = snapshot.getKey();
                if (studentId != null) {
                    for (int i = 0; i < studentsList.size(); i++) {
                        if (studentsList.get(i).getId().equals(studentId)) {
                            studentsList.remove(i);
                            studentsAdapter.notifyItemRemoved(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminStudentsActivity", "Database error: " + error.getMessage());
            }
        });

        // Create a notification channel for Android 8.0 and higher
        createNotificationChannel();
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
            Log.e("AdminStudentsActivity", "Notification permission not granted.");
            return;
        }

        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Student Notifications";
            String description = "Notifications for new students";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onStudentClick(Student student) {
        Intent intent = new Intent(AdminStudentsActivity.this, UpdateStudentActivity.class);
        intent.putExtra("studentId", student.getId());
        startActivity(intent);
    }
}

package com.example.jardinenfantmobile.Student;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.jardinenfantmobile.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentDetailsClientActivity extends AppCompatActivity {

    private TextView studentName, studentBirthDate, studentGender;
    private ImageView studentImageView;
    private Button editStudentButton, deleteStudentButton;
    private DatabaseReference studentRef;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_client_details);

        // Initialize views
        studentName = findViewById(R.id.studentName);
        studentBirthDate = findViewById(R.id.studentBirthDate);
        studentGender = findViewById(R.id.studentGender);
        studentImageView = findViewById(R.id.studentImageView);
//        editStudentButton = findViewById(R.id.editStudentButton);
//        deleteStudentButton = findViewById(R.id.deleteStudentButton);

        // Get student ID from Intent
        studentId = getIntent().getStringExtra("studentId");

        // Firebase reference for the specific student
        studentRef = FirebaseDatabase.getInstance().getReference("students").child(studentId);

        // Load student data
        loadStudentData();

        // Set up edit button
//        editStudentButton.setOnClickListener(v -> {
//            Intent intent = new Intent(StudentDetailsClientActivity.this, UpdateStudentActivity.class);
//            intent.putExtra("studentId", studentId);
//            startActivity(intent);
//        });
//
//        // Set up delete button
//        deleteStudentButton.setOnClickListener(v -> deleteStudent());
    }

    // Method to load student data from Firebase and display it
    private void loadStudentData() {
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);
                    String birthDate = snapshot.child("birthDate").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                    // Display data in TextViews and ImageView
                    studentName.setText(firstName + " " + lastName);
                    studentBirthDate.setText(birthDate);
                    studentGender.setText(gender);
                    if (imageUrl != null) {
                        Glide.with(StudentDetailsClientActivity.this).load(Uri.parse(imageUrl)).into(studentImageView);
                    } else {
                        studentImageView.setImageResource(R.drawable.default_image); // Default image placeholder
                    }
                } else {
                    Toast.makeText(StudentDetailsClientActivity.this, "Student data not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentDetailsClientActivity.this, "Failed to load student data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to delete the student data from Firebase
//    private void deleteStudent() {
//        studentRef.removeValue().addOnSuccessListener(aVoid -> {
//            Toast.makeText(StudentDetailsClientActivity.this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
//            finish(); // Close activity after deletion
//        }).addOnFailureListener(e ->
//                Toast.makeText(StudentDetailsClientActivity.this, "Failed to delete student", Toast.LENGTH_SHORT).show());
//    }
}

package com.example.jardinenfantmobile.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jardinenfantmobile.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateStudentActivity extends AppCompatActivity {
    private EditText firstNameInput, lastNameInput, birthDateInput, genderInput;
    private Button createStudentButton;
    private DatabaseReference studentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        birthDateInput = findViewById(R.id.birthDateInput);
        genderInput = findViewById(R.id.genderInput);
        createStudentButton = findViewById(R.id.createStudentButton);

        studentsRef = FirebaseDatabase.getInstance().getReference("students");

        createStudentButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String birthDate = birthDateInput.getText().toString();
            String gender = genderInput.getText().toString();

            if (!firstName.isEmpty() && !birthDate.isEmpty()) {
                String studentId = studentsRef.push().getKey();

                Map<String, Object> studentDetails = new HashMap<>();
                studentDetails.put("id", studentId);
                studentDetails.put("firstName", firstName);
                studentDetails.put("lastName", lastName);
                studentDetails.put("birthDate", birthDate);
                studentDetails.put("gender", gender);
                studentDetails.put("registered", new HashMap<String, Boolean>());

                studentsRef.child(studentId).setValue(studentDetails)
                        .addOnSuccessListener(aVoid -> Toast.makeText(CreateStudentActivity.this, "Student created successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(CreateStudentActivity.this, "Failed to create student", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(CreateStudentActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
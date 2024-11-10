package com.example.jardinenfantmobile.Student;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.jardinenfantmobile.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateStudentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText firstNameInput, lastNameInput, birthDateInput;
    private Spinner genderSpinner;
    private ImageView studentImageView;
    private Button selectImageButton, updateStudentButton;
    private Uri imageUri;
    private DatabaseReference studentRef;
    private StorageReference storageRef;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student);

        // Initialize views
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        birthDateInput = findViewById(R.id.birthDateInput);
        genderSpinner = findViewById(R.id.genderSpinner);
        studentImageView = findViewById(R.id.studentImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        updateStudentButton = findViewById(R.id.updateStudentButton);

        // Get the student ID from the intent
        studentId = getIntent().getStringExtra("studentId");

        // Firebase references
        studentRef = FirebaseDatabase.getInstance().getReference("students").child(studentId);
        storageRef = FirebaseStorage.getInstance().getReference("student_images");

        // Load existing student data
        loadStudentData();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        birthDateInput.setOnClickListener(v -> showDatePickerDialog());

        // Set up the select image button
        selectImageButton.setOnClickListener(v -> openFileChooser());

        // Set up the update student button
        updateStudentButton.setOnClickListener(v -> updateStudent());
    }

    // Opens a file chooser for selecting an image
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            studentImageView.setImageURI(imageUri);  // Display the selected image
        }
    }

    // Load existing student data into input fields
    private void loadStudentData() {
        studentRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String birthDate = dataSnapshot.child("birthDate").getValue(String.class);
                String gender = dataSnapshot.child("gender").getValue(String.class);
                String imageUrl = dataSnapshot.child("image").getValue(String.class);

                firstNameInput.setText(firstName);
                lastNameInput.setText(lastName);
                birthDateInput.setText(birthDate);
                if (gender != null) {
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) genderSpinner.getAdapter();
                    int position = adapter.getPosition(gender);
                    genderSpinner.setSelection(position);
                }

                // Load the student's image using Glide
                if (imageUrl != null) {
                    Glide.with(this).load(imageUrl).into(studentImageView);
                } else {
                    studentImageView.setImageResource(R.drawable.default_image); // Placeholder if no image
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(UpdateStudentActivity.this, "Failed to load student data", Toast.LENGTH_SHORT).show());
    }
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    birthDateInput.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    // Method to update the student in Firebase
    private void updateStudent() {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String birthDate = birthDateInput.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        // Validation for input fields
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(birthDate) || TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Map with updated fields
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", firstName);
        updates.put("lastName", lastName);
        updates.put("birthDate", birthDate);
        updates.put("gender", gender);

        if (imageUri != null) {
            // Upload the new image to Firebase Storage
            StorageReference fileRef = storageRef.child(studentId + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updates.put("image", uri.toString());
                        // Update the student in Firebase Database
                        studentRef.updateChildren(updates)
                                .addOnSuccessListener(aVoid -> Toast.makeText(UpdateStudentActivity.this, "Student updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(UpdateStudentActivity.this, "Failed to update student", Toast.LENGTH_SHORT).show());
                        finish();
                    })).addOnFailureListener(e -> Toast.makeText(UpdateStudentActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            // Update the student without a new image
            studentRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(UpdateStudentActivity.this, "Student updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(UpdateStudentActivity.this, "Failed to update student", Toast.LENGTH_SHORT).show());
            finish();
        }
    }
}

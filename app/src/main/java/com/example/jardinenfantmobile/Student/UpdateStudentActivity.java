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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.jardinenfantmobile.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateStudentActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText firstNameInput, lastNameInput, birthDateInput;
    private Spinner genderSpinner, classSpinner;
    private ImageView studentImageView;
    private Button selectImageButton, updateStudentButton;
    private Uri imageUri;
    private DatabaseReference studentRef, classesRef;
    private StorageReference storageRef;
    private String studentId, selectedClassId;
    private List<String> classNames = new ArrayList<>();
    private List<String> classIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student);

        // Initialize views
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        birthDateInput = findViewById(R.id.birthDateInput);
        genderSpinner = findViewById(R.id.genderSpinner);
        classSpinner = findViewById(R.id.classSpinner);
        studentImageView = findViewById(R.id.studentImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        updateStudentButton = findViewById(R.id.updateStudentButton);

        // Get the student ID from the intent
        studentId = getIntent().getStringExtra("studentId");

        // Firebase references
        studentRef = FirebaseDatabase.getInstance().getReference("students").child(studentId);
        classesRef = FirebaseDatabase.getInstance().getReference("classes");
        storageRef = FirebaseStorage.getInstance().getReference("student_images");

        // Load existing student data
        loadStudentData();

        // Set up gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Load classes into class spinner
        loadClasses();

        birthDateInput.setOnClickListener(v -> showDatePickerDialog());

        // Set up the select image button
        selectImageButton.setOnClickListener(v -> openFileChooser());

        // Set up the update student button
        updateStudentButton.setOnClickListener(v -> updateStudent());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            studentImageView.setImageURI(imageUri);
        }
    }

    private void loadStudentData() {
        studentRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String firstName = dataSnapshot.child("firstName").getValue(String.class);
                String lastName = dataSnapshot.child("lastName").getValue(String.class);
                String birthDate = dataSnapshot.child("birthDate").getValue(String.class);
                String gender = dataSnapshot.child("gender").getValue(String.class);
                String imageUrl = dataSnapshot.child("image").getValue(String.class);
                selectedClassId = dataSnapshot.child("classId").getValue(String.class);

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
                    studentImageView.setImageResource(R.drawable.default_image);
                }

                // Set selected class in classSpinner after loading classes
                setSelectedClassInSpinner();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(UpdateStudentActivity.this, "Failed to load student data", Toast.LENGTH_SHORT).show());
    }

    private void setSelectedClassInSpinner() {
        classesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < classIds.size(); i++) {
                    if (classIds.get(i).equals(selectedClassId)) {
                        classSpinner.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateStudentActivity.this, "Failed to load class data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadClasses() {
        classesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classNames.clear();
                classIds.clear();
                for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                    String classId = classSnapshot.getKey();
                    String className = classSnapshot.child("name").getValue(String.class);
                    if (classId != null && className != null) {
                        classIds.add(classId);
                        classNames.add(className);
                    }
                }
                ArrayAdapter<String> classAdapter = new ArrayAdapter<>(UpdateStudentActivity.this, android.R.layout.simple_spinner_item, classNames);
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classSpinner.setAdapter(classAdapter);

                // Set the selected class if it was already loaded
                setSelectedClassInSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateStudentActivity.this, "Failed to load classes", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void updateStudent() {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String birthDate = birthDateInput.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String selectedClassName = classSpinner.getSelectedItem().toString();
        int selectedClassIndex = classNames.indexOf(selectedClassName);
        selectedClassId = classIds.get(selectedClassIndex);

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(birthDate) || TextUtils.isEmpty(gender)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", firstName);
        updates.put("lastName", lastName);
        updates.put("birthDate", birthDate);
        updates.put("gender", gender);
        updates.put("classId", selectedClassId);

        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(studentId + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updates.put("image", uri.toString());
                        studentRef.updateChildren(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(UpdateStudentActivity.this, "Student updated successfully", Toast.LENGTH_SHORT).show();
                                    redirectToStudentsList();
                                })
                                .addOnFailureListener(e -> Toast.makeText(UpdateStudentActivity.this, "Failed to update student", Toast.LENGTH_SHORT).show());
                    })
            ).addOnFailureListener(e -> Toast.makeText(UpdateStudentActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            studentRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UpdateStudentActivity.this, "Student updated successfully", Toast.LENGTH_SHORT).show();
                        redirectToStudentsList();
                    })
                    .addOnFailureListener(e -> Toast.makeText(UpdateStudentActivity.this, "Failed to update student", Toast.LENGTH_SHORT).show());
        }
    }

    // Method to redirect to StudentsListActivity after successful update
    private void redirectToStudentsList() {
        Intent intent = new Intent(UpdateStudentActivity.this, StudentsListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the current activity
    }

}

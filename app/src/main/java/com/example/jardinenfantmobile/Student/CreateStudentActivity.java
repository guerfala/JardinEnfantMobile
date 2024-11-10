package com.example.jardinenfantmobile.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jardinenfantmobile.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateStudentActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText firstNameInput, lastNameInput, birthDateInput;
    private Spinner genderSpinner;
    private ImageView studentImageView;
    private Button selectImageButton, createStudentButton;
    private Uri imageUri;
    private DatabaseReference studentsRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        birthDateInput = findViewById(R.id.birthDateInput);
        genderSpinner = findViewById(R.id.genderSpinner);
        studentImageView = findViewById(R.id.studentImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        createStudentButton = findViewById(R.id.createStudentButton);

        // Set up Firebase references
        studentsRef = FirebaseDatabase.getInstance().getReference("students");
        storageRef = FirebaseStorage.getInstance().getReference("student_images");

        // Set up gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        // Set up Date Picker for birth date input
        birthDateInput.setOnClickListener(v -> showDatePickerDialog());

        // Set up select image button
        selectImageButton.setOnClickListener(v -> openFileChooser());

        // Set up create student button
        createStudentButton.setOnClickListener(v -> createStudent());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            studentImageView.setImageURI(imageUri);
        }
    }

    private void createStudent() {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String birthDate = birthDateInput.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            String studentId = studentsRef.push().getKey();
            StorageReference fileRef = storageRef.child(studentId + ".jpg");

            // Upload image to Firebase Storage
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // Create a map of student details
                        Map<String, Object> studentDetails = new HashMap<>();
                        studentDetails.put("id", studentId);
                        studentDetails.put("firstName", firstName);
                        studentDetails.put("lastName", lastName);
                        studentDetails.put("birthDate", birthDate);
                        studentDetails.put("gender", gender);
                        studentDetails.put("imageUrl", imageUrl);

                        // Save student details in Firebase Realtime Database
                        studentsRef.child(studentId).setValue(studentDetails)
                                .addOnSuccessListener(aVoid -> Toast.makeText(CreateStudentActivity.this, "Student created successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(CreateStudentActivity.this, "Failed to create student", Toast.LENGTH_SHORT).show());
                    })
            ).addOnFailureListener(e -> Toast.makeText(CreateStudentActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
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
}

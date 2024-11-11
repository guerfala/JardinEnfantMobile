package com.example.jardinenfantmobile.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jardinenfantmobile.R;
import com.example.jardinenfantmobile.user.UserProfileActivity;
import com.example.jardinenfantmobile.classes.ClassListActivity;
import com.example.jardinenfantmobile.events.AdminEventsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentsListActivity extends AppCompatActivity {
    private RecyclerView studentsRecyclerView;
    private StudentsAdapter studentsAdapter;
    private ArrayList<Student> studentsList;
    private ArrayList<Student> filteredList; // List for gender and class-based filtering
    private DatabaseReference studentsRef, usersRef, classesRef;
    private FloatingActionButton addStudentButton;
    private String userRole;
    private String userId;
    private Spinner genderFilterSpinner, classFilterSpinner;
    private ArrayList<String> classNames = new ArrayList<>();
    private ArrayList<String> classIds = new ArrayList<>(); // To store class IDs for filtering

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        studentsRef = FirebaseDatabase.getInstance().getReference("students");
        classesRef = FirebaseDatabase.getInstance().getReference("classes");

        addStudentButton = findViewById(R.id.addStudentButton);
        addStudentButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsListActivity.this, CreateStudentActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation();
        setupRecyclerView();
        setupGenderFilter(); // Initialize gender filter
        setupClassFilter(); // Initialize class filter
        fetchUserRoleAndLoadStudents();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;
            if (id == R.id.navigation_home) {
                intent = new Intent(StudentsListActivity.this, UserProfileActivity.class);
            } else if (id == R.id.navigation_events) {
                intent = new Intent(StudentsListActivity.this, AdminEventsActivity.class);
            } else if (id == R.id.navigation_students) {
                intent = new Intent(StudentsListActivity.this, StudentsListActivity.class);
            } else if (id == R.id.navigation_classes) {
                intent = new Intent(StudentsListActivity.this, ClassListActivity.class);
            }
            if (intent != null) startActivity(intent);
            return true;
        });
    }

    private void setupGenderFilter() {
        genderFilterSpinner = findViewById(R.id.genderFilterSpinner);

        // Set up the adapter with predefined gender options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderFilterSpinner.setAdapter(adapter);

        genderFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = genderFilterSpinner.getSelectedItem() != null
                        ? genderFilterSpinner.getSelectedItem().toString()
                        : "All";
                filterByClassAndGender(classFilterSpinner.getSelectedItem() != null
                        ? classFilterSpinner.getSelectedItem().toString()
                        : "All", selectedGender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterByClassAndGender(classFilterSpinner.getSelectedItem() != null
                        ? classFilterSpinner.getSelectedItem().toString()
                        : "All", "All");
            }
        });
    }

    private void setupClassFilter() {
        classFilterSpinner = findViewById(R.id.classFilterSpinner);

        // Fetch class names and IDs from Firebase
        classesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classNames.clear();
                classIds.clear();
                classNames.add("All"); // Add "All" option to show all students
                classIds.add("All"); // Corresponding ID for "All" option
                for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                    String classId = classSnapshot.getKey();
                    String className = classSnapshot.child("name").getValue(String.class);

                    if (classId != null && className != null) {
                        classIds.add(classId);
                        classNames.add(className);
                    }
                }

                // Set up the adapter with class options
                ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentsListActivity.this,
                        android.R.layout.simple_spinner_item, classNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classFilterSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentsListActivity", "Failed to load classes: " + error.getMessage());
            }
        });

        classFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedClassId = classIds.get(position); // Use the class ID directly
                filterByClassAndGender(selectedClassId, genderFilterSpinner.getSelectedItem() != null
                        ? genderFilterSpinner.getSelectedItem().toString()
                        : "All");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterByClassAndGender("All", genderFilterSpinner.getSelectedItem() != null
                        ? genderFilterSpinner.getSelectedItem().toString()
                        : "All");
            }
        });
    }

    private void filterByClassAndGender(String classId, String gender) {
        filteredList.clear();

        for (Student student : studentsList) {
            // Retrieve and handle potential null values for gender and classId
            String studentGender = student.getGender() != null ? student.getGender() : "";
            String studentClassId = student.getclassId() != null ? student.getclassId() : "";

            // Apply both filters cumulatively
            boolean matchesGender = gender.equals("All") || studentGender.equalsIgnoreCase(gender);
            boolean matchesClass = classId.equals("All") || studentClassId.equals(classId);

            // Only add the student if it matches both filters
            if (matchesGender && matchesClass) {
                filteredList.add(student);
            }
        }

        // Update the adapter with the filtered list
        studentsAdapter.updateFullList(filteredList);
        studentsAdapter.notifyDataSetChanged();
    }


    private void setupRecyclerView() {
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsList = new ArrayList<>();
        filteredList = new ArrayList<>();
        studentsAdapter = new StudentsAdapter(filteredList, student -> {
            Intent intent = new Intent(StudentsListActivity.this, StudentDetailsActivity.class);
            intent.putExtra("studentId", student.getId());
            startActivity(intent);
        }, this);
        studentsRecyclerView.setAdapter(studentsAdapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                studentsAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                studentsAdapter.filter(newText);
                return false;
            }
        });
    }

    private void fetchUserRoleAndLoadStudents() {
        usersRef.child(userId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userRole = snapshot.getValue(String.class);
                if (userRole != null) {
                    loadStudentsBasedOnRole();
                } else {
                    Log.e("StudentsListActivity", "User role not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentsListActivity", "Failed to retrieve user role: " + error.getMessage());
            }
        });
    }

    private void loadStudentsBasedOnRole() {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    if (student != null) {
                        String parentId = student.getparent_id();
                        if ("admin".equals(userRole) || ("client".equals(userRole) && parentId != null && parentId.equals(userId))) {
                            studentsList.add(student);
                        }
                    }
                }
                // Apply the combined filter after loading the students
                filterByClassAndGender(
                        classFilterSpinner.getSelectedItem() != null ? classFilterSpinner.getSelectedItem().toString() : "All",
                        genderFilterSpinner.getSelectedItem() != null ? genderFilterSpinner.getSelectedItem().toString() : "All"
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentsListActivity", "Database error: " + error.getMessage());
            }
        });
    }
}

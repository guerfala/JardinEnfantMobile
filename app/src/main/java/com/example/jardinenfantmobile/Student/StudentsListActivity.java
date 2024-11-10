package com.example.jardinenfantmobile.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
    private DatabaseReference studentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

        // Initialize BottomNavigationView for navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                // Handle home navigation
                startActivity(new Intent(StudentsListActivity.this, UserProfileActivity.class));
                return true;
            } else if (id == R.id.navigation_events) {
                // Handle events navigation
                startActivity(new Intent(StudentsListActivity.this, AdminEventsActivity.class));
                return true;
            } else if (id == R.id.navigation_students) {
                startActivity(new Intent(StudentsListActivity.this, StudentsListActivity.class));
                return true;
            } else if (id == R.id.navigation_classes) {
                startActivity(new Intent(StudentsListActivity.this, ClassListActivity.class));
                return true;
            }
            return false;
        });

        // Initialize RecyclerView for displaying students
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        studentsList = new ArrayList<>();
        studentsAdapter = new StudentsAdapter(studentsList, student -> {
            // Navigate to StudentDetailsActivity to view student details
            Intent intent = new Intent(StudentsListActivity.this, StudentDetailsActivity.class);
            intent.putExtra("studentId", student.getId());
            startActivity(intent);
        }, this);
        studentsRecyclerView.setAdapter(studentsAdapter);

        // Initialize the SearchView for filtering
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

        // Set up Firebase reference
        studentsRef = FirebaseDatabase.getInstance().getReference("students");

        // Load existing students from Firebase Realtime Database
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    if (student != null) {
                        studentsList.add(student);
                    }
                }
                studentsAdapter.updateFullList(new ArrayList<>(studentsList)); // Update the fullList in the adapter
                studentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentsListActivity", "Database error: " + error.getMessage());
            }
        });
    }
}

package com.example.jardinenfantmobile.Student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private DatabaseReference studentsRef;
    private FloatingActionButton addStudentButton;
    private String userRole;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

        // Retrieve the role and user ID passed from LoginActivity
        userRole = getIntent().getStringExtra("role");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize addStudentButton (visible for both roles)
        addStudentButton = findViewById(R.id.addStudentButton);
        addStudentButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsListActivity.this, CreateStudentActivity.class);
            startActivity(intent);
        });

        // Initialize BottomNavigationView for navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                startActivity(new Intent(StudentsListActivity.this, UserProfileActivity.class));
                return true;
            } else if (id == R.id.navigation_events) {
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

        // Load students based on user role
        loadStudentsBasedOnRole();
    }

    private void loadStudentsBasedOnRole() {
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentsList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Student student = dataSnapshot.getValue(Student.class);

                    if (student != null) {
                        if ("client".equals(userRole) && student.getparent_id() != null && student.getparent_id().equals(userId)) {
                            // Admin should see all students without filtering
                            studentsList.add(student);
                        } else  {
                            // Client sees only their related students
                            studentsList.add(student);
                        }
                    }
                }
                studentsAdapter.updateFullList(new ArrayList<>(studentsList)); // Update the fullList in the adapter
                studentsAdapter.notifyDataSetChanged();

                // Log result to verify data loading based on role
                Log.d("StudentsListActivity", "Loaded students: " + studentsList.size() + " for role: " + userRole);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentsListActivity", "Database error: " + error.getMessage());
            }
        });
    }


}

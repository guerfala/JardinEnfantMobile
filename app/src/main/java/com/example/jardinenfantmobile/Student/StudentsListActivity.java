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
    private DatabaseReference studentsRef, usersRef;
    private FloatingActionButton addStudentButton;
    private String userRole;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Registered Users");
        studentsRef = FirebaseDatabase.getInstance().getReference("students");

        addStudentButton = findViewById(R.id.addStudentButton);
        addStudentButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentsListActivity.this, CreateStudentActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation();
        setupRecyclerView();
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

    private void setupRecyclerView() {
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentsList = new ArrayList<>();
        studentsAdapter = new StudentsAdapter(studentsList, student -> {
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
                        if ("admin".equals(userRole) ||
                                ("client".equals(userRole) && student.getparent_id() != null && student.getparent_id().equals(userId))) {
                            studentsList.add(student);
                        }
                    }
                }
                studentsAdapter.updateFullList(new ArrayList<>(studentsList));
                studentsAdapter.notifyDataSetChanged();
                Log.d("StudentsListActivity", "Loaded students: " + studentsList.size() + " for role: " + userRole);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("StudentsListActivity", "Database error: " + error.getMessage());
            }
        });
    }
}

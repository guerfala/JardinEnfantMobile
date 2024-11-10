package com.example.jardinenfantmobile.classes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jardinenfantmobile.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClassListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClassAdapter classAdapter;
    private List<ClassModel> classList;
    private DatabaseReference databaseReference;
    private Button btnAddClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        recyclerView = findViewById(R.id.recyclerView_classes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        classList = new ArrayList<>();
        classAdapter = new ClassAdapter(this, classList); // Correction ici en ajoutant le contexte `this`
        recyclerView.setAdapter(classAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("classes");

        btnAddClass = findViewById(R.id.btnAddClass);
        btnAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClassListActivity.this, AddClassActivity.class));
            }
        });

        // Charger et écouter les données Firebase en temps réel
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                classList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ClassModel classModel = snapshot.getValue(ClassModel.class);
                    classList.add(classModel);
                }
                classAdapter.notifyDataSetChanged(); // Actualiser l'affichage
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ClassListActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

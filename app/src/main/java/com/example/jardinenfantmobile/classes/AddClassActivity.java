package com.example.jardinenfantmobile.classes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jardinenfantmobile.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddClassActivity extends AppCompatActivity {

    private EditText editTextClassName, editTextClassDescription;
    private Button buttonAddClass;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        // Vérifier si l'utilisateur est connecté
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Veuillez vous connecter pour ajouter une classe.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialiser les vues
        editTextClassName = findViewById(R.id.editTextClassName);
        editTextClassDescription = findViewById(R.id.editTextClassDescription);
        buttonAddClass = findViewById(R.id.buttonAddClass);

        // Initialiser la référence Firebase pour la base de données des classes
        databaseReference = FirebaseDatabase.getInstance().getReference("classes");

        // Ajouter un écouteur pour le bouton
        buttonAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextClassName.getText().toString();
                String description = editTextClassDescription.getText().toString();

                if (!name.isEmpty() && !description.isEmpty()) {
                    // Enregistrer la nouvelle classe dans Firebase
                    String id = databaseReference.push().getKey(); // Générer un ID unique pour chaque classe
                    ClassModel newClass = new ClassModel(id, name, description);

                    databaseReference.child(id).setValue(newClass)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AddClassActivity.this, "Classe ajoutée avec succès", Toast.LENGTH_SHORT).show();
                                finish(); // Fermer l'activité après l'ajout
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddClassActivity.this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(AddClassActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

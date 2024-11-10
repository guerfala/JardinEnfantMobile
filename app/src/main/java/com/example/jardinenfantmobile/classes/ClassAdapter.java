package com.example.jardinenfantmobile.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jardinenfantmobile.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<ClassModel> classList;
    private Context context;

    public ClassAdapter(Context context, List<ClassModel> classList) {
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassModel classModel = classList.get(position);
        holder.textViewName.setText(classModel.getName());
        holder.textViewDescription.setText(classModel.getDescription());

        // Icône de suppression avec message de confirmation
        holder.iconDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer cette classe ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // Supprimer la classe de Firebase
                        DatabaseReference classRef = FirebaseDatabase.getInstance()
                                .getReference("classes")
                                .child(classModel.getId());
                        classRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Classe supprimée avec succès", Toast.LENGTH_SHORT).show();
                                    classList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged(); // Synchronise l'affichage
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        // Icône de modification
        holder.iconEdit.setOnClickListener(v -> {
            if (context instanceof android.app.Activity) {  // Vérifie si le contexte est une activité
                Intent intent = new Intent(context, EditClassActivity.class);
                intent.putExtra("classId", classModel.getId());
                intent.putExtra("className", classModel.getName());
                intent.putExtra("classDescription", classModel.getDescription());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Erreur : Impossible d'ouvrir l'activité de modification", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDescription;
        ImageView iconEdit, iconDelete;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            iconEdit = itemView.findViewById(R.id.iconEdit);
            iconDelete = itemView.findViewById(R.id.iconDelete);
        }
    }
}

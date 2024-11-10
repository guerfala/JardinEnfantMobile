package com.example.jardinenfantmobile.Student;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jardinenfantmobile.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {
    private ArrayList<Student> studentsList;
    private OnStudentClickListener listener;
    private Context context;
    private boolean isAdmin; // Flag to indicate if the adapter is for admin or client

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
    }

    public StudentsAdapter(ArrayList<Student> studentsList, OnStudentClickListener listener, Context context, boolean isAdmin) {
        this.studentsList = studentsList;
        this.listener = listener;
        this.context = context;
        this.isAdmin = isAdmin; // Set the flag based on activity type
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentsList.get(position);
        holder.studentName.setText(student.getFirstName() + " " + student.getLastName());
        holder.itemView.setOnClickListener(v -> listener.onStudentClick(student));

        // Show/hide edit and delete buttons based on isAdmin flag
        if (isAdmin) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            // Edit button logic
            holder.editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, UpdateStudentActivity.class);
                intent.putExtra("studentId", student.getId());
                context.startActivity(intent);
            });

            // Delete button logic
            holder.deleteButton.setOnClickListener(v -> {
                DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("students").child(student.getId());
                studentRef.removeValue().addOnSuccessListener(aVoid -> {
                    // Find the index of the student by matching the student ID
                    int index = -1;
                    for (int i = 0; i < studentsList.size(); i++) {
                        if (studentsList.get(i).getId().equals(student.getId())) {
                            index = i;
                            break;
                        }
                    }

                    // If the student was found, remove it safely
                    if (index != -1) {
                        studentsList.remove(index);
                        notifyItemRemoved(index);
                        notifyItemRangeChanged(index, studentsList.size());
                        Toast.makeText(context, "Student deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Student deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(context, "Failed to delete student", Toast.LENGTH_SHORT).show());
            });
        } else {
            // Hide buttons for non-admin users
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        Button editButton, deleteButton;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}

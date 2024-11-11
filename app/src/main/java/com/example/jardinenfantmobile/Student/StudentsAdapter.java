package com.example.jardinenfantmobile.Student;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jardinenfantmobile.R;

import java.util.ArrayList;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {

    private ArrayList<Student> studentsList;
    private ArrayList<Student> fullList;  // Original list for restoring data
    private final OnStudentClickListener listener;
    private final Context context;

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
    }

    public StudentsAdapter(ArrayList<Student> studentsList, OnStudentClickListener listener, Context context) {
        this.studentsList = studentsList;
        this.fullList = new ArrayList<>(studentsList);  // Initialize full list
        this.listener = listener;
        this.context = context;
    }

    // Method to update the full list
    public void updateFullList(ArrayList<Student> newFullList) {
        this.fullList.clear();
        this.fullList.addAll(newFullList);
    }
    
    // Method to filter the student list based on a query
    public void filter(String query) {
        if (query.isEmpty()) {
            studentsList.clear();
            studentsList.addAll(fullList);  // Restore the full list if the query is empty
        } else {
            ArrayList<Student> filteredList = new ArrayList<>();
            for (Student student : fullList) {
                String fullName = (student.getFirstName() + " " + student.getLastName()).toLowerCase();
                if (fullName.contains(query.toLowerCase())) {
                    filteredList.add(student);
                }
            }
            studentsList.clear();
            studentsList.addAll(filteredList);
        }
        notifyDataSetChanged();
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

        // Bind student attributes to the views
        holder.studentName.setText(student.getFirstName() + " " + student.getLastName());
        holder.studentBirthDate.setText(student.getBirthDate());
//        holder.studentGender.setText(student.getGender());

        // Load student image with Glide, using a default image if the URL is null
        if (student.getImage() != null) {
            Glide.with(context).load(student.getImage()).into(holder.studentImageView);
        } else {
            holder.studentImageView.setImageResource(R.drawable.default_image); // Placeholder image
        }

        // Set up click listener for each student item
        holder.itemView.setOnClickListener(v -> listener.onStudentClick(student));
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    // ViewHolder class to hold references to each item's views
    static class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView studentImageView;
        TextView studentName, studentBirthDate;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentImageView = itemView.findViewById(R.id.studentImageView);
            studentName = itemView.findViewById(R.id.studentName);
            studentBirthDate = itemView.findViewById(R.id.studentBirthDate);
//            studentGender = itemView.findViewById(R.id.studentGender);
        }
    }
}

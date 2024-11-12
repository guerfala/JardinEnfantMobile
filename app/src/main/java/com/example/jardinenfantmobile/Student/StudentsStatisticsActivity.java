package com.example.jardinenfantmobile.Student;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.jardinenfantmobile.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;

public class StudentsStatisticsActivity extends AppCompatActivity {

    private BarChart classBarChart;
    private PieChart genderPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_statistics); // Use your actual layout file

        classBarChart = findViewById(R.id.classBarChart);
        genderPieChart = findViewById(R.id.genderPieChart);

        // Populate with data (replace with actual student data)
        populateClassBarChart();
        populateGenderPieChart();
    }

    private void populateClassBarChart() {
        // Sample data: Class names with student counts
        String[] classNames = {"Class A", "Class B", "Class C"};
        int[] studentCounts = {10, 15, 7};

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < classNames.length; i++) {
            entries.add(new BarEntry(i, studentCounts[i]));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Students per Class");
        barDataSet.setColor(Color.BLUE);

        BarData barData = new BarData(barDataSet);
        classBarChart.setData(barData);

        classBarChart.getDescription().setEnabled(false); // Optional: Hide description
        classBarChart.invalidate(); // Refresh chart
    }

    private void populateGenderPieChart() {
        // Sample data: Gender distribution
        String[] genderCategories = {"Boys", "Girls"};
        int[] genderCounts = {20, 25};

        List<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < genderCategories.length; i++) {
            entries.add(new PieEntry(genderCounts[i], genderCategories[i]));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "Gender Distribution");
        pieDataSet.setColors(Color.MAGENTA, Color.CYAN);

        PieData pieData = new PieData(pieDataSet);
        genderPieChart.setData(pieData);

        genderPieChart.getDescription().setEnabled(false); // Optional: Hide description
        genderPieChart.invalidate(); // Refresh chart
    }
}

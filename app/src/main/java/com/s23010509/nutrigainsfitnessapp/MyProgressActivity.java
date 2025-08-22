package com.s23010509.nutrigainsfitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MyProgressActivity extends AppCompatActivity {

    private EditText etWorkoutTime, etSleepTime, etCaloriesConsumed, etWaterIntake;
    private TextView tvCalorieSummary, tvDailyCalorieGoal;
    private int dailyCalorieGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprogress);


        etWorkoutTime = findViewById(R.id.etWorkoutTime);
        etSleepTime = findViewById(R.id.etSleepTime);
        etCaloriesConsumed = findViewById(R.id.etCaloriesConsumed);
        etWaterIntake = findViewById(R.id.etWaterIntake);
        tvCalorieSummary = findViewById(R.id.tvCalorieSummary);
        tvDailyCalorieGoal = findViewById(R.id.tvDailyCalorieGoal);

        Button btnSave = findViewById(R.id.btnSaveProgress);
        Button btnBack = findViewById(R.id.btnBack);

        // Load user data and calculate daily calorie goal
        loadUserData();
        calculateDailyCalorieGoal();

        btnSave.setOnClickListener(v -> saveProgressData());

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(MyProgressActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        // Load previous entries
        etWorkoutTime.setText(String.valueOf(prefs.getInt("workoutMinutes", 0)));
        etSleepTime.setText(String.valueOf(prefs.getInt("sleepHours", 0)));
        etCaloriesConsumed.setText(String.valueOf(prefs.getInt("caloriesConsumed", 0)));
        etWaterIntake.setText(String.valueOf(prefs.getInt("waterGlasses", 0))); // FIXED

        dailyCalorieGoal = prefs.getInt("dailyCalorieGoal", 2000);
    }

    private void calculateDailyCalorieGoal() {
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        // Get user profile data
        int age = prefs.getInt("age", 30);
        float weight = prefs.getFloat("weight", 70.0f);
        float height = prefs.getFloat("height", 170.0f);
        String gender = prefs.getString("gender", "Male");
        String goal = prefs.getString("goal", "Maintenance");

        // Calculate BMR (Basal Metabolic Rate)
        double bmr;
        if (gender.equals("Male")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        // Adjust for activity level (assuming moderate activity)
        double tdee = bmr * 1.55;

        // Adjust for goal
        if (goal.contains("Weight Gain")) {
            tdee += 500; // Add 500 calories for weight gain
        } else if (goal.contains("Fat Loss")) {
            tdee -= 500; // Subtract 500 calories for fat loss
        } else if (goal.contains("Build Muscles")) {
            tdee += 250; // Add 250 calories for muscle building
        }

        dailyCalorieGoal = (int) tdee;

        // Save and display goal
        prefs.edit().putInt("dailyCalorieGoal", dailyCalorieGoal).apply();
        tvDailyCalorieGoal.setText(String.format("Daily Goal: %d cal", dailyCalorieGoal));
    }

    private void saveProgressData() {
        try {
            int workoutMinutes = Integer.parseInt(etWorkoutTime.getText().toString());
            int sleepHours = Integer.parseInt(etSleepTime.getText().toString());
            int caloriesConsumed = Integer.parseInt(etCaloriesConsumed.getText().toString());
            int waterGlasses = Integer.parseInt(etWaterIntake.getText().toString()); // FIXED

            // Save to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putInt("workoutMinutes", workoutMinutes);
            editor.putInt("sleepHours", sleepHours);
            editor.putInt("caloriesConsumed", caloriesConsumed);
            editor.putInt("waterGlasses", waterGlasses);
            editor.putInt("dailyCalorieGoal", dailyCalorieGoal);

            editor.apply();

            updateUI(workoutMinutes, sleepHours, caloriesConsumed, waterGlasses);
            Toast.makeText(this, "Progress saved!", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(int workoutMinutes, int sleepHours, int caloriesConsumed, int waterGlasses) {
        int deficit = dailyCalorieGoal - caloriesConsumed;
        String summary = String.format("Daily Summary:\n" +
                        "Workout: %d min\nSleep: %d hrs\n" +
                        "Water: %d glasses\n" +
                        "Consumed: %d cal\nGoal: %d cal\n" +
                        "Deficit: %d cal",
                workoutMinutes, sleepHours, waterGlasses,
                caloriesConsumed, dailyCalorieGoal, deficit);

        tvCalorieSummary.setText(summary);
    }
}
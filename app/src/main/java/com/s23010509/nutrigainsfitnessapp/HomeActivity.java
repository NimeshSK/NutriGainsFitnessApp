package com.s23010509.nutrigainsfitnessapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    private static final int SENSOR_PERMISSION_CODE = 1;
    private static final int GOAL_STEPS = 10000;
    private static final float METERS_PER_STEP = 0.762f;
    private static final float CALORIES_PER_STEP = 0.04f;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private TextView stepCountTextView, distanceTextView, caloriesBurnedTextView;
    private TextView calorieIntakeTextView, calorieDeficitTextView, sleepTimeTextView;
    private TextView welcomeNameTextView;
    private ProgressBar progressBar;
    private int stepCount = 0;
    private float initialStepValue = 0;
    private boolean isFirstSensorReading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        stepCountTextView = findViewById(R.id.tvStepCount);
        distanceTextView = findViewById(R.id.tvDistance);
        caloriesBurnedTextView = findViewById(R.id.tvCaloriesBurned);
        calorieIntakeTextView = findViewById(R.id.tvCalorieIntake);
        calorieDeficitTextView = findViewById(R.id.tvCalorieDeficit);
        sleepTimeTextView = findViewById(R.id.tvSleepTime);
        welcomeNameTextView = findViewById(R.id.welcomeName);
        progressBar = findViewById(R.id.progressBar);


        setupButtonListeners();

        progressBar.setMax(GOAL_STEPS);

        // Load and display user data
        loadUserData();

        // Check and request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, SENSOR_PERMISSION_CODE);
        } else {
            initializeSensors();
        }
    }

    private void setupButtonListeners() {
        findViewById(R.id.btnCalculators).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, CalculatorsActivity.class));
        });

        findViewById(R.id.btnReminders).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RemindersActivity.class));
        });

        findViewById(R.id.btnMap).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, StoreLocatorActivity.class));
        });

        findViewById(R.id.btnMyProgress).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MyProgressActivity.class));
        });
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        // Check if we have user profile data
        if (!prefs.contains("age")) {
            // If no profile data, try to load from database
            loadProfileFromDatabase();
            prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        }

        // Update welcome message with user name
        String name = prefs.getString("name", "User");
        welcomeNameTextView.setText(name);

        // Load and display calorie data
        int dailyCalorieGoal = prefs.getInt("dailyCalorieGoal", 2000);
        int caloriesConsumed = prefs.getInt("caloriesConsumed", 0);
        int calorieDeficit = dailyCalorieGoal - caloriesConsumed;

        calorieIntakeTextView.setText(String.format("%d/%d ", caloriesConsumed, dailyCalorieGoal));
        calorieDeficitTextView.setText(String.format("%d Cal", calorieDeficit));

        // Load and display sleep time
        int sleepHours = prefs.getInt("sleepHours", 0);
        sleepTimeTextView.setText(String.format("%d hrs sleep", sleepHours));

        // Set initial values for step based metrics
        stepCountTextView.setText("0");
        distanceTextView.setText("Distance: 0.00 km");
        caloriesBurnedTextView.setText("Burned: 0 cal");
    }

    // Load profile from database for the logged-in user
    private void loadProfileFromDatabase() {
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String email = prefs.getString("userEmail", "");

        if (email.isEmpty()) {
            return;
        }

        DataBaseHelper dbHelper = new DataBaseHelper(this);

        int userId = dbHelper.getUserIdByEmail(email);

        if (userId == -1) {
            dbHelper.close();
            return; // User not found in database
        }

        // Get the profile for THIS specific user
        Cursor cursor = dbHelper.getProfileByUserId(userId);

        if (cursor != null && cursor.moveToFirst()) {
            SharedPreferences.Editor editor = prefs.edit();

            try {
                // Get data for THIS user
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_2));
                int age = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_3)));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_4));
                float height = Float.parseFloat(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_5)));
                float weight = Float.parseFloat(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_6)));
                String goal = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_7));


                editor.putString("name", name);
                editor.putInt("age", age);
                editor.putFloat("height", height);
                editor.putFloat("weight", weight);
                editor.putString("gender", gender);
                editor.putString("goal", goal);

                // Calculate and save daily calorie goal
                int dailyCalorieGoal = calculateDailyCalorieGoal(age, weight, height, gender, goal);
                editor.putInt("dailyCalorieGoal", dailyCalorieGoal);

                editor.apply();

            } catch (Exception e) {
                Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        }
        dbHelper.close();
    }

    private int calculateDailyCalorieGoal(int age, float weight, float height, String gender, String goal) {
        // Calculate BMR (Basal Metabolic Rate)
        double bmr;
        if (gender.equals("Male")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        // Adjust for activity level (consider moderate activity)
        double tdee = bmr * 1.55;

        if (goal.contains("Weight Gain")) {
            tdee += 500; // Add 500 calories for weight gain
        } else if (goal.contains("Fat Loss")) {
            tdee -= 500; // Subtract 500 calories for fat loss
        } else if (goal.contains("Build Muscles")) {
            tdee += 250; // Add 250 calories for muscle building
        }

        return (int) tdee;
    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }
        if (stepSensor == null) {
            Toast.makeText(this, "Step counting sensor is not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetStepCount();
        loadUserData();
        if (sensorManager != null && stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (isFirstSensorReading) {
                initialStepValue = event.values[0];
                isFirstSensorReading = false;
                return;
            }

            int currentSteps = (int) (event.values[0] - initialStepValue);
            if (currentSteps > stepCount) {
                stepCount = currentSteps;
                updateUI();
            }
        }
    }

    private void updateUI() {
        stepCountTextView.setText(String.valueOf(stepCount));

        int progress = Math.min(stepCount, GOAL_STEPS);
        progressBar.setProgress(progress);

        float distanceKm = stepCount * METERS_PER_STEP / 1000;
        distanceTextView.setText(String.format("Distance: %.2f km", distanceKm));

        float caloriesBurned = stepCount * CALORIES_PER_STEP;
        caloriesBurnedTextView.setText(String.format("Burned: %.0f cal", caloriesBurned));
    }

    private void resetStepCount() {
        stepCount = 0;
        initialStepValue = 0;
        isFirstSensorReading = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SENSOR_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSensors();
            } else {
                Toast.makeText(this, "Permission denied, step counting will not work", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
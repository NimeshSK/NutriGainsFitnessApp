package com.s23010509.nutrigainsfitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize email and password fields
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Login
        findViewById(R.id.btnGetStared).setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save email and password
            SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userEmail", email);
            editor.putString("userPassword", password);
            editor.apply();


            if (loadProfileFromDatabase(email, password)) {
                // Profile exists, go to HomeActivity
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });

        // Register to SignUp
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            // Save email and password
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userEmail", email);
                editor.putString("userPassword", password);
                editor.apply();
            }

            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    private boolean loadProfileFromDatabase(String email, String password) {
        DataBaseHelper dbHelper = new DataBaseHelper(this);

        Cursor cursor = dbHelper.getUserByEmailPassword(email, password);

        boolean hasProfile = false;

        if (cursor != null && cursor.moveToFirst()) {
            // Get the user ID from the users table
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_USER_ID));

            // get the profile for this user
            Cursor profileCursor = dbHelper.getProfileByUserId(userId);

            if (profileCursor != null && profileCursor.moveToFirst()) {
                SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                try {
                    String name = profileCursor.getString(profileCursor.getColumnIndexOrThrow(DataBaseHelper.COL_2));
                    int age = Integer.parseInt(profileCursor.getString(profileCursor.getColumnIndexOrThrow(DataBaseHelper.COL_3)));
                    String gender = profileCursor.getString(profileCursor.getColumnIndexOrThrow(DataBaseHelper.COL_4));
                    float height = Float.parseFloat(profileCursor.getString(profileCursor.getColumnIndexOrThrow(DataBaseHelper.COL_5)));
                    float weight = Float.parseFloat(profileCursor.getString(profileCursor.getColumnIndexOrThrow(DataBaseHelper.COL_6)));
                    String goal = profileCursor.getString(profileCursor.getColumnIndexOrThrow(DataBaseHelper.COL_7));

                    // Save
                    editor.putString("name", name);
                    editor.putInt("age", age);
                    editor.putFloat("height", height);
                    editor.putFloat("weight", weight);
                    editor.putString("gender", gender);
                    editor.putString("goal", goal);
                    editor.putString("userEmail", email);
                    editor.putString("userPassword", password);

                    // Calculate and save daily calorie goal
                    int dailyCalorieGoal = calculateDailyCalorieGoal(age, weight, height, gender, goal);
                    editor.putInt("dailyCalorieGoal", dailyCalorieGoal);

                    editor.apply();
                    hasProfile = true;

                    Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show();
                } finally {
                    profileCursor.close();
                }
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Account not found. Please register first.", Toast.LENGTH_SHORT).show();
        }

        dbHelper.close();
        return hasProfile;
    }

    private int calculateDailyCalorieGoal(int age, float weight, float height, String gender, String goal) {
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

        return (int) tdee;
    }
}
package com.s23010509.nutrigainsfitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateProfileActivity extends AppCompatActivity {


    //ui elements initialization
    private EditText etName, etAge, etHeight, etWeight;
    private RadioButton rbMale, rbFemale;
    private CheckBox cbWeightGain, cbBuildMuscles, cbFatLoss;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        dbHelper = new DataBaseHelper(this);

        // Initialize views
        etName = findViewById(R.id.editName);
        etAge = findViewById(R.id.editAge);
        etHeight = findViewById(R.id.editHeight);
        etWeight = findViewById(R.id.editWeight);
        rbMale = findViewById(R.id.radioBtnMale);
        rbFemale = findViewById(R.id.radioBtnFemale);
        cbWeightGain = findViewById(R.id.checkBox);
        cbBuildMuscles = findViewById(R.id.checkBox2);
        cbFatLoss = findViewById(R.id.checkBox3);

        Button btnCreate = findViewById(R.id.btnlogin);
        btnCreate.setOnClickListener(v -> saveProfileData());
    }

    private void saveProfileData() {
        // Get input values
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = "";
        if (rbMale.isChecked()) {
            gender = "Male";
        } else if (rbFemale.isChecked()) {
            gender = "Female";
        } else {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder goals = new StringBuilder();
        if (cbWeightGain.isChecked()) goals.append("Weight Gain,");
        if (cbBuildMuscles.isChecked()) goals.append("Build Muscles,");
        if (cbFatLoss.isChecked()) goals.append("Fat Loss,");
        String goal = goals.length() > 0 ? goals.substring(0, goals.length()-1) : "Maintenance";

        // Get user email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String email = prefs.getString("userEmail", "");

        if (email.isEmpty()) {
            Toast.makeText(this, "User not found. Please sign up again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
            return;
        }

        //Get user ID from email
        int userId = dbHelper.getUserIdByEmail(email);
        if (userId == -1) {
            Toast.makeText(this, "User account not found", Toast.LENGTH_SHORT).show();
            return;
        }

        //Insert profile data linked to user
        boolean isInserted = dbHelper.insertProfileData(name, ageStr, gender, heightStr, weightStr, goal, userId);

        if (isInserted) {
            try {
                int age = Integer.parseInt(ageStr);
                float height = Float.parseFloat(heightStr);
                float weight = Float.parseFloat(weightStr);

                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("name", name);
                editor.putInt("age", age);
                editor.putFloat("height", height);
                editor.putFloat("weight", weight);
                editor.putString("gender", gender);
                editor.putString("goal", goal);
                editor.putInt("userId", userId);

                //Calculate and save daily calorie goal
                int dailyCalorieGoal = calculateDailyCalorieGoal(age, weight, height, gender, goal);
                editor.putInt("dailyCalorieGoal", dailyCalorieGoal);

                editor.apply();

                Toast.makeText(this, "Profile created successfully!", Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to HomeActivity
            Intent intent = new Intent(CreateProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Profile creation failed", Toast.LENGTH_SHORT).show();
        }
    }

    private int calculateDailyCalorieGoal(int age, float weight, float height, String gender, String goal) {
        double bmr;
        if (gender.equals("Male")) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        double tdee = bmr * 1.55;

        if (goal.contains("Weight Gain")) tdee += 500;
        else if (goal.contains("Fat Loss")) tdee -= 500;
        else if (goal.contains("Build Muscles")) tdee += 250;

        return (int) tdee;
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
package com.s23010509.nutrigainsfitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateProfileActivity extends AppCompatActivity {

    private EditText etName, etAge, etHeight, etWeight;
    private RadioButton rbMale, rbFemale;
    private CheckBox cbWeightGain, cbBuildMuscles, cbFatLoss;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        // Initialize DatabaseHelper
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

        // Set click listener for Create button
        Button btnCreate = findViewById(R.id.btnlogin);
        btnCreate.setOnClickListener(v -> saveProfileData());
    }

    private void saveProfileData() {
        // Get input values
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String height = etHeight.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();

        // Handle gender selection (without RadioGroup)
        String gender = "";
        if (rbMale.isChecked()) {
            gender = "Male";
        } else if (rbFemale.isChecked()) {
            gender = "Female";
        }

        // Handle goal selections
        StringBuilder goals = new StringBuilder();
        if (cbWeightGain.isChecked()) goals.append("Weight Gain,");
        if (cbBuildMuscles.isChecked()) goals.append("Build Muscles,");
        if (cbFatLoss.isChecked()) goals.append("Fat Loss,");
        String goal = goals.length() > 0 ? goals.substring(0, goals.length()-1) : "";

        // Insert into database
        boolean isInserted = dbHelper.insertProfileData(name, age, gender, height, weight, goal);

        if (isInserted) {
            Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
            //Navigate to HomeActivity
            Intent intent = (new Intent(CreateProfileActivity.this, HomeActivity.class));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
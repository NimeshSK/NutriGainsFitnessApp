package com.s23010509.nutrigainsfitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class CalculatorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculators);

        // Back button click listener
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Go back to HomeActivity
            Intent intent = new Intent(CalculatorsActivity.this, HomeActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish();
        });

        // Initialize calculators
        setupBmiCalculator();
        setupCalorieCalculator();
    }

    private void setupBmiCalculator() {
        Button btnCalculate = findViewById(R.id.btnCalculateBmi);
        EditText etHeight = findViewById(R.id.etHeight);
        EditText etWeight = findViewById(R.id.etWeight);
        TextView tvResult = findViewById(R.id.tvBmiResult);

        btnCalculate.setOnClickListener(v -> {
            try {
                double height = Double.parseDouble(etHeight.getText().toString());
                double weight = Double.parseDouble(etWeight.getText().toString());

                // BMI formula
                double bmi = weight / Math.pow(height / 100, 2);
                String interpretation = interpretBmi(bmi);

                tvResult.setText(String.format(Locale.getDefault(),
                        "Your BMI: %.1f\n(%s)", bmi, interpretation));
            } catch (Exception e) {
                tvResult.setText("Please enter valid height and weight");
            }
        });
    }

    private String interpretBmi(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal weight";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    private void setupCalorieCalculator() {
        Button btnCalculate = findViewById(R.id.btnCalculateCalories);
        EditText etAge = findViewById(R.id.etAge);
        EditText etWeight = findViewById(R.id.etCalorieWeight);
        EditText etHeight = findViewById(R.id.etCalorieHeight);
        RadioGroup rgGender = findViewById(R.id.rgGender);
        TextView tvResult = findViewById(R.id.tvCalorieResult);

        btnCalculate.setOnClickListener(v -> {
            try {
                int age = Integer.parseInt(etAge.getText().toString());
                double weight = Double.parseDouble(etWeight.getText().toString());
                double height = Double.parseDouble(etHeight.getText().toString());
                boolean isMale = rgGender.getCheckedRadioButtonId() == R.id.rbMale;

                double maintenanceCalories;
                if (isMale) {
                    maintenanceCalories = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
                } else {
                    maintenanceCalories = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
                }

                String result = String.format(Locale.getDefault(),
                        "Daily Calorie Needs:\n\n" +
                                "Maintenance: %.0f kcal",
                        maintenanceCalories);

                tvResult.setText(result);

            } catch (Exception e) {
                tvResult.setText("Please fill all fields correctly");
            }
        });
    }
}
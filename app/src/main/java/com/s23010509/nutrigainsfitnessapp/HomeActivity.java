package com.s23010509.nutrigainsfitnessapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1. Calculators Button
        findViewById(R.id.btnCalculators).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CalculatorsActivity.class);
            startActivity(intent);
        });

        // 2. Reminders Button
        findViewById(R.id.btnReminders).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RemindersActivity.class);
            startActivity(intent);
        });

        // 3. Store Locator Button
        findViewById(R.id.btnMap).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StoreLocatorActivity.class);
            startActivity(intent);
        });

        // 4. My Progress Button
        findViewById(R.id.btnMyProgress).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MyProgressActivity.class);
            startActivity(intent);
        });
    }
}
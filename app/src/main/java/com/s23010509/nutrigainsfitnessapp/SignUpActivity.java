package com.s23010509.nutrigainsfitnessapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        // Register button to CreateProfile page
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, CreateProfileActivity.class));
            finish(); // Remove signup screen from back stack
        });

        // Login button to LoginActivity page
        findViewById(R.id.btnlogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}
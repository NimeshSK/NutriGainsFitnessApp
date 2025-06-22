package com.s23010509.nutrigainsfitnessapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Login → Home
        findViewById(R.id.btnGetStared).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish(); // Remove login screen from back stack
        });

        // Register → SignUp
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}
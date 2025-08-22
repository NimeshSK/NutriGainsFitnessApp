package com.s23010509.nutrigainsfitnessapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Started to Login
        findViewById(R.id.btnGetStared).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        // Register to SignUp
        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }
}
package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Views
        EditText etName = findViewById(R.id.etName);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnSignup = findViewById(R.id.btnSignup);
        TextView tvLogin = findViewById(R.id.tvLogin);

        // Button Logic
        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Account Created: " + name, Toast.LENGTH_SHORT).show();
                // Navigate to createprofile activity
                Intent intent = new Intent(SignupActivity.this, createprofile.class);
                startActivity(intent);
            }
        });

        tvLogin.setOnClickListener(v -> {
            // Handle login navigation
            Toast.makeText(this, "Navigate to Login", Toast.LENGTH_SHORT).show();
        });
    }
}

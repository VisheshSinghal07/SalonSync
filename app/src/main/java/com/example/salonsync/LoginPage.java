package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPage extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(email, password);
            }
        });
    }

    private void performLogin(String email, String password) {
        // Mocking database check
        // In a real app, you would query your database (Firebase, SQLite, etc.) here
        if (email.equals("admin@salonsync.com")) {
            Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginPage.this, Dashboard.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginPage.this, HomePage.class);
            startActivity(intent);
        }
        finish();
    }
}
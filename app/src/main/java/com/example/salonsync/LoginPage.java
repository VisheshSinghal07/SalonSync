package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

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
            String input = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(input, password);
            }
        });
    }

    private void performLogin(String input, String password) {
        // Handle Admin Login Check
        if (input.equalsIgnoreCase("admin@salonsync.com")) {
            checkAdmin(input, password);
            return;
        }

        // Handle User Login (by Phone)
        DatabaseReference reference = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");

        reference.child(input).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String dbPassword = snapshot.child("password").getValue(String.class);
                    if (dbPassword != null && dbPassword.equals(password)) {
                        
                        // Save to SharedPreferences for Profile page
                        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("name", snapshot.child("name").getValue(String.class));
                        editor.putString("phone", snapshot.child("phone").getValue(String.class));
                        editor.putString("city", snapshot.child("city").getValue(String.class));
                        editor.putString("gender", snapshot.child("gender").getValue(String.class));
                        editor.apply();

                        Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginPage.this, HomePage.class));
                        finish();
                    } else {
                        Toast.makeText(LoginPage.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginPage.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginPage.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAdmin(String inputEmail, String inputPassword) {
        DatabaseReference adminRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("admin");

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String dbEmail = snapshot.child("email").getValue(String.class);
                    String dbPassword = snapshot.child("password").getValue(String.class);
                    
                    if (inputEmail.equalsIgnoreCase(dbEmail) && inputPassword.equals(dbPassword)) {
                        Toast.makeText(LoginPage.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginPage.this, Dashboard.class));
                        finish();
                    } else {
                        Toast.makeText(LoginPage.this, "Invalid Admin Credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginPage.this, "Admin node not found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginPage.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etPhone, etCity, etGender, etPassword;
    Button btnSignup;
    TextView tvLogin;
    ImageButton btnBack;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etCity);
        etGender = findViewById(R.id.etGender);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnSignup.setOnClickListener(v -> {
            database = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/");
            reference = database.getReference("users");

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String gender = etGender.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || city.isEmpty() || gender.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("[0-9]{10}")) {
                etPhone.setError("Enter valid 10-digit number");
                return;
            }

            User user = new User(name, phone, city, gender, password);
            reference.child(phone).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Also save to SharedPreferences for local profile view
                    android.content.SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                    android.content.SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("name", name);
                    editor.putString("phone", phone);
                    editor.putString("city", city);
                    editor.putString("gender", gender);
                    editor.apply();

                    Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginPage.class);
            startActivity(intent);
        });
    }
}
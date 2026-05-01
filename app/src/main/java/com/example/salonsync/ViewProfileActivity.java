package com.example.salonsync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logout());

        TextView tvName = findViewById(R.id.tvDisplayName);
        TextView tvPhone = findViewById(R.id.tvDisplayPhone);
        TextView tvCity = findViewById(R.id.tvDisplayCity);
        TextView tvGender = findViewById(R.id.tvDisplayGender);

        // Load from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        tvName.setText(prefs.getString("name", "Not Set"));
        tvPhone.setText(prefs.getString("phone", "Not Set"));
        tvCity.setText(prefs.getString("city", "Not Set"));
        tvGender.setText(prefs.getString("gender", "Not Set"));
    }

    private void logout() {
        // Optional: Clear login status if you have one
        // SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // prefs.edit().clear().apply();

        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ViewProfileActivity.this, LoginPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
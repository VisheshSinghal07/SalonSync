package com.example.salonsync;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView tvPhone = findViewById(R.id.tvDisplayPhone);
        TextView tvCity = findViewById(R.id.tvDisplayCity);
        TextView tvGender = findViewById(R.id.tvDisplayGender);

        // Load from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        tvPhone.setText(prefs.getString("phone", "Not Set"));
        tvCity.setText(prefs.getString("city", "Not Set"));
        tvGender.setText(prefs.getString("gender", "Not Set"));
    }
}
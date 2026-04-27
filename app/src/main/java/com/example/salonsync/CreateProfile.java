package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        EditText etPhone = findViewById(R.id.et_phone);
        EditText etOtherCity = findViewById(R.id.et_other_city);
        Spinner spinnerCity = findViewById(R.id.spinner_city);
        Spinner spinnerGender = findViewById(R.id.spinner_gender);
        Button btnSave = findViewById(R.id.btn_save_profile);

        // 1. City Setup (Delhi NCR)
        String[] cities = {"Select City", "Delhi", "Noida", "Gurgaon", "Ghaziabad", "Other"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, cities);
        spinnerCity.setAdapter(cityAdapter);

        // 2. Gender Setup
        String[] genders = {"Select Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, genders);
        spinnerGender.setAdapter(genderAdapter);

        // 3. Handle "Other" City Visibility
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cities[position].equals("Other")) {
                    etOtherCity.setVisibility(View.VISIBLE);
                } else {
                    etOtherCity.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 4. Save Logic with Validation
        btnSave.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String city = spinnerCity.getSelectedItem().toString();
            if (city.equals("Other")) {
                city = etOtherCity.getText().toString().trim();
            }
            String gender = spinnerGender.getSelectedItem().toString();

            if (phone.length() != 10) {
                etPhone.setError("Invalid number! Must be 10 digits.");
                return;
            }

            // Save to SharedPreferences
            android.content.SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = prefs.edit();
            editor.putString("phone", phone);
            editor.putString("city", city);
            editor.putString("gender", gender);
            editor.apply();

            Toast.makeText(this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();

            // Navigate to LoginPage
            Intent intent = new Intent(CreateProfile.this, LoginPage.class);
            startActivity(intent);
            finish();
        });
    }
}

package com.example.salonsync;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class createprofile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);


        EditText etName = findViewById(R.id.et_full_name);
        EditText etPhone = findViewById(R.id.et_phone);
        EditText etOtherCity = findViewById(R.id.et_other_city);
        Spinner spinnerCity = findViewById(R.id.spinner_city);
        Spinner spinnerGender = findViewById(R.id.spinner_gender);
        Button btnSave = findViewById(R.id.btn_save_profile);


        String[] cities = {"Select City", "Delhi", "Noida", "Gurgaon", "Other"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, cities);
        spinnerCity.setAdapter(cityAdapter);


        String[] genders = {"Select Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, genders);
        spinnerGender.setAdapter(genderAdapter);


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


        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String selectedCity = spinnerCity.getSelectedItem().toString();
            String gender = spinnerGender.getSelectedItem().toString();


            if (phone.length() != 10) {
                etPhone.setError("Invalid number! Enter exactly 10 digits.");
                return;
            }


            if (selectedCity.equals("Select City")) {
                Toast.makeText(this, "Please select a city", Toast.LENGTH_SHORT).show();
                return;
            }

            String finalCity = selectedCity;
            if (selectedCity.equals("Other")) {
                finalCity = etOtherCity.getText().toString().trim();
                if (finalCity.isEmpty()) {
                    etOtherCity.setError("Enter city name");
                    return;
                }
            }


            Toast.makeText(this, "Welcome " + name + " from " + finalCity, Toast.LENGTH_LONG).show();
        });
    }
}

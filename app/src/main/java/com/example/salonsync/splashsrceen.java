package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class splashsrceen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check the dots here: R . layout . filename
        setContentView(R.layout.activity_splash);

        // Check the dots here: R . id . buttonname
        Button getStarted = findViewById(R.id.btn_get_started);

        if (getStarted != null) {
            getStarted.setOnClickListener(v -> {
                Intent intent = new Intent(splashsrceen.this, SignupActivity.class);
                startActivity(intent);
            });
        }
    }
}
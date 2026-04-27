package com.example.salonsync;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    RadioButton cardOption, applePayOption;
    Button payNowBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize views
        cardOption = findViewById(R.id.cardOption);
        applePayOption = findViewById(R.id.applePayOption);
        payNowBtn = findViewById(R.id.payNowBtn);

        // Default selection
        cardOption.setChecked(true);

        // Button Click
        payNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String method;

                if (cardOption.isChecked()) {
                    method = "Card";
                } else if (applePayOption.isChecked()) {
                    method = "Apple Pay";
                } else {
                    method = "None";
                }

                Toast.makeText(PaymentActivity.this,
                        "Processing payment via " + method,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentActivity extends AppCompatActivity {

    RadioButton cardOption, applePayOption;
    Button payNowBtn;
    TextView tvSalonName, tvSalonAddress, tvBookingService, tvBookingDateTime, tvPriceLabel, tvPriceValue, tvTaxValue, tvTotalValue;
    private String serviceName, selectedTime, salonId, salonNameIntent, salonAddressIntent;
    private int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get data from intent
        salonId = getIntent().getStringExtra("salon_id");
        salonNameIntent = getIntent().getStringExtra("salon_name");
        salonAddressIntent = getIntent().getStringExtra("salon_address");
        serviceName = getIntent().getStringExtra("service_name");
        selectedTime = getIntent().getStringExtra("selected_time");
        totalPrice = getIntent().getIntExtra("total_price", 0);

        // Initialize views
        cardOption = findViewById(R.id.cardOption);
        applePayOption = findViewById(R.id.applePayOption);
        payNowBtn = findViewById(R.id.payNowBtn);

        tvSalonName = findViewById(R.id.tvSalonName);
        tvSalonAddress = findViewById(R.id.tvSalonAddress);
        tvBookingService = findViewById(R.id.tvBookingService);
        tvBookingDateTime = findViewById(R.id.tvBookingDateTime);
        tvPriceLabel = findViewById(R.id.tvPriceLabel);
        tvPriceValue = findViewById(R.id.tvPriceValue);
        tvTaxValue = findViewById(R.id.tvTaxValue);
        tvTotalValue = findViewById(R.id.tvTotalValue);

        // Set dynamic data
        if (salonNameIntent != null) tvSalonName.setText(salonNameIntent);
        if (salonAddressIntent != null) tvSalonAddress.setText(salonAddressIntent);
        tvBookingService.setText("Service: " + serviceName);
        tvBookingDateTime.setText("Date & Time: " + selectedTime);
        tvPriceLabel.setText(serviceName);
        
        int tax = (int) (totalPrice * 0.1); // Assuming 10% tax
        int basePrice = totalPrice - tax;
        
        tvPriceValue.setText("₹" + basePrice);
        tvTaxValue.setText("₹" + tax);
        tvTotalValue.setText("₹" + totalPrice);
        payNowBtn.setText("Pay Now ₹" + totalPrice);

        // Fetch Salon details from Firebase if not passed correctly via intent
        if (salonId != null && (salonNameIntent == null || salonAddressIntent == null)) {
            DatabaseReference salonRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("salons").child(salonId);
            salonRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String location = snapshot.child("location").getValue(String.class);
                        if (name != null) tvSalonName.setText(name);
                        if (location != null) tvSalonAddress.setText(location);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PaymentActivity.this, "Failed to load salon details", Toast.LENGTH_SHORT).show();
                }
            });
        }

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

                // Navigate to Confirmation Screen
                Intent intent = new Intent(PaymentActivity.this, BookingConfirmationActivity.class);
                intent.putExtra("salon_id", salonId);
                intent.putExtra("salon_name", tvSalonName.getText().toString());
                intent.putExtra("salon_address", tvSalonAddress.getText().toString());
                intent.putExtra("service", serviceName);
                intent.putExtra("time", selectedTime);
                intent.putExtra("price", String.valueOf(totalPrice));
                startActivity(intent);
            }
        });
    }
}
package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BookingConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        String service = getIntent().getStringExtra("service");
        String time = getIntent().getStringExtra("time");
        String price = getIntent().getStringExtra("price");

        TextView tvService = findViewById(R.id.tvConfirmService);
        TextView tvTime = findViewById(R.id.tvConfirmDateTime);
        TextView tvPrice = findViewById(R.id.tvConfirmPayment);

        if (service != null) tvService.setText(service);
        if (time != null) tvTime.setText(time);
        if (price != null) tvPrice.setText(price);

        Button btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        Button btnViewBookings = findViewById(R.id.btnViewBookings);
        btnViewBookings.setOnClickListener(v -> {
            // Navigate to bookings if available
            finish();
        });
    }
}
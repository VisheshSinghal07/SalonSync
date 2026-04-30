package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class BookingConfirmationActivity extends AppCompatActivity {

    private String salonId, salonName, salonAddress, service, time, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        salonId = getIntent().getStringExtra("salon_id");
        salonName = getIntent().getStringExtra("salon_name");
        salonAddress = getIntent().getStringExtra("salon_address");
        service = getIntent().getStringExtra("service");
        time = getIntent().getStringExtra("time");
        price = getIntent().getStringExtra("price");

        TextView tvService = findViewById(R.id.tvConfirmService);
        TextView tvTime = findViewById(R.id.tvConfirmDateTime);
        TextView tvPrice = findViewById(R.id.tvConfirmPayment);
        TextView tvName = findViewById(R.id.tvSalonName);
        TextView tvAddress = findViewById(R.id.tvSalonAddress);

        if (salonName != null) tvName.setText(salonName);
        if (salonAddress != null) tvAddress.setText(salonAddress);
        if (service != null) tvService.setText(service);
        if (time != null) tvTime.setText(time);
        if (price != null) tvPrice.setText("₹" + price);

        saveBookingToFirebase();

        Button btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        Button btnViewBookings = findViewById(R.id.btnViewBookings);
        btnViewBookings.setOnClickListener(v -> {
            startActivity(new Intent(this, MyBookingsActivity.class));
            finish();
        });
    }

    private void saveBookingToFirebase() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String userPhone = prefs.getString("phone", "unknown_user");
        String userName = prefs.getString("name", "User");

        DatabaseReference bookingsRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("bookings");
        String bookingId = bookingsRef.push().getKey();

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("bookingId", bookingId);
        bookingData.put("userId", userPhone);
        bookingData.put("userName", userName);
        bookingData.put("salonId", salonId);
        bookingData.put("salonName", salonName);
        bookingData.put("salonAddress", salonAddress);
        bookingData.put("service", service);
        bookingData.put("dateTime", time);
        bookingData.put("price", price);
        bookingData.put("status", "Upcoming");

        if (bookingId != null) {
            bookingsRef.child(bookingId).setValue(bookingData)
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save booking to database", Toast.LENGTH_SHORT).show());
        }
    }
}
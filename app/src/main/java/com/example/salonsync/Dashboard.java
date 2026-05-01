package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Dashboard extends AppCompatActivity {

    private TextView tvTodayEarnings, tvTotalBookings, tvActiveHrs, tvAvailableSlots, tvBookedSlots;
    private DatabaseReference bookingsRef, slotsRef;
    private static final String TAG = "DashboardDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvTodayEarnings = findViewById(R.id.tvTodayEarnings);
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvActiveHrs = findViewById(R.id.tvActiveHrs);
        tvAvailableSlots = findViewById(R.id.tvAvailableSlots);
        tvBookedSlots = findViewById(R.id.tvBookedSlots);

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/");
        bookingsRef = db.getReference("bookings");
        slotsRef = db.getReference("salons").child("LuxeBeautyLounge").child("slots");

        loadDashboardData();
        setupNavigation();
    }

    private void loadDashboardData() {
        String todayDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        String dayKey = new SimpleDateFormat("EEE dd", Locale.getDefault()).format(new Date());
        String testDate = "Tue 13"; // Fallback for your testing data

        // 1. Load Bookings
        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalToday = 0;
                double earningsToday = 0;
                
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Booking booking = postSnapshot.getValue(Booking.class);
                    if (booking != null && booking.getDateTime() != null) {
                        String bookingDate = booking.getDateTime();
                        
                        // Check if booking is for "Today" or matches current date strings
                        if (bookingDate.contains("Today") || 
                            bookingDate.contains(todayDate) || 
                            bookingDate.contains(testDate) ||
                            bookingDate.contains(dayKey)) {
                            
                            totalToday++;
                            if (!"Cancelled".equalsIgnoreCase(booking.getStatus())) {
                                try {
                                    String priceStr = booking.getPrice().replace("₹", "").trim();
                                    earningsToday += Double.parseDouble(priceStr);
                                } catch (Exception e) {
                                    Log.e(TAG, "Price parse error: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
                tvTodayEarnings.setText("₹" + (int)earningsToday);
                tvTotalBookings.setText(totalToday + " Bookings");
                tvBookedSlots.setText(totalToday + " Booked");
                tvActiveHrs.setText(String.format(Locale.getDefault(), "%.1f hrs active", totalToday * 0.5));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // 2. Load Slots (Total Available)
        slotsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalCapacity = 0;
                
                // Try current day, fallback to test date if empty
                DataSnapshot targetSlots = snapshot.child(dayKey);
                if (!targetSlots.exists()) {
                    targetSlots = snapshot.child(testDate);
                }

                if (targetSlots.exists()) {
                    for (DataSnapshot slotSnap : targetSlots.getChildren()) {

                        Object capObj = slotSnap.child("maleCapacity").getValue();
                        int capacity = 0;
                        if (capObj instanceof Long) capacity = ((Long) capObj).intValue();
                        else if (capObj instanceof Integer) capacity = (Integer) capObj;
                        
                        totalCapacity += capacity;
                    }
                }
                
                tvAvailableSlots.setText(totalCapacity + " Available");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupNavigation() {
        findViewById(R.id.navSalon).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminSalonProfileActivity.class));
        });
        findViewById(R.id.navBookings).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminBookingsActivity.class));
        });
        findViewById(R.id.navServices).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageSlotsActivity.class));
        });
        findViewById(R.id.navDashboard).setOnClickListener(v -> {});
    }
}
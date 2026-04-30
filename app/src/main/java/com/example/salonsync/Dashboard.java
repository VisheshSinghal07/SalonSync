package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvTodayEarnings = findViewById(R.id.tvTodayEarnings);
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvActiveHrs = findViewById(R.id.tvActiveHrs);
        tvAvailableSlots = findViewById(R.id.tvAvailableSlots);
        tvBookedSlots = findViewById(R.id.tvBookedSlots);

        bookingsRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("bookings");
        slotsRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("salons").child("LuxeBeautyLounge").child("slots");

        loadDashboardData();
        setupNavigation();
    }

    private void loadDashboardData() {
        String todayDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

        // Load Bookings Data for Today
        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalToday = 0;
                double earningsToday = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    com.example.salonsync.Booking booking = postSnapshot.getValue(com.example.salonsync.Booking.class);
                    if (booking != null && booking.getDateTime() != null && booking.getDateTime().contains(todayDate)) {
                        totalToday++;
                        if ("Confirmed".equalsIgnoreCase(booking.getStatus()) || "Completed".equalsIgnoreCase(booking.getStatus())) {
                            try {
                                earningsToday += Double.parseDouble(booking.getPrice().replace("₹", "").trim());
                            } catch (Exception e) {}
                        }
                    }
                }
                tvTodayEarnings.setText(String.format("₹%.0f", earningsToday));
                tvTotalBookings.setText(totalToday + " Bookings");
                tvBookedSlots.setText(totalToday + " Booked");
                tvActiveHrs.setText((totalToday * 0.5) + " hrs active"); // Mock logic for active hours
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Load Slots Data for Today
        String dayKey = new SimpleDateFormat("EEE dd", Locale.getDefault()).format(new Date());
        slotsRef.child(dayKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int available = 0;
                int booked = 0;
                for (DataSnapshot slotSnap : snapshot.getChildren()) {
                    // This is simplified. In a real app, you'd check capacity vs current bookings.
                    // For now, let's count capacity.
                    Integer maleCap = slotSnap.child("maleCapacity").getValue(Integer.class);
                    Integer femaleCap = slotSnap.child("femaleCapacity").getValue(Integer.class);
                    if (maleCap != null) available += maleCap;
                    if (femaleCap != null) available += femaleCap;
                }
                
                // We'll update booked based on today's confirmed bookings in the other listener or keep it simple
                tvAvailableSlots.setText(available + " Total Capacity");
                // For 'Booked', we can use the totalToday from the other listener if we want accuracy
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupNavigation() {
        findViewById(R.id.navSalon).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminSalonProfileActivity.class));
            finish();
        });

        findViewById(R.id.navBookings).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminBookingsActivity.class));
            finish();
        });

        findViewById(R.id.navServices).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageSlotsActivity.class));
            finish();
        });
    }
}
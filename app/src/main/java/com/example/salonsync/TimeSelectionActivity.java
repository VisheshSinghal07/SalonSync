package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class TimeSelectionActivity extends AppCompatActivity {

    private String selectedServiceName;
    private int totalDuration;
    private int totalPrice;
    private String selectedTimeSlot;
    private TextView tvSelectedService, tvSelectedTime;
    private String salonId, salonName, salonAddress;
    private String selectedDate = "Tue 13"; // Matching ManageSlotsActivity default

    private List<TimeSlot> morningSlots = new ArrayList<>();
    private List<TimeSlot> afternoonSlots = new ArrayList<>();
    private List<TimeSlot> eveningSlots = new ArrayList<>();
    
    private TimeSlotAdapter morningAdapter, afternoonAdapter, eveningAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_selection);

        salonId = getIntent().getStringExtra("salon_id");
        if (salonId == null) salonId = "LuxeBeautyLounge";

        selectedServiceName = getIntent().getStringExtra("service_name");
        totalDuration = getIntent().getIntExtra("total_duration", 30);
        totalPrice = getIntent().getIntExtra("total_price", 0);
        
        // Fetch Salon details to pass along
        DatabaseReference salonRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("salons").child(salonId);
        salonRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    salonName = snapshot.child("name").getValue(String.class);
                    salonAddress = snapshot.child("location").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        if (selectedServiceName == null) selectedServiceName = "Service";

        tvSelectedService = findViewById(R.id.tvSelectedService);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvSelectedService.setText(selectedServiceName);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            if (selectedTimeSlot != null) {
                Intent intent = new Intent(TimeSelectionActivity.this, PaymentActivity.class);
                intent.putExtra("salon_id", salonId);
                intent.putExtra("salon_name", salonName);
                intent.putExtra("salon_address", salonAddress);
                intent.putExtra("service_name", selectedServiceName);
                intent.putExtra("total_price", totalPrice);
                intent.putExtra("selected_time", selectedDate + " • " + selectedTimeSlot);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            }
        });

        setupRecyclerViews();
        loadSlotsFromFirebase();
    }

    private void setupRecyclerViews() {
        // Morning
        RecyclerView rvMorning = findViewById(R.id.rvMorningSlots);
        rvMorning.setLayoutManager(new GridLayoutManager(this, 3));
        morningAdapter = new TimeSlotAdapter(morningSlots, slot -> updateSelection(slot, "morning"));
        rvMorning.setAdapter(morningAdapter);

        // Afternoon
        RecyclerView rvAfternoon = findViewById(R.id.rvAfternoonSlots);
        rvAfternoon.setLayoutManager(new GridLayoutManager(this, 3));
        afternoonAdapter = new TimeSlotAdapter(afternoonSlots, slot -> updateSelection(slot, "afternoon"));
        rvAfternoon.setAdapter(afternoonAdapter);

        // Evening
        RecyclerView rvEvening = findViewById(R.id.rvEveningSlots);
        rvEvening.setLayoutManager(new GridLayoutManager(this, 3));
        eveningAdapter = new TimeSlotAdapter(eveningSlots, slot -> updateSelection(slot, "evening"));
        rvEvening.setAdapter(eveningAdapter);
    }

    private void loadSlotsFromFirebase() {
        DatabaseReference slotsRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("salons").child(salonId).child("slots").child(selectedDate);

        slotsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                morningSlots.clear();
                afternoonSlots.clear();
                eveningSlots.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String timeRange = snap.child("time").getValue(String.class); // "09:00 AM - 10:00 AM"
                    String period = snap.child("period").getValue(String.class);
                    Boolean available = snap.child("available").getValue(Boolean.class);
                    
                    if (timeRange != null) {
                        String startTime = timeRange.split(" - ")[0];
                        TimeSlot slot = new TimeSlot(startTime, available != null ? available : true);
                        
                        if ("morning".equals(period)) morningSlots.add(slot);
                        else if ("afternoon".equals(period)) afternoonSlots.add(slot);
                        else eveningSlots.add(slot); // Handle "evening" if salon creates it
                    }
                }
                morningAdapter.notifyDataSetChanged();
                afternoonAdapter.notifyDataSetChanged();
                eveningAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateSelection(TimeSlot slot, String period) {
        if ("morning".equals(period)) {
            if (afternoonAdapter != null) afternoonAdapter.clearSelection();
            if (eveningAdapter != null) eveningAdapter.clearSelection();
        } else if ("afternoon".equals(period)) {
            if (morningAdapter != null) morningAdapter.clearSelection();
            if (eveningAdapter != null) eveningAdapter.clearSelection();
        } else if ("evening".equals(period)) {
            if (morningAdapter != null) morningAdapter.clearSelection();
            if (afternoonAdapter != null) afternoonAdapter.clearSelection();
        }

        selectedTimeSlot = slot.getTime();
        String endTime = calculateEndTime(selectedTimeSlot, totalDuration);
        tvSelectedTime.setText(selectedDate + " • " + selectedTimeSlot + " - " + endTime + " (" + totalDuration + " mins)");
    }

    private String calculateEndTime(String startTime, int durationMins) {
        try {
            String[] parts = startTime.split("[: ]");
            int hour = Integer.parseInt(parts[0]);
            int mins = Integer.parseInt(parts[1]);
            String period = parts[2];

            if (period.equals("PM") && hour != 12) hour += 12;
            if (period.equals("AM") && hour == 12) hour = 0;

            int totalStartMins = hour * 60 + mins;
            int totalEndMins = totalStartMins + durationMins;

            int endHour = (totalEndMins / 60) % 24;
            int endMins = totalEndMins % 60;

            return formatTime(endHour, endMins);
        } catch (Exception e) {
            return "";
        }
    }

    private String formatTime(int hour, int minutes) {
        String period = (hour >= 12) ? "PM" : "AM";
        int displayHour = (hour > 12) ? hour - 12 : (hour == 0 ? 12 : hour);
        return String.format("%02d:%02d %s", displayHour, minutes, period);
    }
}
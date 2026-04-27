package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TimeSelectionActivity extends AppCompatActivity {

    private String selectedServiceName;
    private int totalDuration;
    private String selectedTimeSlot;
    private TextView tvSelectedService, tvSelectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_selection);

        selectedServiceName = getIntent().getStringExtra("service_name");
        totalDuration = getIntent().getIntExtra("total_duration", 30);
        
        if (selectedServiceName == null) selectedServiceName = "Service";

        tvSelectedService = findViewById(R.id.tvSelectedService);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvSelectedService.setText(selectedServiceName);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            if (selectedTimeSlot != null) {
                Toast.makeText(this, "Booking confirmed for " + selectedTimeSlot, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show();
            }
        });

        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        // Morning Slots (9 AM - 12 PM)
        RecyclerView rvMorning = findViewById(R.id.rvMorningSlots);
        rvMorning.setLayoutManager(new GridLayoutManager(this, 3));
        rvMorning.setAdapter(new TimeSlotAdapter(generateSlots(9, 12), slot -> updateSelection(slot)));

        // Afternoon Slots (12 PM - 4 PM)
        RecyclerView rvAfternoon = findViewById(R.id.rvAfternoonSlots);
        rvAfternoon.setLayoutManager(new GridLayoutManager(this, 3));
        rvAfternoon.setAdapter(new TimeSlotAdapter(generateSlots(12, 16), slot -> updateSelection(slot)));

        // Evening Slots (4 PM - 8 PM)
        RecyclerView rvEvening = findViewById(R.id.rvEveningSlots);
        rvEvening.setLayoutManager(new GridLayoutManager(this, 3));
        rvEvening.setAdapter(new TimeSlotAdapter(generateSlots(16, 20), slot -> updateSelection(slot)));
    }

    private List<TimeSlot> generateSlots(int startHour, int endHour) {
        List<TimeSlot> slots = new ArrayList<>();
        for (int hour = startHour; hour < endHour; hour++) {
            slots.add(new TimeSlot(formatTime(hour, 0), true));
            slots.add(new TimeSlot(formatTime(hour, 30), true));
        }
        return slots;
    }

    private String formatTime(int hour, int minutes) {
        String period = (hour >= 12) ? "PM" : "AM";
        int displayHour = (hour > 12) ? hour - 12 : (hour == 0 ? 12 : hour);
        return String.format("%02d:%02d %s", displayHour, minutes, period);
    }

    private void updateSelection(TimeSlot slot) {
        selectedTimeSlot = slot.getTime();
        // Calculate end time based on totalDuration
        String endTime = calculateEndTime(selectedTimeSlot, totalDuration);
        tvSelectedTime.setText("Today • " + selectedTimeSlot + " - " + endTime + " (" + totalDuration + " mins)");
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
}
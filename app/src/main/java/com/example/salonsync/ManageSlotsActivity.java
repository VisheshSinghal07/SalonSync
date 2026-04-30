package com.example.salonsync;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class ManageSlotsActivity extends AppCompatActivity {

    LinearLayout morningSlotsContainer, afternoonSlotsContainer;
    LinearLayout dateContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_slots);

        morningSlotsContainer = findViewById(R.id.morningSlotsContainer);
        afternoonSlotsContainer = findViewById(R.id.afternoonSlotsContainer);

        // Add Slot Listeners
        findViewById(R.id.btnAddMorningSlot).setOnClickListener(v -> showTimePicker(true));
        findViewById(R.id.btnAddAfternoonSlot).setOnClickListener(v -> showTimePicker(false));

        setupDateSelector();
        setupNavigation();

        // Add initial default slots
        addSlotToContainer(true, "09:00 AM", "10:00 AM");
        addSlotToContainer(false, "12:00 PM", "01:00 PM");
    }

    private void showTimePicker(boolean isMorning) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            String time = formatTime(selectedHour, selectedMinute);
            String endTime = formatTime(selectedHour + 1, selectedMinute); // Default 1 hour slot
            addSlotToContainer(isMorning, time, endTime);
        }, hour, minute, false);
        mTimePicker.setTitle("Select Start Time");
        mTimePicker.show();
    }

    private String formatTime(int hour, int minute) {
        String am_pm = (hour < 12) ? "AM" : "PM";
        int displayHour = (hour > 12) ? hour - 12 : (hour == 0 ? 12 : hour);
        return String.format("%02d:%02d %s", displayHour, minute, am_pm);
    }

    private void addSlotToContainer(boolean isMorning, String startTime, String endTime) {
        View slotView = LayoutInflater.from(this).inflate(R.layout.item_manage_slot_card, null);
        
        TextView tvTimeRange = slotView.findViewById(R.id.tvTimeRange);
        tvTimeRange.setText(startTime + " - " + endTime);

        TextView maleCount = slotView.findViewById(R.id.maleCount);
        TextView femaleCount = slotView.findViewById(R.id.femaleCount);
        Button malePlus = slotView.findViewById(R.id.malePlus);
        Button maleMinus = slotView.findViewById(R.id.maleMinus);
        Button femalePlus = slotView.findViewById(R.id.femalePlus);
        Button femaleMinus = slotView.findViewById(R.id.femaleMinus);
        ImageView btnDelete = slotView.findViewById(R.id.btnDelete);

        final int[] counts = {4, 4}; // [male, female]

        malePlus.setOnClickListener(v -> { counts[0]++; maleCount.setText(String.valueOf(counts[0])); });
        maleMinus.setOnClickListener(v -> { if (counts[0] > 0) { counts[0]--; maleCount.setText(String.valueOf(counts[0])); } });
        femalePlus.setOnClickListener(v -> { counts[1]++; femaleCount.setText(String.valueOf(counts[1])); });
        femaleMinus.setOnClickListener(v -> { if (counts[1] > 0) { counts[1]--; femaleCount.setText(String.valueOf(counts[1])); } });

        LinearLayout container = isMorning ? morningSlotsContainer : afternoonSlotsContainer;
        
        btnDelete.setOnClickListener(v -> container.removeView(slotView));

        container.addView(slotView);
    }

    private void setupDateSelector() {
        dateContainer = findViewById(R.id.dateContainer);
        for (int i = 0; i < dateContainer.getChildCount(); i++) {
            View child = dateContainer.getChildAt(i);
            if (child instanceof TextView) {
                child.setOnClickListener(v -> {
                    for (int j = 0; j < dateContainer.getChildCount(); j++) {
                        View c = dateContainer.getChildAt(j);
                        c.setBackground(ContextCompat.getDrawable(this, R.drawable.card_bg));
                        ((TextView)c).setTextColor(0xFF555555);
                        c.setBackgroundColor(0xFFEEEEEE);
                    }
                    v.setBackgroundColor(0xFFC19A6B);
                    ((TextView)v).setTextColor(0xFFFFFFFF);
                    Toast.makeText(this, "Slots for " + ((TextView) v).getText().toString().replace("\n", " "), Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void setupNavigation() {
        findViewById(R.id.navDashboard).setOnClickListener(v -> startActivity(new Intent(this, Dashboard.class)));
        findViewById(R.id.navBookings).setOnClickListener(v -> startActivity(new Intent(this, AdminBookingsActivity.class)));
    }
}
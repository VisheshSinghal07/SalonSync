package com.example.salonsync;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageSlotsActivity extends AppCompatActivity {

    LinearLayout morningSlotsContainer, afternoonSlotsContainer, eveningSlotsContainer;
    LinearLayout dateContainer;
    String selectedDate = "Tue 13"; // Default for now, matching the pre-selected UI
    DatabaseReference salonRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_slots);

        morningSlotsContainer = findViewById(R.id.morningSlotsContainer);
        afternoonSlotsContainer = findViewById(R.id.afternoonSlotsContainer);
        eveningSlotsContainer = findViewById(R.id.eveningSlotsContainer);

        // Firebase reference
        salonRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("salons").child("LuxeBeautyLounge");

        // Add Slot Listeners
        findViewById(R.id.btnAddMorningSlot).setOnClickListener(v -> showTimePicker("morning"));
        findViewById(R.id.btnAddAfternoonSlot).setOnClickListener(v -> showTimePicker("afternoon"));
        findViewById(R.id.btnAddEveningSlot).setOnClickListener(v -> showTimePicker("evening"));
        
        findViewById(R.id.btnSaveSlots).setOnClickListener(v -> saveSlotsToFirebase());

        setupDateSelector();
        setupNavigation();

        // Load existing slots for the default date
        loadSlotsFromFirebase();
    }

    private void saveSlotsToFirebase() {
        List<Map<String, Object>> slotsList = new ArrayList<>();
        
        for (int i = 0; i < morningSlotsContainer.getChildCount(); i++) {
            slotsList.add(getSlotData(morningSlotsContainer.getChildAt(i), "morning"));
        }
        for (int i = 0; i < afternoonSlotsContainer.getChildCount(); i++) {
            slotsList.add(getSlotData(afternoonSlotsContainer.getChildAt(i), "afternoon"));
        }
        for (int i = 0; i < eveningSlotsContainer.getChildCount(); i++) {
            slotsList.add(getSlotData(eveningSlotsContainer.getChildAt(i), "evening"));
        }

        salonRef.child("slots").child(selectedDate.replace("\n", " ")).setValue(slotsList)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Slots saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private Map<String, Object> getSlotData(View view, String period) {
        TextView tvTimeRange = view.findViewById(R.id.tvTimeRange);
        TextView slotCount = view.findViewById(R.id.slotCount);
        
        int capacity = Integer.parseInt(slotCount.getText().toString());
        Map<String, Object> data = new HashMap<>();
        data.put("time", tvTimeRange.getText().toString());
        // Simplify back to shared capacity if user wants 1 box, or split 50/50
        data.put("maleCapacity", capacity); 
        data.put("femaleCapacity", capacity);
        data.put("period", period);
        data.put("available", true);
        return data;
    }

    private void loadSlotsFromFirebase() {
        // Clear containers first to ensure no "ghost" slots from other dates
        morningSlotsContainer.removeAllViews();
        afternoonSlotsContainer.removeAllViews();
        eveningSlotsContainer.removeAllViews();

        salonRef.child("slots").child(selectedDate.replace("\n", " ")).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                for (com.google.firebase.database.DataSnapshot snap : task.getResult().getChildren()) {
                    String time = snap.child("time").getValue(String.class);
                    String period = snap.child("period").getValue(String.class);
                    
                    // Firebase numbers are often Long, safe-cast them to avoid crashes
                    Object maleObj = snap.child("maleCapacity").getValue();
                    int male = 4; // Default
                    if (maleObj instanceof Long) male = ((Long) maleObj).intValue();
                    else if (maleObj instanceof Integer) male = (Integer) maleObj;
                    
                    if (time != null && time.contains(" - ")) {
                        String[] times = time.split(" - ");
                        if ("morning".equals(period)) {
                            addSlotToContainer(morningSlotsContainer, times[0], times[1], male);
                        } else if ("afternoon".equals(period)) {
                            addSlotToContainer(afternoonSlotsContainer, times[0], times[1], male);
                        } else if ("evening".equals(period)) {
                            addSlotToContainer(eveningSlotsContainer, times[0], times[1], male);
                        }
                    }
                }
            }
        });
    }

    private void addSlotToContainer(LinearLayout container, String startTime, String endTime, int capacity) {
        View slotView = LayoutInflater.from(this).inflate(R.layout.item_manage_slot_card, null);
        
        TextView tvTimeRange = slotView.findViewById(R.id.tvTimeRange);
        tvTimeRange.setText(startTime + " - " + endTime);

        TextView slotCount = slotView.findViewById(R.id.slotCount);
        slotCount.setText(String.valueOf(capacity));

        TextView slotPlus = slotView.findViewById(R.id.slotPlus);
        TextView slotMinus = slotView.findViewById(R.id.slotMinus);
        ImageView btnDelete = slotView.findViewById(R.id.btnDelete);

        final int[] count = {capacity};

        slotPlus.setOnClickListener(v -> { count[0]++; slotCount.setText(String.valueOf(count[0])); });
        slotMinus.setOnClickListener(v -> { if (count[0] > 0) { count[0]--; slotCount.setText(String.valueOf(count[0])); } });

        btnDelete.setOnClickListener(v -> {
            container.removeView(slotView);
            // After removing from UI, save the updated list to Firebase
            saveSlotsToFirebase();
        });
        container.addView(slotView);
    }

    private void showTimePicker(String period) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            String time = formatTime(selectedHour, selectedMinute);
            String endTime = formatTime(selectedHour + 1, selectedMinute); 
            LinearLayout container;
            if ("morning".equals(period)) container = morningSlotsContainer;
            else if ("afternoon".equals(period)) container = afternoonSlotsContainer;
            else container = eveningSlotsContainer;
            
            addSlotToContainer(container, time, endTime, 4);
        }, hour, minute, false);
        mTimePicker.setTitle("Select Start Time");
        mTimePicker.show();
    }

    private String formatTime(int hour, int minute) {
        String am_pm = (hour < 12) ? "AM" : "PM";
        int displayHour = (hour > 12) ? hour - 12 : (hour == 0 ? 12 : hour);
        if (hour == 12) am_pm = "PM";
        return String.format("%02d:%02d %s", displayHour, minute, am_pm);
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
                    }
                    v.setBackgroundColor(0xFFC19A6B);
                    ((TextView)v).setTextColor(0xFFFFFFFF);
                    selectedDate = ((TextView) v).getText().toString();
                    loadSlotsFromFirebase();
                });
            }
        }
    }

    private void setupNavigation() {
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });
        findViewById(R.id.navBookings).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminBookingsActivity.class));
            finish();
        });
        findViewById(R.id.navSalon).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminSalonProfileActivity.class));
            finish();
        });
    }
}
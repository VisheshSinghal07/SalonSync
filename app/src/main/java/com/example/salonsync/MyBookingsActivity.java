package com.example.salonsync;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyBookingsActivity extends AppCompatActivity {

    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> showCancelDialog());
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    Toast.makeText(this,
                            "Booking Cancelled",
                            Toast.LENGTH_SHORT).show();

                    btnCancel.setText("Cancelled");
                    btnCancel.setEnabled(false);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
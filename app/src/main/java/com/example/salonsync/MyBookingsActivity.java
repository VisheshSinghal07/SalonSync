package com.example.salonsync;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView rvMyBookings;
    private BookingsAdapter adapter;
    private List<Booking> bookingList;
    private DatabaseReference bookingsRef;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, HomePage.class));
            finish();
        });

        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        userPhone = prefs.getString("phone", "");

        rvMyBookings = findViewById(R.id.rvMyBookings);
        rvMyBookings.setLayoutManager(new LinearLayoutManager(this));
        
        bookingList = new ArrayList<>();
        adapter = new BookingsAdapter(bookingList);
        rvMyBookings.setAdapter(adapter);

        bookingsRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("bookings");
        loadMyBookings();
    }

    private void loadMyBookings() {
        bookingsRef.orderByChild("userId").equalTo(userPhone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Booking booking = postSnapshot.getValue(Booking.class);
                    if (booking != null) {
                        bookingList.add(booking);
                    }
                }
                adapter.notifyDataSetChanged();
                if (bookingList.isEmpty()) {
                    Toast.makeText(MyBookingsActivity.this, "No bookings found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyBookingsActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCancelDialog(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking? It will be deleted permanently.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    bookingsRef.child(booking.getBookingId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(MyBookingsActivity.this, "Booking Cancelled and Deleted", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show();
    }

    private class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {
        private List<Booking> list;

        public BookingsAdapter(List<Booking> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_booking, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Booking booking = list.get(position);
            holder.tvSalonName.setText(booking.getSalonName());
            holder.tvSalonAddress.setText(booking.getSalonAddress());
            holder.tvService.setText(booking.getService());
            holder.tvDateTime.setText(booking.getDateTime());
            holder.tvPrice.setText("₹" + booking.getPrice());
            holder.tvStatus.setText(booking.getStatus());

            if ("Cancelled".equalsIgnoreCase(booking.getStatus())) {
                holder.btnCancel.setEnabled(false);
                holder.btnCancel.setText("Cancelled");
                holder.tvStatus.setTextColor(0xFFD32F2F);
                holder.tvStatus.setBackgroundResource(0); // Optional: change background
            } else {
                holder.btnCancel.setEnabled(true);
                holder.btnCancel.setText("Cancel Booking");
                holder.btnCancel.setOnClickListener(v -> showCancelDialog(booking));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSalonName, tvSalonAddress, tvService, tvDateTime, tvPrice, tvStatus;
            Button btnCancel;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSalonName = itemView.findViewById(R.id.tvSalonName);
                tvSalonAddress = itemView.findViewById(R.id.tvSalonAddress);
                tvService = itemView.findViewById(R.id.tvService);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}
package com.example.salonsync;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminBookingsActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private List<Booking> bookingList;
    private BookingAdapter adapter;
    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bookings);

        rvBookings = findViewById(R.id.rvUpcomingBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));

        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList);
        rvBookings.setAdapter(adapter);

        bookingsRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("bookings");
        loadBookings();

        setupNavigation();
    }

    private void loadBookings() {
        bookingsRef.addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void setupNavigation() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });
        
        findViewById(R.id.navServices).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageSlotsActivity.class));
            finish();
        });

        findViewById(R.id.navSalon).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminSalonProfileActivity.class));
            finish();
        });
    }

    // Reuse adapter logic for simplicity in this activity
    private class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
        private List<Booking> list;

        public BookingAdapter(List<Booking> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
            return new BookingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
            Booking booking = list.get(position);
            holder.name.setText(booking.getUserName());
            holder.service.setText(booking.getService());
            holder.time.setText(booking.getDateTime());
            holder.status.setText(booking.getStatus());

            if ("Cancelled".equalsIgnoreCase(booking.getStatus())) {
                holder.status.setBackgroundColor(Color.parseColor("#FFEBEE"));
                holder.status.setTextColor(Color.parseColor("#D32F2F"));
            } else {
                holder.status.setBackgroundColor(Color.parseColor("#FFF3E0"));
                holder.status.setTextColor(Color.parseColor("#E65100"));
            }

            holder.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(AdminBookingsActivity.this)
                        .setTitle("Delete Booking")
                        .setMessage("Are you sure you want to delete this booking?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            bookingsRef.child(booking.getBookingId()).removeValue();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        public class BookingViewHolder extends RecyclerView.ViewHolder {
            TextView name, service, time, status;
            public BookingViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.txtName);
                service = itemView.findViewById(R.id.txtService);
                time = itemView.findViewById(R.id.txtTime);
                status = itemView.findViewById(R.id.txtStatus);
            }
        }
    }
}

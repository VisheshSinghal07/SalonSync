package com.example.salonsync;

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

public class AdminBookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bookings);

        RecyclerView rvBookings = findViewById(R.id.rvUpcomingBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));

        List<Dashboard.Booking> list = new ArrayList<>();
        list.add(new Dashboard.Booking("Sarah Jenkins", "Balayage & Blowdry", "11:00 AM", "Upcoming"));
        list.add(new Dashboard.Booking("Maya Roberts", "Classic Manicure", "01:30 PM", "Upcoming"));
        list.add(new Dashboard.Booking("Jessica Alba", "Hair Coloring", "04:00 PM", "Upcoming"));
        list.add(new Dashboard.Booking("David Beckham", "Men's Trim", "05:30 PM", "Upcoming"));

        BookingAdapter adapter = new BookingAdapter(list);
        rvBookings.setAdapter(adapter);

        setupNavigation();
    }

    private void setupNavigation() {
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });
        
        // navBookings is already active
    }

    // Reuse adapter logic for simplicity in this activity
    private class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
        private List<Dashboard.Booking> bookingList;

        public BookingAdapter(List<Dashboard.Booking> bookingList) {
            this.bookingList = bookingList;
        }

        @NonNull
        @Override
        public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
            return new BookingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
            Dashboard.Booking booking = bookingList.get(position);
            holder.name.setText(booking.getName());
            holder.service.setText(booking.getService());
            holder.time.setText(booking.getTime());
            holder.status.setText(booking.getStatus());

            holder.status.setBackgroundColor(Color.parseColor("#FFF3E0"));
            holder.status.setTextColor(Color.parseColor("#E65100"));
        }

        @Override
        public int getItemCount() { return bookingList.size(); }

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
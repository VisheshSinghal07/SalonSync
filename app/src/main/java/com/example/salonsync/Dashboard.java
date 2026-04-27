package com.example.salonsync;

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

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 1. Setup RecyclerView
        RecyclerView rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));

        // 2. Prepare Data
        List<Booking> list = new ArrayList<>();
        list.add(new Booking("Sarah Jenkins", "Balayage & Blowdry", "11:00 AM", "Upcoming"));
        list.add(new Booking("Maya Roberts", "Classic Manicure", "01:30 PM", "Upcoming"));
        list.add(new Booking("Emily Clark", "Root Touch-up", "03:00 PM", "Completed"));

        // 3. Set Adapter
        BookingAdapter adapter = new BookingAdapter(list);
        rvBookings.setAdapter(adapter);
    }

    // --- INNER DATA MODEL CLASS ---
    public static class Booking {
        private String name, service, time, status;

        public Booking(String name, String service, String time, String status) {
            this.name = name;
            this.service = service;
            this.time = time;
            this.status = status;
        }

        public String getName() { return name; }
        public String getService() { return service; }
        public String getTime() { return time; }
        public String getStatus() { return status; }
    }

    // --- INNER ADAPTER CLASS ---
    public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
        private List<Booking> bookingList;

        public BookingAdapter(List<Booking> bookingList) {
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
            Booking booking = bookingList.get(position);
            holder.name.setText(booking.getName());
            holder.service.setText(booking.getService());
            holder.time.setText(booking.getTime());
            holder.status.setText(booking.getStatus());

            // Status Styling
            if (booking.getStatus().equalsIgnoreCase("Completed")) {
                holder.status.setBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.status.setTextColor(Color.parseColor("#2E7D32"));
            } else {
                holder.status.setBackgroundColor(Color.parseColor("#FFF3E0"));
                holder.status.setTextColor(Color.parseColor("#E65100"));
            }
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
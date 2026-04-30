package com.example.salonsync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserBookingAdapter extends RecyclerView.Adapter<UserBookingAdapter.BookingViewHolder> {

    private List<UserBooking> bookingList;
    private OnBookingActionListener actionListener;

    public interface OnBookingActionListener {
        void onCancelClick(UserBooking booking);
    }

    public UserBookingAdapter(List<UserBooking> bookingList, OnBookingActionListener actionListener) {
        this.bookingList = bookingList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        UserBooking booking = bookingList.get(position);
        holder.tvSalonName.setText(booking.getSalonName());
        holder.tvSalonAddress.setText(booking.getAddress());
        holder.tvStatus.setText(booking.getStatus());
        holder.tvBookingTime.setText(booking.getTime());
        holder.tvBookingService.setText(booking.getService());

        holder.btnCancel.setOnClickListener(v -> actionListener.onCancelClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvSalonName, tvSalonAddress, tvStatus, tvBookingTime, tvBookingService;
        Button btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSalonName = itemView.findViewById(R.id.tvSalonName);
            tvSalonAddress = itemView.findViewById(R.id.tvSalonAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            tvBookingService = itemView.findViewById(R.id.tvBookingService);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}

class UserBooking {
    private String salonName, address, status, time, service;

    public UserBooking(String salonName, String address, String status, String time, String service) {
        this.salonName = salonName;
        this.address = address;
        this.status = status;
        this.time = time;
        this.service = service;
    }

    public String getSalonName() { return salonName; }
    public String getAddress() { return address; }
    public String getStatus() { return status; }
    public String getTime() { return time; }
    public String getService() { return service; }
}
package com.example.salonsync;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private List<TimeSlot> timeSlots;
    private OnTimeSlotClickListener listener;
    private int selectedPosition = -1;

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(TimeSlot timeSlot);
    }

    public TimeSlotAdapter(List<TimeSlot> timeSlots, OnTimeSlotClickListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot slot = timeSlots.get(position);
        holder.tvTime.setText(slot.getTime());

        if (!slot.isAvailable()) {
            holder.tvTime.setTextColor(Color.parseColor("#CCCCCC"));
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F9F9F9"));
            holder.cardView.setStrokeColor(Color.parseColor("#EEEEEE"));
            holder.itemView.setEnabled(false);
        } else if (selectedPosition == position) {
            holder.tvTime.setTextColor(Color.BLACK);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F5E6D3"));
            holder.cardView.setStrokeColor(Color.parseColor("#C49A6C"));
            holder.itemView.setEnabled(true);
        } else {
            holder.tvTime.setTextColor(Color.BLACK);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.cardView.setStrokeColor(Color.parseColor("#EEEEEE"));
            holder.itemView.setEnabled(true);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onTimeSlotClick(slot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        MaterialCardView cardView;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            cardView = (MaterialCardView) itemView;
        }
    }
}
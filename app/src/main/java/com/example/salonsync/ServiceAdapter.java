package com.example.salonsync;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.card.MaterialCardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(List<Service> serviceList, OnServiceClickListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.tvName.setText(service.getName());
        holder.tvDuration.setText(service.getDuration());
        holder.tvPrice.setText("₹ " + service.getPrice());
        holder.cbService.setChecked(service.isSelected());

        if (service.isSelected()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F5E6D3")); // Light brown highlight
            holder.cardView.setStrokeWidth(2);
            holder.cardView.setStrokeColor(Color.parseColor("#C49A6C"));
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.cardView.setStrokeWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            service.setSelected(!service.isSelected());
            notifyItemChanged(position);
            if (listener != null) {
                listener.onServiceClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDuration, tvPrice;
        CheckBox cbService;
        MaterialCardView cardView;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvServiceName);
            tvDuration = itemView.findViewById(R.id.tvServiceDuration);
            tvPrice = itemView.findViewById(R.id.tvServicePrice);
            cbService = itemView.findViewById(R.id.cbService);
            cardView = (MaterialCardView) itemView;
        }
    }
}
package com.example.salonsync;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SalonAdapter extends RecyclerView.Adapter<SalonAdapter.SalonViewHolder> {

    private List<Salon> salonList;

    public SalonAdapter(List<Salon> salonList) {
        this.salonList = salonList;
    }

    @NonNull
    @Override
    public SalonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salon, parent, false);
        return new SalonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalonViewHolder holder, int position) {
        Salon salon = salonList.get(position);
        holder.tvName.setText(salon.getName());
        holder.tvDetails.setText(salon.getRating());

        // Decode and show Base64 image if it exists, otherwise use fallback
        if (salon.getImageUrl() != null && salon.getImageUrl().length() > 100) {
            try {
                byte[] decodedString = Base64.decode(salon.getImageUrl(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.ivImage.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.ivImage.setImageResource(R.drawable.salon);
            }
        } else {
            holder.ivImage.setImageResource(R.drawable.salon);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SalonDetailsActivity.class);
            intent.putExtra("salon_id", salon.getId());
            intent.putExtra("salon_name", salon.getName());
            v.getContext().startActivity(intent);
        });

        // Use Glide or Picasso here to load images from your Node.js server
        // Example: Glide.with(holder.itemView.getContext()).load(salon.getImageUrl()).into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }

    public void filterList(List<Salon> filteredList) {
        this.salonList = filteredList;
        notifyDataSetChanged();
    }

    public static class SalonViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;
        ImageView ivImage;

        public SalonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSalonName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            ivImage = itemView.findViewById(R.id.ivSalonImage);
        }
    }
}
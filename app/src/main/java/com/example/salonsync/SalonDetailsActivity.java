package com.example.salonsync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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

public class SalonDetailsActivity extends AppCompatActivity {

    private RecyclerView rvServices, rvReviews;
    private ServiceAdapter serviceAdapter;
    private ReviewAdapter reviewAdapter;
    private TextView tvTotalPrice, tvSalonNameLarge, tvSalonAddress;
    private int totalPrice = 0;
    private List<Service> services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_details);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvSalonNameLarge = findViewById(R.id.tvSalonNameLarge);
        tvSalonAddress = findViewById(R.id.tvSalonAddress);
        ImageView ivSalonHeader = findViewById(R.id.ivSalonHeader);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnBookNow = findViewById(R.id.btnBookNow);
        
        // Hardcode the salon image to the local drawable
        ivSalonHeader.setImageResource(R.drawable.salon);

        btnBack.setOnClickListener(v -> finish());

        final String salonId = getIntent().getStringExtra("salon_id") != null ? getIntent().getStringExtra("salon_id") : "LuxeBeautyLounge";
        final String salonName = getIntent().getStringExtra("salon_name");

        // Setup Services
        rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        
        services = new ArrayList<>();
        serviceAdapter = new ServiceAdapter(services, service -> {
            if (service.isSelected()) {
                totalPrice += service.getPrice();
            } else {
                totalPrice -= service.getPrice();
            }
            tvTotalPrice.setText("₹ " + totalPrice);
        });
        rvServices.setAdapter(serviceAdapter);

        // Fetch Salon Data from Firebase
        DatabaseReference salonRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("salons").child(salonId);

        salonRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);
                    String rating = snapshot.child("rating").getValue(String.class);

                    tvSalonNameLarge.setText(name != null ? name : salonName);
                    tvSalonAddress.setText(location != null ? location : "");
                    
                    // Decode and show Base64 image
                    String encodedImage = snapshot.child("imageEncoded").getValue(String.class);
                    if (encodedImage != null && !encodedImage.isEmpty()) {
                        try {
                            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            ivSalonHeader.setImageBitmap(decodedByte);
                        } catch (Exception e) {
                            ivSalonHeader.setImageResource(R.drawable.salon);
                        }
                    } else {
                        ivSalonHeader.setImageResource(R.drawable.salon);
                    }

                    TextView tvRatingBadge = findViewById(R.id.tvRatingBadge);
                    if (tvRatingBadge != null) tvRatingBadge.setText(rating != null ? rating : "0.0");

                    services.clear();
                    DataSnapshot servicesSnap = snapshot.child("services");
                    for (DataSnapshot s : servicesSnap.getChildren()) {
                        String sName = s.child("name").getValue(String.class);
                        String priceStr = s.child("price").getValue(String.class);
                        int price = 0;
                        if (priceStr != null) {
                            try {
                                // Strip non-numeric characters except for the price itself
                                String cleanPrice = priceStr.replaceAll("[^0-9]", "");
                                price = Integer.parseInt(cleanPrice);
                            } catch (NumberFormatException e) {
                                price = 0;
                            }
                        }
                        if (sName != null) {
                            services.add(new Service(sName, "30 mins", price));
                        }
                    }
                    serviceAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Setup Reviews
        rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("Sarah Jenkins", "2 days ago", "Amazing service!", 5));
        reviewAdapter = new ReviewAdapter(reviews);
        rvReviews.setAdapter(reviewAdapter);

        btnBookNow.setOnClickListener(v -> {
            String selectedName = "";
            int totalDuration = 0;
            int count = 0;

            for (Service s : services) {
                if (s.isSelected()) {
                    if (count == 0) selectedName = s.getName();
                    count++;
                    totalDuration += 30;
                }
            }

            if (count == 0) {
                Toast.makeText(this, "Please select at least one service", Toast.LENGTH_SHORT).show();
                return;
            }

            if (count > 1) selectedName = "Multiple Services (" + count + ")";

            Intent intent = new Intent(this, TimeSelectionActivity.class);
            intent.putExtra("salon_id", salonId);
            intent.putExtra("service_name", selectedName);
            intent.putExtra("total_duration", totalDuration);
            intent.putExtra("total_price", totalPrice);
            startActivity(intent);
        });
    }
}

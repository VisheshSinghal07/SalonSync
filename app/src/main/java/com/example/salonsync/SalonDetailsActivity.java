package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SalonDetailsActivity extends AppCompatActivity {

    private RecyclerView rvServices, rvReviews;
    private ServiceAdapter serviceAdapter;
    private ReviewAdapter reviewAdapter;
    private TextView tvTotalPrice;
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_details);

        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnBookNow = findViewById(R.id.btnBookNow);

        btnBack.setOnClickListener(v -> finish());

        // Setup Services
        rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        
        List<Service> services = new ArrayList<>();
        services.add(new Service("Women's Haircut", "45 mins", 1000));
        services.add(new Service("Balayage & Blowdry", "120 mins", 2000));
        services.add(new Service("Signature Facial", "60 mins", 1500));
        services.add(new Service("Manicure", "30 mins", 500));

        serviceAdapter = new ServiceAdapter(services, service -> {
            if (service.isSelected()) {
                totalPrice += service.getPrice();
            } else {
                totalPrice -= service.getPrice();
            }
            tvTotalPrice.setText("₹ " + totalPrice);
        });
        rvServices.setAdapter(serviceAdapter);

        // Setup Reviews
        rvReviews = findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));

        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("Sarah Jenkins", "2 days ago", "Amazing service! My hair has never looked better. The staff was incredibly professional and the salon is gorgeous.", 5));
        reviews.add(new Review("Michelle O.", "1 week ago", "Loved the balayage. Very relaxing experience and they really listen to what you want. Highly recommend!", 4.5f));

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
                    // Extract number from "45 mins"
                    try {
                        String durationStr = s.getDuration().split(" ")[0];
                        totalDuration += Integer.parseInt(durationStr);
                    } catch (Exception e) {
                        totalDuration += 30; // Default if parsing fails
                    }
                }
            }

            if (count == 0) {
                android.widget.Toast.makeText(this, "Please select at least one service", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            if (count > 1) selectedName = "Multiple Services (" + count + ")";

            Intent intent = new Intent(this, TimeSelectionActivity.class);
            intent.putExtra("service_name", selectedName);
            intent.putExtra("total_duration", totalDuration);
            startActivity(intent);
        });
    }
}
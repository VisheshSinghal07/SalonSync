package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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

public class HomePage extends AppCompatActivity {

    private List<Salon> salonList;
    private SalonAdapter adapter;
    private DatabaseReference salonsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        salonList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.rvSalons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalonAdapter(salonList);
        recyclerView.setAdapter(adapter);

        // Firebase reference
        salonsRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("salons");

        loadSalons();

        // Search Logic
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bottom Navigation Logic
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ViewProfileActivity.class));
                return true;
            } else if (id == R.id.nav_bookings) {
                startActivity(new Intent(this, MyBookingsActivity.class));
                return true;
            } else if (id == R.id.nav_home) {
                return true;
            }
            return true;
        });
    }

    private void loadSalons() {
        salonsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                salonList.clear();
                for (DataSnapshot salonSnap : snapshot.getChildren()) {
                    String id = salonSnap.getKey();
                    String name = salonSnap.child("name").getValue(String.class);
                    String rating = salonSnap.child("rating").getValue(String.class);
                    
                    // Prioritize encoded image for free storage
                    String image = salonSnap.child("imageEncoded").getValue(String.class);
                    if (image == null || image.isEmpty()) {
                        image = salonSnap.child("imageUrl").getValue(String.class);
                    }
                    
                    if (name != null) {
                        salonList.add(new Salon(id, name, rating, image));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filter(String text) {
        List<Salon> filteredList = new ArrayList<>();
        for (Salon item : salonList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }
}
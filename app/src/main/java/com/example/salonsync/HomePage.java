package com.example.salonsync;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private List<Salon> salonList;
    private SalonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // 1. Initialize List with Dummy Data (Replace with PERN API call later)
        salonList = new ArrayList<>();
        salonList.add(new Salon("Luxe Hair & Spa", "4.9", "1.2 km"));
        salonList.add(new Salon("Glow Studio", "4.7", "2.5 km"));
        salonList.add(new Salon("Tress & Trim", "4.5", "0.8 km"));

        // 2. Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvSalons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SalonAdapter(salonList);
        recyclerView.setAdapter(adapter);

        // 3. Search Logic
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
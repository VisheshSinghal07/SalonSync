package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class AdminSalonProfileActivity extends AppCompatActivity {

    private List<ServiceItem> services;
    private ServiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_salon_profile);

        // 1. Setup Services RecyclerView
        RecyclerView rvAdminServices = findViewById(R.id.rvAdminServices);
        rvAdminServices.setLayoutManager(new LinearLayoutManager(this));

        services = new ArrayList<>();
        services.add(new ServiceItem("Hair Cut", "₹ 500"));
        services.add(new ServiceItem("Beard Trim", "₹ 200"));
        services.add(new ServiceItem("Hair Color", "₹ 1200"));
        services.add(new ServiceItem("Facial", "₹ 800"));

        adapter = new ServiceAdapter(services);
        rvAdminServices.setAdapter(adapter);

        // 2. Button Listeners
        findViewById(R.id.btnAddService).setOnClickListener(v -> showServiceDialog(null, -1));

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> 
            Toast.makeText(this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show());

        setupNavigation();
    }

    private void showServiceDialog(ServiceItem item, int position) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etDialogServiceName);
        TextInputEditText etPrice = dialogView.findViewById(R.id.etDialogServicePrice);

        String title = "Add New Service";
        if (item != null) {
            title = "Edit Service";
            etName.setText(item.name);
            // Remove ₹ symbol if exists for editing
            String priceOnly = item.price.replace("₹ ", "");
            etPrice.setText(priceOnly);
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String price = etPrice.getText().toString().trim();

                    if (!name.isEmpty() && !price.isEmpty()) {
                        String formattedPrice = "₹ " + price;
                        if (item == null) {
                            // Add new
                            services.add(new ServiceItem(name, formattedPrice));
                            adapter.notifyItemInserted(services.size() - 1);
                        } else {
                            // Update existing
                            item.name = name;
                            item.price = formattedPrice;
                            adapter.notifyItemChanged(position);
                        }
                    } else {
                        Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupNavigation() {
        findViewById(R.id.navDashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        });

        findViewById(R.id.navBookings).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminBookingsActivity.class));
            finish();
        });
    }

    // --- DATA MODEL ---
    public static class ServiceItem {
        String name, price;
        public ServiceItem(String name, String price) {
            this.name = name;
            this.price = price;
        }
    }

    // --- ADAPTER ---
    public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
        List<ServiceItem> serviceList;
        public ServiceAdapter(List<ServiceItem> serviceList) { this.serviceList = serviceList; }

        @NonNull
        @Override
        public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_service, parent, false);
            return new ServiceViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
            ServiceItem item = serviceList.get(position);
            holder.name.setText(item.name);
            holder.price.setText(item.price);
            holder.btnEdit.setOnClickListener(v -> showServiceDialog(item, position));
        }

        @Override
        public int getItemCount() { return serviceList.size(); }

        class ServiceViewHolder extends RecyclerView.ViewHolder {
            TextView name, price;
            ImageView btnEdit;
            public ServiceViewHolder(View v) {
                super(v);
                name = v.findViewById(R.id.txtServiceName);
                price = v.findViewById(R.id.txtServicePrice);
                btnEdit = v.findViewById(R.id.btnEditService);
            }
        }
    }
}
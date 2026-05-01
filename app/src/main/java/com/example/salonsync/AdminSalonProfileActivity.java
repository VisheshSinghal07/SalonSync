package com.example.salonsync;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminSalonProfileActivity extends AppCompatActivity {

    private List<ServiceItem> servicesList;
    private ServiceAdapter adapter;
    private EditText etSalonName, etLocation;
    private ImageView ivSalonImage;
    private DatabaseReference salonRef;
    private String encodedImage;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    uploadToFirebaseAsBase64(imageUri);
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Permission Denied! Cannot upload image.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_salon_profile);

        etSalonName = findViewById(R.id.etSalonName);
        etLocation = findViewById(R.id.etLocation);
        ivSalonImage = findViewById(R.id.ivSalonImage);
        
        RecyclerView rvAdminServices = findViewById(R.id.rvAdminServices);
        rvAdminServices.setLayoutManager(new LinearLayoutManager(this));

        servicesList = new ArrayList<>();
        adapter = new ServiceAdapter(servicesList);
        rvAdminServices.setAdapter(adapter);

        salonRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("salons").child("LuxeBeautyLounge");

        loadSalonData();

        findViewById(R.id.btnUploadImage).setOnClickListener(v -> checkPermissionAndOpenGallery());
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());

        findViewById(R.id.btnAddService).setOnClickListener(v -> showServiceDialog(null, -1));
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveSalonData());

        setupNavigation();
    }

    private void logout() {
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminSalonProfileActivity.this, LoginPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void checkPermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void uploadToFirebaseAsBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); 
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            ivSalonImage.setImageBitmap(bitmap);

            salonRef.child("imageEncoded").setValue(encodedImage).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("UploadError", e.getMessage());
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSalonData() {
        salonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etSalonName.setText(snapshot.child("name").getValue(String.class));
                    etLocation.setText(snapshot.child("location").getValue(String.class));
                    
                    String loadedEncoded = snapshot.child("imageEncoded").getValue(String.class);
                    if (loadedEncoded != null && !loadedEncoded.isEmpty()) {
                        encodedImage = loadedEncoded;
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivSalonImage.setImageBitmap(decodedByte);
                    } else {
                        ivSalonImage.setImageResource(R.drawable.salon);
                    }
                    
                    servicesList.clear();
                    DataSnapshot servicesSnap = snapshot.child("services");
                    for (DataSnapshot s : servicesSnap.getChildren()) {
                        String name = s.child("name").getValue(String.class);
                        String price = s.child("price").getValue(String.class);
                        servicesList.add(new ServiceItem(name, price));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveSalonData() {
        String name = etSalonName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (name.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill salon name and location", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> salonData = new HashMap<>();
        salonData.put("name", name);
        salonData.put("location", location);
        salonData.put("rating", "4.9");
        if (encodedImage != null) {
            salonData.put("imageEncoded", encodedImage);
        }

        Map<String, Object> servicesMap = new HashMap<>();
        for (int i = 0; i < servicesList.size(); i++) {
            Map<String, String> s = new HashMap<>();
            s.put("name", servicesList.get(i).name);
            s.put("price", servicesList.get(i).price);
            servicesMap.put("service" + i, s);
        }
        salonData.put("services", servicesMap);

        salonRef.updateChildren(salonData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Salon Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showServiceDialog(ServiceItem item, int position) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_service, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etDialogServiceName);
        TextInputEditText etPrice = dialogView.findViewById(R.id.etDialogServicePrice);

        if (item != null) {
            etName.setText(item.name);
            etPrice.setText(item.price.replace("₹ ", ""));
        }

        new AlertDialog.Builder(this)
                .setTitle(item == null ? "Add Service" : "Edit Service")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String price = etPrice.getText().toString().trim();
                    if (!name.isEmpty() && !price.isEmpty()) {
                        if (item == null) {
                            servicesList.add(new ServiceItem(name, "₹ " + price));
                        } else {
                            item.name = name;
                            item.price = "₹ " + price;
                        }
                        adapter.notifyDataSetChanged();
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
        findViewById(R.id.navServices).setOnClickListener(v -> {
            startActivity(new Intent(this, ManageSlotsActivity.class));
            finish();
        });
    }

    public static class ServiceItem {
        public String name, price;
        public ServiceItem(String name, String price) {
            this.name = name;
            this.price = price;
        }
    }

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
package com.example.salonsync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // --- FIREBASE CONNECTION CHECK ---
        DatabaseReference connectedRef = FirebaseDatabase.getInstance("https://salonsync-a4c38-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (connected != null && connected) {
                    Log.d("FirebaseStatus", "Connected to Firebase!");
                } else {
                    Log.d("FirebaseStatus", "Not connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseStatus", "Listener was cancelled: " + error.getMessage());
            }
        });
        // --- END CONNECTION CHECK ---

        Button getStarted = findViewById(R.id.btn_get_started);

        if (getStarted != null) {
            getStarted.setOnClickListener(v -> {
                Intent intent = new Intent(SplashScreen.this, SignupActivity.class);
                startActivity(intent);
            });
        }
    }
}
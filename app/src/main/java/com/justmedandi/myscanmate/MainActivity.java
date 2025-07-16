package com.justmedandi.myscanmate;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerKelas;
    TextView tvGreeting;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // sesuaikan dengan layout utama kamu

        recyclerKelas = findViewById(R.id.recyclerKelas);
        recyclerKelas.setLayoutManager(new LinearLayoutManager(this));
        recyclerKelas.setAdapter(new KelasAdapter());

        tvGreeting = findViewById(R.id.tvGreeting);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            tvGreeting.setText("Halo👋\n" + name);
                        }
                    })
                    .addOnFailureListener(e -> {
                        tvGreeting.setText("Halo, User");
                    });
        }

        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_scan) {
                    startActivity(new Intent(MainActivity.this, ScanQRActivity.class));
                    return true;
                } else if (id == R.id.nav_booking) {
                    startActivity(new Intent(MainActivity.this, BookingActivity.class));
                    return true;
                } else if (id == R.id.nav_delegasi) {
                    startActivity(new Intent(MainActivity.this, DelegasiActivity.class));
                    return true;
                }
                return false;
            }
        });

    }
}

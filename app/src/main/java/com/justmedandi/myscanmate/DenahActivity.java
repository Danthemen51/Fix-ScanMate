package com.justmedandi.myscanmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DenahActivity extends AppCompatActivity {

    ImageView imageDenahKampus;
    BottomNavigationView bottomNav;
    String currentRole = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denah);

        imageDenahKampus = findViewById(R.id.imageDenahKampus);
        imageDenahKampus.setImageResource(R.drawable.denah_kampus);

        // Ambil role dari intent atau simpan di SharedPreferences sebelumnya
        Intent intent = getIntent();
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentRole = prefs.getString("role", "");
        // Pastikan kamu kirim role dari MainActivity

        bottomNav = findViewById(R.id.bottom_Nav);
        bottomNav.setSelectedItemId(R.id.nav_denah); // tandai sebagai halaman aktif

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(DenahActivity.this, MainActivity.class));
                    finish(); // biar nggak tumpuk activity
                    return true;

                } else if (id == R.id.nav_scan) {
                    startActivity(new Intent(DenahActivity.this, ScanQRActivity.class));
                    finish();
                    return true;

                } else if (id == R.id.nav_delegasi) {
                    if (currentRole != null &&
                            (currentRole.equalsIgnoreCase("Ketua") || currentRole.equalsIgnoreCase("Wakil"))) {
                        startActivity(new Intent(DenahActivity.this, DelegasiActivity.class));
                        finish();
                    } else {
                        Toast.makeText(DenahActivity.this, "Hanya Ketua atau Wakil yang bisa akses Delegasi", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                } else if (id == R.id.nav_denah) {
                    return true;
                }

                return false;
            }
        });
    }
}

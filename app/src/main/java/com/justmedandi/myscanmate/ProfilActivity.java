package com.justmedandi.myscanmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilActivity extends AppCompatActivity {

    private TextView textNama, textRole;
    private Button btnSignOut;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        textNama = findViewById(R.id.textNama);
        textRole = findViewById(R.id.textRole);
        btnSignOut = findViewById(R.id.btnSignOut);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Ambil 'nama' jika ada, kalau nggak ada coba 'name'
                            String nama = documentSnapshot.getString("nama");
                            if (nama == null || nama.isEmpty()) {
                                nama = documentSnapshot.getString("name"); // fallback
                            }

                            String role = documentSnapshot.getString("role");

                            textNama.setText(nama != null && !nama.isEmpty() ? nama : "Nama belum diisi");
                            textRole.setText(ubahRole(role));
                        } else {
                            Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal memuat profil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Pengguna belum login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, activity_login.class));
            finish();
        }

        btnSignOut.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, activity_login.class));
            finish();
        });
    }

    private String ubahRole(String kode) {
        if (kode == null) return "Mahasiswa Biasa";
        switch (kode.toLowerCase()) {
            case "admin":
                return "Admin";
            case "ketua":
                return "Ketua";
            case "wakil":
                return "Wakil";
            default:
                return "Mahasiswa Biasa";
        }
    }
}

package com.justmedandi.myscanmate;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailRuanganActivity extends AppCompatActivity {

    TextView namaRuanganTextView, statusTextView, kelasTextView, matkulTextView, dosenTextView, jamTextView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ruangan);

        db = FirebaseFirestore.getInstance();

        // Ambil ID dari intent
        String idRuangan = getIntent().getStringExtra("idRuangan");

        // Inisialisasi UI
        namaRuanganTextView = findViewById(R.id.namaRuanganTextView);
        statusTextView = findViewById(R.id.statusTextView);
        kelasTextView = findViewById(R.id.kelasTextView);
        matkulTextView = findViewById(R.id.matkulTextView);
        dosenTextView = findViewById(R.id.dosenTextView);
        jamTextView = findViewById(R.id.jamTextView);

        // Ambil data dari Firestore
        db.collection("ruangan").document(idRuangan)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        namaRuanganTextView.setText(documentSnapshot.getString("nama"));
                        statusTextView.setText(documentSnapshot.getString("status"));
                        kelasTextView.setText(documentSnapshot.getString("kelas"));
                        matkulTextView.setText(documentSnapshot.getString("mataKuliah"));
                        dosenTextView.setText(documentSnapshot.getString("dosen"));
                        jamTextView.setText(documentSnapshot.getString("jam"));
                    } else {
                        Toast.makeText(this, "Data ruangan tidak ditemukan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal ambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
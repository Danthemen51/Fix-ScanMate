package com.justmedandi.myscanmate;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditKelasActivity extends AppCompatActivity {

    private EditText edtNama, edtWaktu, edtTanggal;
    private Button btnSimpan;

    private FirebaseFirestore db;
    private String kelasId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kelas);

        edtNama = findViewById(R.id.edtNamaKelas);
        edtWaktu = findViewById(R.id.edtWaktuKelas);
        edtTanggal = findViewById(R.id.edtTanggalKelas);
        btnSimpan = findViewById(R.id.btnSimpanKelas);

        db = FirebaseFirestore.getInstance();

        kelasId = getIntent().getStringExtra("kelasId");

        if (kelasId != null) {
            loadDataKelas(kelasId);
        } else {
            Toast.makeText(this, "ID kelas tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnSimpan.setOnClickListener(v -> updateDataKelas());
    }

    private void loadDataKelas(String id) {
        db.collection("kelas").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nama = documentSnapshot.getString("nama");
                        String waktu = documentSnapshot.getString("waktu");
                        String tanggal = documentSnapshot.getString("tanggal");

                        edtNama.setText(nama);
                        edtWaktu.setText(waktu);
                        edtTanggal.setText(tanggal);
                    } else {
                        Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void updateDataKelas() {
        String nama = edtNama.getText().toString().trim();
        String waktu = edtWaktu.getText().toString().trim();
        String tanggal = edtTanggal.getText().toString().trim();

        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(waktu) || TextUtils.isEmpty(tanggal)) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("nama", nama);
        update.put("waktu", waktu);
        update.put("tanggal", tanggal);

        DocumentReference kelasRef = db.collection("kelas").document(kelasId);
        kelasRef.update(update)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Kelas berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menyimpan perubahan", Toast.LENGTH_SHORT).show();
                });
    }
}

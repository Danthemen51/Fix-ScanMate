package com.justmedandi.myscanmate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TambahKelasActivity extends AppCompatActivity {

    private EditText editNamaKelas, editWaktu;
    private Button btnTambahKelas;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kelas);

        editNamaKelas = findViewById(R.id.editNamaKelas);
        editWaktu = findViewById(R.id.editWaktu);
        btnTambahKelas = findViewById(R.id.btnSimpanKelas);

        firestore = FirebaseFirestore.getInstance();

        btnTambahKelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahKelasBaru();
            }
        });
    }

    private void tambahKelasBaru() {
        String nama = editNamaKelas.getText().toString().trim();
        String waktu = editWaktu.getText().toString().trim();

        if (nama.isEmpty() || waktu.isEmpty()) {
            Toast.makeText(this, "Nama dan Waktu harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dapatkan tanggal hari ini dalam format yyyy-MM-dd
        String tanggalHariIni = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> kelasMap = new HashMap<>();
        kelasMap.put("nama", nama);
        kelasMap.put("waktu", waktu);
        kelasMap.put("tersedia", true);        // ✅ masih tersedia
        kelasMap.put("booked", false);         // ✅ belum dibooking
        kelasMap.put("bookedBy", "");          // ✅ belum ada yang booking
        kelasMap.put("tanggal", tanggalHariIni); // untuk validasi harian

        firestore.collection("kelas")
                .add(kelasMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Kelas berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menambahkan kelas", Toast.LENGTH_SHORT).show();
                });
    }
}

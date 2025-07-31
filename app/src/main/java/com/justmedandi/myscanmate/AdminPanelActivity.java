package com.justmedandi.myscanmate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminPanelActivity extends AppCompatActivity {

    private EditText editNama, editWaktu, editTanggal;
    private Button btnTambahKelas;
    private RecyclerView recyclerView;
    private KelasAdapter adapter;
    private ArrayList<KelasModel> kelasList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        editNama = findViewById(R.id.editNama);
        editWaktu = findViewById(R.id.editWaktu);
        editTanggal = findViewById(R.id.editTanggal);
        btnTambahKelas = findViewById(R.id.btnTambahKelas);
        recyclerView = findViewById(R.id.recyclerKelas);

        db = FirebaseFirestore.getInstance();
        kelasList = new ArrayList<>();
        adapter = new KelasAdapter(this, kelasList, true); // true untuk mode admin

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadKelas();

        btnTambahKelas.setOnClickListener(v -> tambahKelas());
    }

    private void tambahKelas() {
        String nama = editNama.getText().toString();
        String waktu = editWaktu.getText().toString();
        String tanggal = editTanggal.getText().toString();

        if (nama.isEmpty() || waktu.isEmpty() || tanggal.isEmpty()) {
            Toast.makeText(this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("nama", nama);
        data.put("waktu", waktu);
        data.put("tanggal", tanggal);
        data.put("tersedia", true);
        data.put("dibooking", false);
        data.put("bookedBy", "");
        data.put("updateAt", FieldValue.serverTimestamp());

        db.collection("kelas").document(nama)
                .set(data)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Kelas ditambahkan", Toast.LENGTH_SHORT).show();
                    loadKelas();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menambahkan", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadKelas() {
        kelasList.clear();
        db.collection("kelas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            KelasModel kelas = doc.toObject(KelasModel.class);
                            kelas.setId(doc.getId());
                            kelasList.add(kelas);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}

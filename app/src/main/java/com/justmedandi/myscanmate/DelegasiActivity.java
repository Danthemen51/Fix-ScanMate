package com.justmedandi.myscanmate;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DelegasiActivity extends AppCompatActivity {

    TextView tvDelegasiInfo;
    Spinner spinnerMahasiswa;
    Button btnDelegasikan;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    String currentUid;
    String currentRole;
    ArrayList<String> listMahasiswa = new ArrayList<>();
    ArrayList<String> listUidMahasiswa = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegasi);

        tvDelegasiInfo = findViewById(R.id.tvDelegasiInfo);
        spinnerMahasiswa = findViewById(R.id.spinnerMahasiswa);
        btnDelegasikan = findViewById(R.id.btnDelegasikan);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();

        // Ambil role user sekarang
        db.collection("users").document(currentUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentRole = documentSnapshot.getString("role");

                    // Tampilkan role sekarang
                    tvDelegasiInfo.setText("Role kamu: " + currentRole);

                    // Nonaktifkan tombol jika bukan ketua/wakil
                    if (!currentRole.equalsIgnoreCase("ketua") && !currentRole.equalsIgnoreCase("wakil")) {
                        btnDelegasikan.setEnabled(false);
                        Toast.makeText(this, "Kamu bukan Ketua/Wakil, tidak bisa mendelegasikan", Toast.LENGTH_SHORT).show();
                    } else {
                        loadMahasiswaBiasa();
                    }
                });

        btnDelegasikan.setOnClickListener(v -> {
            int index = spinnerMahasiswa.getSelectedItemPosition();
            if (index >= 0 && index < listUidMahasiswa.size()) {
                String targetUid = listUidMahasiswa.get(index);
                delegasikanRole(targetUid);
            }
        });
    }

    private void loadMahasiswaBiasa() {
        db.collection("users")
                .whereEqualTo("role", "mahasiswa biasa")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    listMahasiswa.clear();
                    listUidMahasiswa.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        String uid = doc.getId();
                        listMahasiswa.add(name);
                        listUidMahasiswa.add(uid);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listMahasiswa);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMahasiswa.setAdapter(adapter);
                });
    }

    private void delegasikanRole(String targetUid) {
        // Tambahkan data delegasi ke Firestore
        Map<String, Object> delegasiData = new HashMap<>();
        delegasiData.put("fromUid", currentUid);
        delegasiData.put("toUid", targetUid);
        delegasiData.put("roleDelegated", currentRole);
        delegasiData.put("timestamp", Timestamp.now());

        db.collection("delegasi")
                .document(currentUid) // satu dokumen per ketua/wakil
                .set(delegasiData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Berhasil delegasikan ke " + spinnerMahasiswa.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal delegasi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

package com.justmedandi.myscanmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerKelas;
    private TextView tvGreeting, tvRoleBadge;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAddKelas;

    private FirebaseFirestore firestore;
    private FirebaseUser user;

    private ArrayList<KelasModel> kelasList = new ArrayList<>();
    private KelasAdapter adapter;

    private String currentRole = "-";
    private String currentName = "User";
    private ImageView imgProfile;
    private SearchView searchView;
    private ImageView btnFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        btnFilter = findViewById(R.id.btnFilter);

// Tombol filter menjadi pemicu pencarian
        btnFilter.setOnClickListener(v -> {
            String query = searchView.getQuery().toString().trim();
            if (!query.isEmpty()) {
                filterKelas(query); // Fungsi untuk filter RecyclerView
            } else {
                filterKelas(""); // Jika kosong, tampilkan semua
            }
        });


        initViews();
        initFirebase();
        setupBottomNavigation();
        loadUserData();
    }

    private void initViews() {
        recyclerKelas = findViewById(R.id.recyclerKelas);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvRoleBadge = findViewById(R.id.tvRoleBadge);
        imgProfile = findViewById(R.id.imgProfile);
        bottomNav = findViewById(R.id.bottomNav);
        fabAddKelas = findViewById(R.id.fabAddKelas);

        fabAddKelas.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TambahKelasActivity.class);
            startActivity(intent);
        });

        imgProfile.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfilActivity.class);
            startActivity(intent);
        });

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterKelas(newText);
                return true;
            }
        });
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setupRecyclerView(boolean isAdmin) {
        adapter = new KelasAdapter(this, kelasList, isAdmin, kelas -> {
            if ("Ketua".equalsIgnoreCase(currentRole) || "Wakil".equalsIgnoreCase(currentRole)) {
                Intent intent = new Intent(MainActivity.this, BookingActivity.class);
                intent.putExtra("kelas_id", kelas.getId());
                intent.putExtra("kelas_nama", kelas.getNama());
                startActivity(intent);
            } else {
                showAlert("Akses Ditolak ❌", "Hanya Ketua atau Wakil yang dapat melakukan booking kelas.");
            }
        });

        recyclerKelas.setLayoutManager(new LinearLayoutManager(this));
        recyclerKelas.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_scan) {
                startActivity(new Intent(this, ScanQRActivity.class));
                return true;
            } else if (id == R.id.nav_denah) {
                startActivity(new Intent(this, DenahActivity.class));
                return true;
            } else if (id == R.id.nav_delegasi) {
                if (currentRole.equalsIgnoreCase("Ketua") || currentRole.equalsIgnoreCase("Wakil")) {
                    startActivity(new Intent(this, DelegasiActivity.class));
                } else {
                    showAlert("Akses Ditolak 🔒", "Maaf, hanya Ketua dan Wakil kelas yang dapat mengakses fitur Delegasi.");
                }
                return true;
            }
            return false;
        });
    }

    private void loadUserData() {
        if (user != null) {
            firestore.collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            currentName = documentSnapshot.getString("name") != null ? documentSnapshot.getString("name") : "User";
                            currentRole = documentSnapshot.getString("role") != null ? documentSnapshot.getString("role") : "-";

                            tvGreeting.setText("Halo👋\n" + currentName);
                            tvRoleBadge.setText(currentRole);

                            if (!"Ketua".equalsIgnoreCase(currentRole) && !"Wakil".equalsIgnoreCase(currentRole)) {
                                bottomNav.getMenu().findItem(R.id.nav_delegasi)
                                        .setIcon(R.drawable.ic_delegasi_disabled);
                            }

                            if ("Admin".equalsIgnoreCase(currentRole)) {
                                fabAddKelas.setVisibility(View.VISIBLE);
                            } else {
                                fabAddKelas.setVisibility(View.GONE);
                            }

                            setupRecyclerView("admin".equalsIgnoreCase(currentRole));
                            loadKelasData();
                        } else {
                            showDefaultUserInfo();
                        }
                    })
                    .addOnFailureListener(e -> showDefaultUserInfo());
        } else {
            showDefaultUserInfo();
        }
    }

    private void showDefaultUserInfo() {
        tvGreeting.setText("Halo, User");
        tvRoleBadge.setText("-");
        currentRole = "-";
        fabAddKelas.setVisibility(View.GONE);
        setupRecyclerView(false);
        loadKelasData();
    }

    private void loadKelasData() {
        firestore.collection("kelas")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    kelasList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        KelasModel kelas = new KelasModel();
                        kelas.setId(doc.getId());
                        kelas.setNama(doc.getString("nama") != null ? doc.getString("nama") : "-");
                        kelas.setWaktu(doc.getString("waktu") != null ? doc.getString("waktu") : "-");
                        kelas.setBooked(doc.getBoolean("booked") != null && doc.getBoolean("booked"));
                        kelas.setTersedia(doc.getBoolean("tersedia") != null ? doc.getBoolean("tersedia") : true);
                        kelas.setBookedBy(doc.getString("bookedBy") != null ? doc.getString("bookedBy") : "-");
                        kelas.setTanggal(doc.getString("tanggal") != null ? doc.getString("tanggal") : "-");

                        if (kelas.isBooked() && kelas.getTanggal() != null) {
                            try {
                                String waktu = kelas.getWaktu().replace("–", "-");
                                String[] waktuSplit = waktu.split("-");
                                if (waktuSplit.length >= 2) {
                                    String waktuSelesai = waktuSplit[1].trim();
                                    String waktuSekarang = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                                    String tanggalSekarang = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    String tanggalBooking = kelas.getTanggal();

                                    if (tanggalBooking.equals(tanggalSekarang)) {
                                        if (waktuSekarang.compareTo(waktuSelesai) > 0) {
                                            resetKelas(kelas);
                                        }
                                    } else {
                                        resetKelas(kelas);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        kelasList.add(kelas);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    kelasList.clear();
                    adapter.notifyDataSetChanged();
                    showAlert("Gagal Memuat Data", "Tidak dapat mengambil data kelas dari server.");
                });
    }

    private void resetKelas(KelasModel kelas) {
        firestore.collection("kelas").document(kelas.getId())
                .update("booked", false,
                        "tersedia", true,
                        "bookedBy", "",
                        "tanggal", "")
                .addOnSuccessListener(aVoid -> {
                    kelas.setBooked(false);
                    kelas.setTersedia(true);
                    kelas.setBookedBy("");
                    kelas.setTanggal("");
                });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadKelasData();
    }

    private void filterKelas(String text) {
        List<KelasModel> filteredList = new ArrayList<>();
        for (KelasModel item : kelasList) {
            if (item.getNama().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        adapter.updateList(filteredList);
    }


}

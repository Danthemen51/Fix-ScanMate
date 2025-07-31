package com.justmedandi.myscanmate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookingActivity extends AppCompatActivity {

    EditText etTanggal, etJamMulai, etJamSelesai, etKeperluan;
    Button btnKirim;

    String kelasId, kelasNama;
    FirebaseFirestore firestore;
    FirebaseUser user;

    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        etTanggal = findViewById(R.id.etTanggal);
        etJamMulai = findViewById(R.id.etJamMulai);
        etJamSelesai = findViewById(R.id.etJamSelesai);
        etKeperluan = findViewById(R.id.etKeperluan);
        btnKirim = findViewById(R.id.btnKirim);

        // Ambil data kelas dari Intent
        kelasId = getIntent().getStringExtra("kelas_id");
        kelasNama = getIntent().getStringExtra("kelas_nama");

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Pilih tanggal
        etTanggal.setOnClickListener(view -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(BookingActivity.this,
                    (view1, y, m, d) -> etTanggal.setText(String.format("%02d/%02d/%04d", d, m + 1, y)),
                    year, month, day);
            datePicker.show();
        });

        // Pilih jam mulai
        etJamMulai.setOnClickListener(view -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePicker = new TimePickerDialog(BookingActivity.this,
                    (view1, h, m) -> etJamMulai.setText(String.format("%02d:%02d", h, m)),
                    hour, minute, true);
            timePicker.show();
        });

        // Pilih jam selesai
        etJamSelesai.setOnClickListener(view -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePicker = new TimePickerDialog(BookingActivity.this,
                    (view1, h, m) -> etJamSelesai.setText(String.format("%02d:%02d", h, m)),
                    hour, minute, true);
            timePicker.show();
        });

        // Kirim booking
        btnKirim.setOnClickListener(v -> kirimBooking());
    }

    private void kirimBooking() {
        String tanggal = etTanggal.getText().toString().trim();
        String jamMulai = etJamMulai.getText().toString().trim();
        String jamSelesai = etJamSelesai.getText().toString().trim();
        String keperluan = etKeperluan.getText().toString().trim();

        if (tanggal.isEmpty() || jamMulai.isEmpty() || jamSelesai.isEmpty() || keperluan.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua data terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        // Ambil data pengguna (kelas yang booking)
        firestore.collection("users").document(uid).get().addOnSuccessListener(userSnapshot -> {
            if (userSnapshot.exists()) {
                String kelasMahasiswa = userSnapshot.getString("kelas");

                Map<String, Object> bookingData = new HashMap<>();
                bookingData.put("kelasId", kelasId);
                bookingData.put("kelasNama", kelasNama);
                bookingData.put("userId", uid);
                bookingData.put("tanggal", tanggal);
                bookingData.put("jamMulai", jamMulai);
                bookingData.put("jamSelesai", jamSelesai);
                bookingData.put("keperluan", keperluan);
                bookingData.put("timestamp", System.currentTimeMillis());

                firestore.collection("bookings")
                        .add(bookingData)
                        .addOnSuccessListener(documentReference -> {
                            // Update status kelas jadi penuh
                            Map<String, Object> updateStatus = new HashMap<>();
                            updateStatus.put("tersedia", false);
                            updateStatus.put("dibooking", true);
                            updateStatus.put("bookedby", kelasMahasiswa);
                            updateStatus.put("tanggal", tanggal);
                            updateStatus.put("jamMulai", jamMulai);
                            updateStatus.put("jamSelesai", jamSelesai);

                            firestore.collection("kelas")
                                    .document(kelasId)
                                    .update(updateStatus)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Booking berhasil dan kelas ditandai penuh!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Booking berhasil, tapi update status gagal.", Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Gagal menyimpan data booking.", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Data pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal mengambil data pengguna.", Toast.LENGTH_SHORT).show();
        });
    }
}

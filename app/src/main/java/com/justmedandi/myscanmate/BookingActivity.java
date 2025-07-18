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

    Calendar calendar = Calendar.getInstance(); // Untuk date/time picker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        etTanggal = findViewById(R.id.etTanggal);
        etJamMulai = findViewById(R.id.etJamMulai);
        etJamSelesai = findViewById(R.id.etJamSelesai);
        etKeperluan = findViewById(R.id.etKeperluan);
        btnKirim = findViewById(R.id.btnKirim);

        // Ambil data dari Intent
        kelasId = getIntent().getStringExtra("kelas_id");
        kelasNama = getIntent().getStringExtra("kelas_nama");

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Tanggal Picker
        etTanggal.setOnClickListener(view -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity.this,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String tanggal = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        etTanggal.setText(tanggal);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Jam Mulai Picker
        etJamMulai.setOnClickListener(view -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this,
                    (view1, hourOfDay, minute1) -> {
                        String jam = String.format("%02d:%02d", hourOfDay, minute1);
                        etJamMulai.setText(jam);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        // Jam Selesai Picker
        etJamSelesai.setOnClickListener(view -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this,
                    (view1, hourOfDay, minute1) -> {
                        String jam = String.format("%02d:%02d", hourOfDay, minute1);
                        etJamSelesai.setText(jam);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

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

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("kelasId", kelasId);
        bookingData.put("kelasNama", kelasNama);
        bookingData.put("userId", user.getUid());
        bookingData.put("tanggal", tanggal);
        bookingData.put("jamMulai", jamMulai);
        bookingData.put("jamSelesai", jamSelesai);
        bookingData.put("keperluan", keperluan);
        bookingData.put("timestamp", System.currentTimeMillis()); // opsional sorting nanti

        firestore.collection("bookings")
                .add(bookingData)
                .addOnSuccessListener(documentReference -> {
                    // Update status kelas jadi tidak tersedia
                    Map<String, Object> updateStatus = new HashMap<>();
                    updateStatus.put("tersedia", false);
                    updateStatus.put("dibooking", true);

                    firestore.collection("kelas")
                            .document(kelasId)
                            .update(updateStatus)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Berhasil booking dan ruangan ditandai penuh!", Toast.LENGTH_SHORT).show();
                                finish(); // tutup activity setelah berhasil
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Booking berhasil tapi gagal update status kelas.", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal booking: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

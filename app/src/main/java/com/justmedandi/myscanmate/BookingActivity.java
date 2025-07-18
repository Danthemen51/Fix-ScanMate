package com.justmedandi.myscanmate;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookingActivity extends AppCompatActivity {

    EditText etTanggal, etJamMulai, etJamSelesai, etKeperluan;
    Button btnKirim;

    String kelasId, kelasNama;
    FirebaseFirestore firestore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        etTanggal = findViewById(R.id.etTanggal);
        etJamMulai = findViewById(R.id.etJamMulai);
        etJamSelesai = findViewById(R.id.etJamSelesai);
        etKeperluan = findViewById(R.id.etKeperluan);
        btnKirim = findViewById(R.id.btnKirim);

        kelasId = getIntent().getStringExtra("kelas_id");
        kelasNama = getIntent().getStringExtra("kelas_nama");

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        btnKirim.setOnClickListener(v -> kirimBooking());
    }

    private void kirimBooking() {
        String tanggal = etTanggal.getText().toString();
        String jamMulai = etJamMulai.getText().toString();
        String jamSelesai = etJamSelesai.getText().toString();
        String keperluan = etKeperluan.getText().toString();

        if (tanggal.isEmpty() || jamMulai.isEmpty() || jamSelesai.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show();
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

        firestore.collection("bookings")
                .add(bookingData)
                .addOnSuccessListener(documentReference -> {
                    // update status kelas jadi booked
                    firestore.collection("kelas")
                            .document(kelasId)
                            .update("booked", true);

                    Toast.makeText(this, "Booking berhasil!", Toast.LENGTH_SHORT).show();
                    finish(); // kembali ke main
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal booking: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

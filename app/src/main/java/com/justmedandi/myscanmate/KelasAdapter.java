package com.justmedandi.myscanmate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class KelasAdapter extends RecyclerView.Adapter<KelasAdapter.KelasViewHolder> {

    private List<KelasModel> listKelas = new ArrayList<>();
    private Context context;

    public KelasAdapter() {
        loadDataFromFirestore();
    }

    private void loadDataFromFirestore() {
        FirebaseFirestore.getInstance()
                .collection("kelas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listKelas.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        KelasModel kelas = doc.toObject(KelasModel.class);
                        listKelas.add(kelas);
                    }
                    notifyDataSetChanged();
                });
    }

    @NonNull
    @Override
    public KelasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_kelas, parent, false);
        return new KelasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KelasViewHolder holder, int position) {
        KelasModel kelas = listKelas.get(position);
        holder.nama.setText(kelas.getNama());
        holder.waktu.setText(kelas.getWaktu());

        // Status visual
        if (kelas.isTersedia()) {
            holder.status.setText("Tersedia");
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundResource(R.drawable.bg_status_tersedia);
        } else {
            holder.status.setText("Penuh");
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundResource(R.drawable.bg_status_penuh);
        }

        // Gambar gedung berdasarkan awalan nama
        if (kelas.getNama().startsWith("A")) {
            holder.imgGedung.setImageResource(R.drawable.ic_a1);
        } else if (kelas.getNama().startsWith("B")) {
            holder.imgGedung.setImageResource(R.drawable.ic_b1_tes);
        } else {
            holder.imgGedung.setImageResource(R.drawable.utb);
        }

        // Tombol Booking aktif hanya jika tersedia dan belum dibooking
        holder.btnBooking.setEnabled(kelas.isTersedia() && !kelas.isDibooking());

        holder.btnBooking.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");

                            if (role != null && (role.equalsIgnoreCase("ketua") || role.equalsIgnoreCase("wakil") || role.equalsIgnoreCase("delegasi"))) {
                                // Buka BookingActivity
                                Intent intent = new Intent(context, BookingActivity.class);
                                intent.putExtra("kelas_id", kelas.getId());
                                intent.putExtra("kelas_nama", kelas.getNama());
                                context.startActivity(intent);
                            } else {
                                // Tidak diizinkan booking
                                showDeniedPopup();
                            }
                        }
                    });
        });
    }

    private void showDeniedPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_booking_denied, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return listKelas.size();
    }

    public static class KelasViewHolder extends RecyclerView.ViewHolder {
        TextView nama, waktu, status;
        Button btnBooking;
        ImageView imgGedung;

        public KelasViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tvNamaKelas);
            waktu = itemView.findViewById(R.id.tvWaktuKelas);
            status = itemView.findViewById(R.id.tvStatusKelas);
            btnBooking = itemView.findViewById(R.id.btnBooking);
            imgGedung = itemView.findViewById(R.id.imgGedung);
        }
    }
}

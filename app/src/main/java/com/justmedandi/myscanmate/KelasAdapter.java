package com.justmedandi.myscanmate;

import android.content.Context;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class KelasAdapter extends RecyclerView.Adapter<KelasAdapter.KelasViewHolder> {

    private List<KelasModel> listKelas = new ArrayList<>();
    private Context context;

    public KelasAdapter() {
        this.context = context;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kelas, parent, false);
        return new KelasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KelasViewHolder holder, int position) {
        KelasModel kelas = listKelas.get(position);
        holder.nama.setText(kelas.getNama());
        holder.waktu.setText(kelas.getWaktu());

        // Status
        if (kelas.isTersedia()) {
            holder.status.setText("Tersedia");
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundResource(R.drawable.bg_status_tersedia);
        } else {
            holder.status.setText("Penuh");
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundResource(R.drawable.bg_status_penuh);
        }

        // Gambar huruf gedung berdasarkan nama ruang
        if (kelas.getNama().startsWith("A")) {
            holder.imgGedung.setImageResource(R.drawable.ic_a1);
        } else if (kelas.getNama().startsWith("B")) {
            holder.imgGedung.setImageResource(R.drawable.ic_b1_tes);
        } else {
            holder.imgGedung.setImageResource(R.drawable.utb); // optional fallback
        }

        // Tombol Booking
        holder.btnBooking.setEnabled(kelas.isTersedia() && !kelas.isDibooking());
        holder.btnBooking.setOnClickListener(v -> {
            kelas.setDibooking(true);
            FirebaseFirestore.getInstance()
                    .collection("kelas")
                    .document(kelas.getNama())
                    .update("dibooking", true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(holder.itemView.getContext(), "Kelas berhasil dibooking", Toast.LENGTH_SHORT).show();
                        holder.btnBooking.setEnabled(false);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(holder.itemView.getContext(), "Gagal booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
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

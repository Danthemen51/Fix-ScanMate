package com.justmedandi.myscanmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kelas, parent, false);
        return new KelasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KelasViewHolder holder, int position) {
        KelasModel kelas = listKelas.get(position);
        holder.nama.setText(kelas.getNama());
        holder.waktu.setText(kelas.getWaktu());
        holder.status.setText(kelas.isTersedia() ? "Tersedia" : "Penuh");
        holder.status.setTextColor(kelas.isTersedia() ? 0xFF388E3C : 0xFFD32F2F);

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

        public KelasViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tvNamaKelas);
            waktu = itemView.findViewById(R.id.tvWaktuKelas);
            status = itemView.findViewById(R.id.tvStatusKelas);
            btnBooking = itemView.findViewById(R.id.btnBooking);
        }
    }
}

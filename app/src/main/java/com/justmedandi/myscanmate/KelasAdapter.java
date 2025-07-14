package com.justmedandi.myscanmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

        // load foto kelas pakai Glide (kalau ada fotoUrl)
        if (kelas.getFotoUrl() != null && !kelas.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(kelas.getFotoUrl())
                    .into(holder.foto);
        } else {
            holder.foto.setImageResource(R.drawable.ic_placeholder); // default image
        }
    }

    @Override
    public int getItemCount() {
        return listKelas.size();
    }

    public static class KelasViewHolder extends RecyclerView.ViewHolder {
        TextView nama, waktu, status;
        ImageView foto;

        public KelasViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tvNamaKelas);
            waktu = itemView.findViewById(R.id.tvWaktuKelas);
            status = itemView.findViewById(R.id.tvStatusKelas);
            foto = itemView.findViewById(R.id.imgKelas);
        }
    }
}

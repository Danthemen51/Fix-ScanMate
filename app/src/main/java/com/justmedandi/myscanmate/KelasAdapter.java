package com.justmedandi.myscanmate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KelasAdapter extends RecyclerView.Adapter<KelasAdapter.KelasViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(KelasModel kelasModel);
    }

    private final List<KelasModel> listKelas;
    private final Context context;
    private final OnItemClickListener listener;
    private final boolean isAdmin;
    private final String currentUserId;
    private final FirebaseFirestore db;
    private List<KelasModel> kelasList = new ArrayList<>();


    // Constructor untuk user biasa
    public KelasAdapter(Context context, List<KelasModel> listKelas, OnItemClickListener listener) {
        this.context = context;
        this.listKelas = listKelas;
        this.listener = listener;
        this.isAdmin = false;
        this.currentUserId = FirebaseAuth.getInstance().getUid();
        this.db = FirebaseFirestore.getInstance();
    }

    // Constructor untuk admin
    public KelasAdapter(Context context, List<KelasModel> listKelas, boolean isAdmin) {
        this.context = context;
        this.listKelas = listKelas;
        this.listener = null;
        this.isAdmin = isAdmin;
        this.currentUserId = FirebaseAuth.getInstance().getUid();
        this.db = FirebaseFirestore.getInstance();
    }

    // ✅ Constructor untuk admin/user dengan listener
    public KelasAdapter(Context context, List<KelasModel> listKelas, boolean isAdmin, OnItemClickListener listener) {
        this.context = context;
        this.listKelas = listKelas;
        this.isAdmin = isAdmin;
        this.listener = listener;
        this.currentUserId = FirebaseAuth.getInstance().getUid();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public KelasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_kelas, parent, false);
        return new KelasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KelasViewHolder holder, int position) {
        KelasModel kelas = listKelas.get(position);

        holder.nama.setText(kelas.getNama());
        holder.waktu.setText(kelas.getWaktu());

        boolean isToday = isToday(kelas.getTanggal());
        boolean bisaDibooking = kelas.isTersedia() && !kelas.isBooked() && isToday;

        // Status tampilan
        if (bisaDibooking) {
            holder.status.setText("Tersedia");
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundResource(R.drawable.bg_status_tersedia);
            holder.bookedBy.setVisibility(View.GONE);
        } else {
            holder.status.setText("Penuh");
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundResource(R.drawable.bg_status_penuh);

            String pemesan = kelas.getBookedBy();
            if (pemesan != null && !pemesan.isEmpty()) {
                holder.bookedBy.setText("Telah dibooking oleh: " + pemesan);
                holder.bookedBy.setVisibility(View.VISIBLE);
            } else {
                holder.bookedBy.setVisibility(View.GONE);
            }
        }

        // Gambar gedung berdasarkan nama
        if (kelas.getNama().startsWith("A")) {
            holder.imgGedung.setImageResource(R.drawable.ic_a1);
        } else if (kelas.getNama().startsWith("B")) {
            holder.imgGedung.setImageResource(R.drawable.ic_b1_tes);
        } else {
            holder.imgGedung.setImageResource(R.drawable.utb); // default
        }

        // Penyesuaian tombol berdasarkan admin/user
        if (!isAdmin) {
            // User biasa
            holder.btnBooking.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);

            holder.btnBooking.setEnabled(bisaDibooking);
            holder.btnBooking.setAlpha(bisaDibooking ? 1.0f : 0.5f);

            holder.btnBooking.setOnClickListener(v -> {
                if (listener != null && bisaDibooking) {
                    listener.onItemClick(kelas);
                } else {
                    showDeniedPopup();
                }
            });

        } else {
            // Admin
            holder.btnBooking.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditKelasActivity.class);
                intent.putExtra("kelasId", kelas.getId());
                context.startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> showDeleteConfirmation(kelas));
        }
    }

    private void showDeniedPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_booking_denied, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmation(KelasModel kelas) {
        new AlertDialog.Builder(context)
                .setTitle("Hapus Kelas")
                .setMessage("Yakin ingin menghapus kelas " + kelas.getNama() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    DocumentReference docRef = db.collection("kelas").document(kelas.getId());
                    docRef.delete().addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Kelas dihapus", Toast.LENGTH_SHORT).show();
                        int index = listKelas.indexOf(kelas);
                        if (index != -1) {
                            listKelas.remove(index);
                            notifyItemRemoved(index);
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return listKelas.size();
    }

    public static class KelasViewHolder extends RecyclerView.ViewHolder {
        TextView nama, waktu, status, bookedBy;
        Button btnBooking;
        ImageView imgGedung;
        ImageButton btnEdit, btnDelete;

        public KelasViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.tvNamaKelas);
            waktu = itemView.findViewById(R.id.tvWaktuKelas);
            status = itemView.findViewById(R.id.tvStatusKelas);
            bookedBy = itemView.findViewById(R.id.tvBookedBy);
            btnBooking = itemView.findViewById(R.id.btnBooking);
            imgGedung = itemView.findViewById(R.id.imgGedung);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private boolean isToday(String tanggal) {
        if (tanggal == null) return false;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return today.equals(tanggal);
    }

    public void updateList(List<KelasModel> filteredList) {
        this.kelasList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }
}

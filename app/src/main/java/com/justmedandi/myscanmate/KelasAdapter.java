package com.justmedandi.myscanmate;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
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

    public KelasAdapter(Context context, List<KelasModel> listKelas, OnItemClickListener listener) {
        this.context = context;
        this.listKelas = listKelas;
        this.listener = listener;
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
        } else {
            holder.status.setText("Penuh");
            holder.status.setTextColor(Color.WHITE);
            holder.status.setBackgroundResource(R.drawable.bg_status_penuh);
        }

        // Gambar berdasarkan nama gedung
        if (kelas.getNama().startsWith("A")) {
            holder.imgGedung.setImageResource(R.drawable.ic_a1);
        } else if (kelas.getNama().startsWith("B")) {
            holder.imgGedung.setImageResource(R.drawable.ic_b1_tes);
        } else {
            holder.imgGedung.setImageResource(R.drawable.utb);
        }

        // Tombol aktif kalau bisa dibooking
        holder.btnBooking.setEnabled(bisaDibooking);
        holder.btnBooking.setAlpha(bisaDibooking ? 1.0f : 0.5f);

        holder.btnBooking.setOnClickListener(v -> {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            if (role != null && (
                                    role.equalsIgnoreCase("ketua") ||
                                            role.equalsIgnoreCase("wakil") ||
                                            role.equalsIgnoreCase("delegasi"))) {

                                listener.onItemClick(kelas); // Callback ke BookingActivity

                            } else {
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

    // Helper: cek apakah tanggal = hari ini
    private boolean isToday(String tanggal) {
        if (tanggal == null) return false;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return today.equals(tanggal);
    }
}

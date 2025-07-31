package com.justmedandi.myscanmate;

public class KelasModel {
    private String id;
    private String nama;
    private String waktu;
    private boolean tersedia;
    private boolean booked;
    private String tanggal;
    private String dibookingOleh;
    private String namaKelas;

    // Constructor kosong (wajib untuk Firebase)
    public KelasModel() {
    }

    // Constructor lengkap
    public KelasModel(String id, String nama, String waktu, boolean tersedia, boolean booked, String tanggal, String bookedBy, String namaKelas) {
        this.id = id;
        this.nama = nama;
        this.waktu = waktu;
        this.tersedia = tersedia;
        this.booked = booked;
        this.tanggal = tanggal;
        this.dibookingOleh = bookedBy;
        this.namaKelas = namaKelas;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getWaktu() {
        return waktu;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public boolean isBooked() {
        return booked;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getBookedBy() {
        return dibookingOleh;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public String getNamaKelas() {
        return namaKelas; // atau sesuai field
    }


    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setBookedBy(String bookedBy) {
        this.dibookingOleh = bookedBy;
    }
}

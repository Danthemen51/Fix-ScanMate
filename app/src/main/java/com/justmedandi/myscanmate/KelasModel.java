package com.justmedandi.myscanmate;

public class KelasModel {
    private String id;         // ID dokumen Firestore
    private String nama;
    private String waktu;
    private boolean tersedia;
    private boolean dibooking;

    public KelasModel() {
        // Diperlukan untuk Firestore
    }

    public KelasModel(String id, String nama, String waktu, boolean tersedia, boolean dibooking) {
        this.id = id;
        this.nama = nama;
        this.waktu = waktu;
        this.tersedia = tersedia;
        this.dibooking = dibooking;
    }

    // Getter dan Setter ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter dan Setter Nama
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    // Getter dan Setter Waktu
    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    // Getter dan Setter Tersedia
    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    // Getter dan Setter Dibooking
    public boolean isDibooking() {
        return dibooking;
    }

    public void setDibooking(boolean dibooking) {
        this.dibooking = dibooking;
    }
}

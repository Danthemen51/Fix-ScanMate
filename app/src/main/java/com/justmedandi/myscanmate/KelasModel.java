package com.justmedandi.myscanmate;

public class KelasModel {
    private String nama;
    private String waktu;
    private boolean tersedia;
    private boolean dibooking;

    public KelasModel() {
        // diperlukan untuk Firestore
    }

    public KelasModel(String nama, String waktu, boolean tersedia, boolean dibooking) {
        this.nama = nama;
        this.waktu = waktu;
        this.tersedia = tersedia;
        this.dibooking = dibooking;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public boolean isDibooking() {
        return dibooking;
    }

    public void setDibooking(boolean dibooking) {
        this.dibooking = dibooking;
    }
}

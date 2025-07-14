package com.justmedandi.myscanmate;

public class KelasModel {
    private String nama;
    private String waktu;
    private boolean tersedia;
    private String fotoUrl;
    private boolean dibooking;

    public KelasModel() {}

    public KelasModel(String nama, String waktu, boolean tersedia, String fotoUrl) {
        this.nama = nama;
        this.waktu = waktu;
        this.tersedia = tersedia;
        this.fotoUrl = fotoUrl;
        this.dibooking = false;
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

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public boolean isDibooking() {
        return dibooking;
    }

    public void setDibooking(boolean dibooking) {
        this.dibooking = dibooking;
    }
}

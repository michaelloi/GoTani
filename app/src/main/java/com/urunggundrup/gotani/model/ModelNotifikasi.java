package com.urunggundrup.gotani.model;

public class ModelNotifikasi {
    private String id_notifikasi, judul_notifikasi, isi_notifikasi, id_user, broadcast, created_date;

    public String getId_notifikasi() {
        return id_notifikasi;
    }

    public void setId_notifikasi(String id_notifikasi) {
        this.id_notifikasi = id_notifikasi;
    }

    public String getJudul_notifikasi() {
        return judul_notifikasi;
    }

    public void setJudul_notifikasi(String judul_notifikasi) {
        this.judul_notifikasi = judul_notifikasi;
    }

    public String getIsi_notifikasi() {
        return isi_notifikasi;
    }

    public void setIsi_notifikasi(String isi_notifikasi) {
        this.isi_notifikasi = isi_notifikasi;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}

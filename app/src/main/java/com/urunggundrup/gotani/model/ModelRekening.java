package com.urunggundrup.gotani.model;

public class ModelRekening {
    private String id_rekening_pembayaran, nama_bank_rekening_pembayaran, atas_nama_rekening_pembayaran, no_rekening_pembayaran;

    public String getId_rekening_pembayaran() {
        return id_rekening_pembayaran;
    }

    public void setId_rekening_pembayaran(String id_rekening_pembayaran) {
        this.id_rekening_pembayaran = id_rekening_pembayaran;
    }

    public String getNama_bank_rekening_pembayaran() {
        return nama_bank_rekening_pembayaran;
    }

    public void setNama_bank_rekening_pembayaran(String nama_bank_rekening_pembayaran) {
        this.nama_bank_rekening_pembayaran = nama_bank_rekening_pembayaran;
    }

    public String getAtas_nama_rekening_pembayaran() {
        return atas_nama_rekening_pembayaran;
    }

    public void setAtas_nama_rekening_pembayaran(String atas_nama_rekening_pembayaran) {
        this.atas_nama_rekening_pembayaran = atas_nama_rekening_pembayaran;
    }

    public String getNo_rekening_pembayaran() {
        return no_rekening_pembayaran;
    }

    public void setNo_rekening_pembayaran(String no_rekening_pembayaran) {
        this.no_rekening_pembayaran = no_rekening_pembayaran;
    }
}

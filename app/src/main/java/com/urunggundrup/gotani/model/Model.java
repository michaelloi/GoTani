package com.urunggundrup.gotani.model;

import java.util.List;

public class Model {
    String value,message;
    ModelUser user;
    List<ModelLokasi> list_lokasi;
    List<ModelStatusPesanan> list_status_pesanan;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModelUser getUser() {
        return user;
    }

    public void setUser(ModelUser user) {
        this.user = user;
    }

    public List<ModelLokasi> getList_lokasi() {
        return list_lokasi;
    }

    public void setList_lokasi(List<ModelLokasi> list_lokasi) {
        this.list_lokasi = list_lokasi;
    }

    public List<ModelStatusPesanan> getList_status_pesanan() {
        return list_status_pesanan;
    }

    public void setList_status_pesanan(List<ModelStatusPesanan> list_status_pesanan) {
        this.list_status_pesanan = list_status_pesanan;
    }
}

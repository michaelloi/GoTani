package com.urunggundrup.gotani.model;

import java.util.List;

public class Model {
    String value,message;
    ModelUser user;
    List<ModelLokasi> list_lokasi;
    List<ModelStatusPesanan> list_status_pesanan;
    List<ModelAlamatUser> list_alamat;
    List<ModelNotifikasi> list_notifikasi;
    List<ModelProdukPetani> list_produk_petani;
    List<ModelKategori> list_kategori;
    List<ModelProdukUser> list_produk_user;
    List<ModelKeranjang> list_keranjang;
    List<ModelRekening> list_rekening;

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

    public List<ModelAlamatUser> getList_alamat() {
        return list_alamat;
    }

    public void setList_alamat(List<ModelAlamatUser> list_alamat) {
        this.list_alamat = list_alamat;
    }

    public List<ModelNotifikasi> getList_notifikasi() {
        return list_notifikasi;
    }

    public void setList_notifikasi(List<ModelNotifikasi> list_notifikasi) {
        this.list_notifikasi = list_notifikasi;
    }

    public List<ModelProdukPetani> getList_produk_petani() {
        return list_produk_petani;
    }

    public void setList_produk_petani(List<ModelProdukPetani> list_produk_petani) {
        this.list_produk_petani = list_produk_petani;
    }

    public List<ModelKategori> getList_kategori() {
        return list_kategori;
    }

    public void setList_kategori(List<ModelKategori> list_kategori) {
        this.list_kategori = list_kategori;
    }

    public List<ModelProdukUser> getList_produk_user() {
        return list_produk_user;
    }

    public void setList_produk_user(List<ModelProdukUser> list_produk_user) {
        this.list_produk_user = list_produk_user;
    }

    public List<ModelKeranjang> getList_keranjang() {
        return list_keranjang;
    }

    public void setList_keranjang(List<ModelKeranjang> list_keranjang) {
        this.list_keranjang = list_keranjang;
    }

    public List<ModelRekening> getList_rekening() {
        return list_rekening;
    }

    public void setList_rekening(List<ModelRekening> list_rekening) {
        this.list_rekening = list_rekening;
    }
}

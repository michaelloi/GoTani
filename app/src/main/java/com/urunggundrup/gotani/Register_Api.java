package com.urunggundrup.gotani;

import com.urunggundrup.gotani.model.Model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Register_Api {
    @FormUrlEncoded
    @POST("Register.php")
    Call<Model> registerUser(
            @Field("nama") String nama,
            @Field("nohp") String nohp,
            @Field("username") String username,
            @Field("password") String password,
            @Field("lokasi") String lokasi,
            @Field("status") String status,
            @Field("namatoko") String namatoko
    );

    @FormUrlEncoded
    @POST("Login.php")
    Call<Model> loginUser(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("Add_Alamat.php")
    Call<Model> addAlamatUser(
            @Field("id_user") String id_user,
            @Field("judul_alamat") String judul_alamat,
            @Field("alamat") String alamat,
            @Field("nama_penerima") String nama_penerima,
            @Field("nohp_penerima") String nohp_penerima,
            @Field("status_alamat") String status_alamat
    );

    @FormUrlEncoded
    @POST("Add_Pesanan.php")
    Call<Model> addPesanan(
            @Field("id_user") String id_user,
            @Field("id_status_pesanan") String id_status_pesanan,
            @Field("id_alamat") String id_alamat,
            @Field("id_rekening_pembayaran") String id_rekening_pembayaran,
            @Field("list_id_keranjang") String list_id_keranjang,
            @Field("harga_pesanan") String harga_pesanan,
            @Field("harga_ongkir") String harga_ongkir,
            @Field("total_pembayaran") String total_pembayaran
    );

    @FormUrlEncoded
    @POST("Add_Keranjang.php")
    Call<Model> addKeranjang(
            @Field("id_produk") String id_produk,
            @Field("id_user") String id_user,
            @Field("jumlah_pesanan") String jumlah_pesanan
    );

    @FormUrlEncoded
    @POST("Delete_Keranjang_User.php")
    Call<Model> deleteKeranjangItem(
            @Field("idKeranjang") String idKeranjang
    );

    @FormUrlEncoded
    @POST("Edit_Keranjang_User.php")
    Call<Model> editKeranjangItem(
            @Field("idKeranjang") String idKeranjang,
            @Field("jumlahPesanan") String jumlahPesanan
    );

    @FormUrlEncoded
    @POST("ListLokasi.php")
    Call<Model> listLokasi(
            @Field("iduser") String iduser
    );

    @FormUrlEncoded
    @POST("ListStatusPesanan.php")
    Call<Model> listStatusPesanan(
            @Field("iduser") String iduser
    );

    @FormUrlEncoded
    @POST("ListAlamatUser.php")
    Call<Model> listAlamatUser(
            @Field("iduser") String iduser,
            @Field("status_alamat") String status_alamat
    );

    @FormUrlEncoded
    @POST("ListNotifikasi.php")
    Call<Model> listNotifikasi(
            @Field("id_user") String iduser,
            @Field("broadcast") String broadcast
    );

    @FormUrlEncoded
    @POST("ListProdukPetani.php")
    Call<Model> listProdukPetani(
            @Field("idtoko") String idtoko,
            @Field("idkategori") String idkategori,
            @Field("sortir") String sortir
    );

    @FormUrlEncoded
    @POST("ListKategori.php")
    Call<Model> listKategori(
            @Field("iduser") String iduser
    );

    @FormUrlEncoded
    @POST("ListProdukUserLimit.php")
    Call<Model> listProdukUserLimit(
            @Field("idkategori") String idkategori,
            @Field("sortir") String sortir
    );

    @FormUrlEncoded
    @POST("ListProdukUser.php")
    Call<Model> listProdukUser(
            @Field("idkategori") String idkategori,
            @Field("sortir") String sortir
    );

    @FormUrlEncoded
    @POST("List_Keranjang_User.php")
    Call<Model> listKeranjang(
            @Field("id_user") String id_user
    );

    @FormUrlEncoded
    @POST("List_Keranjang_User_Checkout.php")
    Call<Model> listKeranjangCheckout(
            @Field("listIdKeranjang") String listIdKeranjang
    );

    @FormUrlEncoded
    @POST("ListRekening.php")
    Call<Model> listRekening(
            @Field("idUser") String idUser
    );
}

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
    @POST("Add_Keranjang.php")
    Call<Model> addKeranjang(
            @Field("id_produk") String id_produk,
            @Field("id_user") String id_user,
            @Field("jumlah_pesanan") String jumlah_pesanan
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
}

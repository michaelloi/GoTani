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
    @POST("ListLokasi.php")
    Call<Model> listLokasi(
            @Field("iduser") String iduser
    );

    @FormUrlEncoded
    @POST("ListStatusPesanan.php")
    Call<Model> listStatusPesanan(
            @Field("iduser") String iduser
    );
}

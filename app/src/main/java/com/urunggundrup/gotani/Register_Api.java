package com.urunggundrup.gotani;

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

}

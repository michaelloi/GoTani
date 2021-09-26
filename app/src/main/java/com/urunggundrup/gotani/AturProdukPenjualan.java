package com.urunggundrup.gotani;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.urunggundrup.gotani.adapter.AdapterAlamatUser;
import com.urunggundrup.gotani.adapter.AdapterProdukPetani;
import com.urunggundrup.gotani.adapter.AdapterSpinnerKategoriProduk;
import com.urunggundrup.gotani.adapter.AdapterSpinnerStatusPesanan;
import com.urunggundrup.gotani.adapter.AdapterSpinnerUrutkan;
import com.urunggundrup.gotani.databinding.ActivityAturProdukPenjualanBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelKategori;
import com.urunggundrup.gotani.model.ModelProdukPetani;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AturProdukPenjualan extends AppCompatActivity {

    private ActivityAturProdukPenjualanBinding binding;
    SessionManager sessionManager;
    AdapterProdukPetani adapterProdukPetani;
    AdapterSpinnerKategoriProduk adapterSpinnerKategoriProduk;
    AdapterSpinnerUrutkan adapterSpinnerUrutkan;
    String sKategoriProduk="0";
    String sUrutkan;
    String sId;
    List<ModelProdukPetani> listProdukPetani = new ArrayList<>();
    List<ModelKategori> listKategori = new ArrayList<>();
    List<String> listNamaKategori = new ArrayList<>();
    List<String> listUrutkan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Atur Produk");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityAturProdukPenjualanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //membaca session aplikasi
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        sId = user.get(SessionManager.USER_ID);

        //spinner kategori produk
        loadListKategoriProduk("");
        binding.spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(binding.spinnerKategori.getSelectedItemPosition()!=0){
                    sKategoriProduk = listKategori.get(binding.spinnerKategori.getSelectedItemPosition()-1).getId_kategori();
                    loadListProdukPenjualan(sId, sKategoriProduk, sUrutkan);
                }else{
                    sKategoriProduk = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sKategoriProduk = "0";
            }
        });

        //spinner urutkan berdasarkan
        listUrutkan = Arrays.asList(getResources().getStringArray(R.array.urutkan_arrays));
        adapterSpinnerUrutkan = new AdapterSpinnerUrutkan(getApplicationContext(), listUrutkan);
        binding.spinnerUrutkan.setAdapter(adapterSpinnerUrutkan);
        binding.spinnerUrutkan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sUrutkan = listUrutkan.get(binding.spinnerUrutkan.getSelectedItemPosition());
                loadListProdukPenjualan(sId, sKategoriProduk, sUrutkan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sUrutkan = "";
            }
        });

        //load list produk penjualan
        loadListProdukPenjualan(sId, sKategoriProduk, sUrutkan);

        //swipe refresh action
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadListProdukPenjualan(sId, sKategoriProduk, sUrutkan);
                binding.swipeRefresh.setRefreshing(false);
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent goToHome = new Intent(AturProdukPenjualan.this, MainActivity.class);
        goToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToHome);
        return true;
    }

    private void loadListKategoriProduk(String s) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listKategori(s);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    listKategori = response.body().getList_kategori();

                    listNamaKategori.add("Semua Kategori");

                    for(int i=0;i<listKategori.size();i++){
                        listNamaKategori.add(listKategori.get(i).getNama_kategori());
                    }

                    adapterSpinnerKategoriProduk = new AdapterSpinnerKategoriProduk(getApplicationContext(), listNamaKategori);
                    binding.spinnerKategori.setAdapter(adapterSpinnerKategoriProduk);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(AturProdukPenjualan.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadListProdukPenjualan(String idUser, String kategoriProduk, String urutkan) {
        binding.pesanProgress.setVisibility(View.VISIBLE);
        binding.recyclerProdukPetani.setVisibility(View.GONE);
        binding.pesanMaaf.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listProdukPetani(idUser, kategoriProduk, urutkan);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    binding.pesanProgress.setVisibility(View.GONE);
                    listProdukPetani = response.body().getList_produk_petani();
                    if(listProdukPetani.size()<1){
                        binding.pesanMaaf.setVisibility(View.VISIBLE);
                        binding.recyclerProdukPetani.setVisibility(View.GONE);
                    }else{
                        binding.pesanMaaf.setVisibility(View.GONE);
                        binding.recyclerProdukPetani.setVisibility(View.VISIBLE);
                        adapterProdukPetani = new AdapterProdukPetani(AturProdukPenjualan.this, listProdukPetani);
                        binding.recyclerProdukPetani.setAdapter(adapterProdukPetani);
                    }
                }else{
                    binding.pesanProgress.setVisibility(View.GONE);
                    binding.recyclerProdukPetani.setVisibility(View.GONE);
                    binding.pesanMaaf.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                binding.pesanProgress.setVisibility(View.GONE);
                binding.recyclerProdukPetani.setVisibility(View.GONE);
                binding.pesanMaaf.setVisibility(View.GONE);
                Toast.makeText(AturProdukPenjualan.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });

    }




}
package com.urunggundrup.gotani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.urunggundrup.gotani.adapter.AdapterPesanan;
import com.urunggundrup.gotani.adapter.AdapterPesananToko;
import com.urunggundrup.gotani.databinding.ActivityKeranjangBinding;
import com.urunggundrup.gotani.databinding.ActivityPesananTokoBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelPesanan;
import com.urunggundrup.gotani.model.ModelPesananToko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityPesananToko extends AppCompatActivity {

    private ActivityPesananTokoBinding binding;
    Intent getData;
    SessionManager sessionManager;
    String sIdToko;
    List<ModelPesananToko> listPesananToko = new ArrayList<>();
    AdapterPesananToko adapterPesananToko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData = getIntent();
        getSupportActionBar().setTitle(getData.getStringExtra("judulActivity"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityPesananTokoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try{
            //membaca session aplikasi
            sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            sIdToko = user.get(SessionManager.USER_ID_TOKO);

            //request data list pesanan
            loadListPesananToko(sIdToko, getData.getStringExtra("statusPesanan"));

            //swipe refresh action
            binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadListPesananToko(sIdToko, getData.getStringExtra("statusPesanan"));
                    binding.swipeRefresh.setRefreshing(false);
                }
            });

            //set data list alamat to recyclerview
            adapterPesananToko = new AdapterPesananToko(ActivityPesananToko.this, listPesananToko);
            RecyclerView.LayoutManager pesananLayout = new GridLayoutManager(getApplicationContext(), 1);
            binding.recyclerPesanan.setLayoutManager(pesananLayout);
            binding.recyclerPesanan.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerPesanan.setAdapter(adapterPesananToko);
        }catch (Exception e){
            System.out.println();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent goToHome = new Intent(ActivityPesananToko.this, MainActivity.class);
        goToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToHome);
        return true;
    }

    private void loadListPesananToko(String idToko, String idStatusPesanan) {
        binding.pesanProgress.setVisibility(View.VISIBLE);
        binding.recyclerPesanan.setVisibility(View.GONE);
        binding.pesanMaaf.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listPesananToko(idToko, idStatusPesanan);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if(response.body().getValue().equalsIgnoreCase("1")){
                    binding.pesanProgress.setVisibility(View.GONE);
                    listPesananToko = response.body().getList_pesananToko();
                    if(listPesananToko.size()<1){
                        binding.pesanMaaf.setVisibility(View.VISIBLE);
                        binding.recyclerPesanan.setVisibility(View.GONE);
                    }else{
                        binding.pesanMaaf.setVisibility(View.GONE);
                        binding.recyclerPesanan.setVisibility(View.VISIBLE);
                        adapterPesananToko = new AdapterPesananToko(ActivityPesananToko.this, listPesananToko);
                        binding.recyclerPesanan.setAdapter(adapterPesananToko);
                    }
                }else{
                    binding.pesanProgress.setVisibility(View.GONE);
                    binding.recyclerPesanan.setVisibility(View.GONE);
                    binding.pesanMaaf.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                binding.pesanProgress.setVisibility(View.GONE);
                binding.recyclerPesanan.setVisibility(View.GONE);
                binding.pesanMaaf.setVisibility(View.GONE);
                Toast.makeText(ActivityPesananToko.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
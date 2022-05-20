package com.urunggundrup.gotani;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.urunggundrup.gotani.adapter.AdapterAlamatUser;
import com.urunggundrup.gotani.adapter.AdapterSpinnerLokasi;
import com.urunggundrup.gotani.databinding.ActivityAturAlamatPengirimanBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelAlamatUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AturAlamatPengiriman extends AppCompatActivity {

    private ActivityAturAlamatPengirimanBinding binding;
    SessionManager sessionManager;
    String sId;
    AdapterAlamatUser adapterAlamatUser;
    List<ModelAlamatUser> listAlamatUser = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Alamat Pengiriman");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityAturAlamatPengirimanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try{
            //membaca session aplikasi
            sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            sId = user.get(SessionManager.USER_ID);

            //request data list alamat
            loadListAlamat(sId, "Aktif");

            //swipe refresh action
            binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadListAlamat(sId, "Aktif");
                    binding.swipeRefresh.setRefreshing(false);
                }
            });

            //set data list alamat to recyclerview
            adapterAlamatUser = new AdapterAlamatUser(AturAlamatPengiriman.this, listAlamatUser);
            RecyclerView.LayoutManager alamatUserLayout = new GridLayoutManager(getApplicationContext(), 1);
            binding.recyclerAlamat.setLayoutManager(alamatUserLayout);
            binding.recyclerAlamat.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerAlamat.setAdapter(adapterAlamatUser);

            binding.fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goToAddAlamat = new Intent(AturAlamatPengiriman.this, Add_Alamat.class);
                    startActivity(goToAddAlamat);
                }
            });
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent goToHome = new Intent(AturAlamatPengiriman.this, MainActivity.class);
        goToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToHome);
        return true;
    }

    private void loadListAlamat(String idUser, String statusAlamat) {
        binding.pesanProgress.setVisibility(View.VISIBLE);
        binding.recyclerAlamat.setVisibility(View.GONE);
        binding.pesanMaaf.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listAlamatUser(idUser,statusAlamat);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    binding.pesanProgress.setVisibility(View.GONE);
                    listAlamatUser = response.body().getList_alamat();
                    if(listAlamatUser.size()<1){
                        binding.pesanMaaf.setVisibility(View.VISIBLE);
                        binding.recyclerAlamat.setVisibility(View.GONE);
                    }else{
                        binding.pesanMaaf.setVisibility(View.GONE);
                        binding.recyclerAlamat.setVisibility(View.VISIBLE);
                        adapterAlamatUser = new AdapterAlamatUser(AturAlamatPengiriman.this, listAlamatUser);
                        binding.recyclerAlamat.setAdapter(adapterAlamatUser);
                    }
                }else{
                    binding.pesanProgress.setVisibility(View.GONE);
                    binding.recyclerAlamat.setVisibility(View.GONE);
                    binding.pesanMaaf.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                binding.pesanProgress.setVisibility(View.GONE);
                binding.recyclerAlamat.setVisibility(View.GONE);
                binding.pesanMaaf.setVisibility(View.GONE);
                Toast.makeText(AturAlamatPengiriman.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.urunggundrup.gotani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.urunggundrup.gotani.adapter.AdapterNotifikasi;
import com.urunggundrup.gotani.adapter.AdapterProdukUserHorizontal;
import com.urunggundrup.gotani.adapter.AdapterProdukUserVertical;
import com.urunggundrup.gotani.databinding.ActivityAturProdukPenjualanBinding;
import com.urunggundrup.gotani.databinding.ActivityLihatSemuaProdukBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelProdukUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LihatSemuaProduk extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ActivityLihatSemuaProdukBinding binding;
    AdapterProdukUserVertical adapterProdukUserVertical;
    List<ModelProdukUser> listProduk = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent getDataIntent = getIntent();
        getSupportActionBar().setTitle(getDataIntent.getStringExtra("nama_kategori"));

        binding = ActivityLihatSemuaProdukBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //get list produk buah
        loadProdukUser(getDataIntent.getStringExtra("id_kategori"),"");
        adapterProdukUserVertical = new AdapterProdukUserVertical(LihatSemuaProduk.this, listProduk);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        binding.recyclerProdukUser.setLayoutManager(layoutManager);
        binding.recyclerProdukUser.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerProdukUser.setAdapter(adapterProdukUserVertical);
        //
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }


    private void loadProdukUser(String idKategori, String sortir) {
        binding.pesanProgress.setVisibility(View.VISIBLE);
        binding.recyclerProdukUser.setVisibility(View.GONE);
        binding.pesanMaaf.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listProdukUser(idKategori,sortir);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    binding.pesanProgress.setVisibility(View.GONE);
                    listProduk = response.body().getList_produk_user();
                    if(listProduk.size()<1){
                        binding.pesanMaaf.setVisibility(View.VISIBLE);
                        binding.recyclerProdukUser.setVisibility(View.GONE);
                    }else{
                        binding.pesanMaaf.setVisibility(View.GONE);
                        binding.recyclerProdukUser.setVisibility(View.VISIBLE);
                        adapterProdukUserVertical = new AdapterProdukUserVertical(LihatSemuaProduk.this, listProduk);
                        binding.recyclerProdukUser.setAdapter(adapterProdukUserVertical);
                    }
                }else{
                    binding.pesanProgress.setVisibility(View.GONE);
                    binding.recyclerProdukUser.setVisibility(View.GONE);
                    binding.pesanMaaf.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                binding.pesanProgress.setVisibility(View.GONE);
                binding.recyclerProdukUser.setVisibility(View.GONE);
                binding.pesanMaaf.setVisibility(View.GONE);
                Toast.makeText(LihatSemuaProduk.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) searchViewItem.getActionView();
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                adapterProdukUserVertical.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterProdukUserVertical.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
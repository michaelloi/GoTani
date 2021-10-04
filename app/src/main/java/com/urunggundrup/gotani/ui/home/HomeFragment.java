package com.urunggundrup.gotani.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.urunggundrup.gotani.Keranjang;
import com.urunggundrup.gotani.LihatSemuaProduk;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.SessionManager;
import com.urunggundrup.gotani.adapter.AdapterNotifikasi;
import com.urunggundrup.gotani.adapter.AdapterProdukUserHorizontal;
import com.urunggundrup.gotani.databinding.FragmentHomeBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelProdukUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    AdapterProdukUserHorizontal adapterProdukUserHorizontalBuah;
    AdapterProdukUserHorizontal adapterProdukUserHorizontalSayur;
    AdapterProdukUserHorizontal adapterProdukUserHorizontalBumbu;
    List<ModelProdukUser> listBuah = new ArrayList<>();
    List<ModelProdukUser> listSayur = new ArrayList<>();
    List<ModelProdukUser> listBumbu = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.keranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToKeranjang = new Intent(getActivity(), Keranjang.class);
                startActivity(goToKeranjang);
            }
        });

        //get list produk buah
        loadProdukUser("1","");

        //set recycler view buah
        adapterProdukUserHorizontalBuah = new AdapterProdukUserHorizontal(getActivity(), listBuah);
        RecyclerView.LayoutManager layoutManagerBuah = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerBuah.setLayoutManager(layoutManagerBuah);
        binding.recyclerBuah.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerBuah.setAdapter(adapterProdukUserHorizontalBuah);

        //go to lihat semua buah
        binding.lihatSemuaBuah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLihatSemuaBuah = new Intent(getActivity(), LihatSemuaProduk.class);
                goToLihatSemuaBuah.putExtra("id_kategori", "1");
                goToLihatSemuaBuah.putExtra("nama_kategori", "Buah Buahan");
                startActivity(goToLihatSemuaBuah);
            }
        });

        //get list produk sayur
        loadProdukUser("2","");

        //set recycler view sayur
        adapterProdukUserHorizontalSayur = new AdapterProdukUserHorizontal(getActivity(), listSayur);
        RecyclerView.LayoutManager layoutManagerSayur = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerSayur.setLayoutManager(layoutManagerSayur);
        binding.recyclerSayur.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerSayur.setAdapter(adapterProdukUserHorizontalSayur);

        //go to lihat semua sayur
        binding.lihatSemuaSayur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLihatSemuaSayur = new Intent(getActivity(), LihatSemuaProduk.class);
                goToLihatSemuaSayur.putExtra("id_kategori", "2");
                goToLihatSemuaSayur.putExtra("nama_kategori", "Sayur Sayuran");
                startActivity(goToLihatSemuaSayur);
            }
        });


        //get list produk bumbu
        loadProdukUser("3","");

        //set recycler view bumbu
        adapterProdukUserHorizontalBumbu = new AdapterProdukUserHorizontal(getActivity(), listBumbu);
        RecyclerView.LayoutManager layoutManagerBumbu = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerBumbu.setLayoutManager(layoutManagerBumbu);
        binding.recyclerBumbu.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerBumbu.setAdapter(adapterProdukUserHorizontalBumbu);

        //go to lihat semua bumbu
        binding.lihatSemuaBumbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLihatSemuaBumbu = new Intent(getActivity(), LihatSemuaProduk.class);
                goToLihatSemuaBumbu.putExtra("id_kategori", "3");
                goToLihatSemuaBumbu.putExtra("nama_kategori", "Bumbu Dapur");
                startActivity(goToLihatSemuaBumbu);
            }
        });

        binding.semuaProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToLihatSemuaProduk = new Intent(getActivity(), LihatSemuaProduk.class);
                goToLihatSemuaProduk.putExtra("id_kategori", "0");
                goToLihatSemuaProduk.putExtra("nama_kategori", "Semua Produk");
                startActivity(goToLihatSemuaProduk);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadProdukUser(String idKategori, String sortir) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listProdukUserLimit(idKategori, sortir);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){

                    if(idKategori.equalsIgnoreCase("1")){
                        listBuah = response.body().getList_produk_user();
                        adapterProdukUserHorizontalBuah = new AdapterProdukUserHorizontal(getActivity(), listBuah);
                        binding.recyclerBuah.setAdapter(adapterProdukUserHorizontalBuah);
                    }else if(idKategori.equalsIgnoreCase("2")){
                        listSayur = response.body().getList_produk_user();
                        adapterProdukUserHorizontalSayur = new AdapterProdukUserHorizontal(getActivity(), listSayur);
                        binding.recyclerSayur.setAdapter(adapterProdukUserHorizontalSayur);
                    }else{
                        listBumbu = response.body().getList_produk_user();
                        adapterProdukUserHorizontalBumbu = new AdapterProdukUserHorizontal(getActivity(), listBumbu);
                        binding.recyclerBumbu.setAdapter(adapterProdukUserHorizontalBumbu);
                    }
                }else{
                    Toast.makeText(getActivity(), "Maaf terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getActivity(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.urunggundrup.gotani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.urunggundrup.gotani.adapter.AdapterKeranjang;
import com.urunggundrup.gotani.databinding.ActivityKeranjangBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelKeranjang;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Keranjang extends AppCompatActivity implements KeranjangListListener{

    private ActivityKeranjangBinding binding;
    SessionManager sessionManager;
    String sId="", idToko;
    AdapterKeranjang adapterKeranjang;
    List<ModelKeranjang> listKeranjang = new ArrayList<>();
    List<String> listIdKeranjang;
    Integer jumlahToko, hargaItemPesanan, hargaTotalItemPesanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Keranjang");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityKeranjangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try{
//membaca session aplikasi
            sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            sId = user.get(SessionManager.USER_ID);

            //request data list alamat
            loadListKeranjang(sId);

            //swipe refresh action
            binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadListKeranjang(sId);
                    binding.swipeRefresh.setRefreshing(false);
                }
            });

            //set data list alamat to recyclerview
            adapterKeranjang = new AdapterKeranjang(Keranjang.this, listKeranjang, Keranjang.this);
            RecyclerView.LayoutManager keranjangLayout = new GridLayoutManager(getApplicationContext(), 1);
            binding.recyclerKeranjang.setLayoutManager(keranjangLayout);
            binding.recyclerKeranjang.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerKeranjang.setAdapter(adapterKeranjang);

            //go to AlamatCheckout
            binding.selanjutnya.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(jumlahToko<2){
                        Intent goToAlamatCheckout = new Intent(Keranjang.this, AlamatCheckout.class);
                        goToAlamatCheckout.putExtra("listIdKeranjang", listIdKeranjang.toString());
                        goToAlamatCheckout.putExtra("jumlahToko", String.valueOf(jumlahToko));
                        goToAlamatCheckout.putExtra("idToko",idToko);
                        goToAlamatCheckout.putExtra("hargaPesanan", String.valueOf(hargaTotalItemPesanan));
                        startActivity(goToAlamatCheckout);
                    }else{
                        Toast.makeText(Keranjang.this, "Kamu hanya dapat memproses produk yang berasal dari 1 toko", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent goToHome = new Intent(Keranjang.this, MainActivity.class);
        goToHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToHome);
        return true;
    }

    private void loadListKeranjang(String id_user) {
        binding.pesanProgress.setVisibility(View.VISIBLE);
        binding.recyclerKeranjang.setVisibility(View.GONE);
        binding.pesanMaaf.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listKeranjang(id_user);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    binding.pesanProgress.setVisibility(View.GONE);
                    listKeranjang = response.body().getList_keranjang();
                    List<ModelKeranjang> listKeranjangCheckout = response.body().getList_keranjang();
                    if(listKeranjang.size()<1){
                        binding.pesanMaaf.setVisibility(View.VISIBLE);
                        binding.recyclerKeranjang.setVisibility(View.GONE);
                        binding.bottom.setVisibility(View.GONE);
                    }else{
                        binding.pesanMaaf.setVisibility(View.GONE);
                        binding.recyclerKeranjang.setVisibility(View.VISIBLE);
                        adapterKeranjang = new AdapterKeranjang(Keranjang.this, listKeranjang, Keranjang.this);
                        binding.recyclerKeranjang.setAdapter(adapterKeranjang);
                        getListChange(listKeranjang);
                        getJumlahToko(listKeranjang);
                        binding.bottom.setVisibility(View.VISIBLE);
                    }
                }else{
                    binding.bottom.setVisibility(View.GONE);
                    binding.pesanProgress.setVisibility(View.GONE);
                    binding.recyclerKeranjang.setVisibility(View.GONE);
                    binding.pesanMaaf.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                binding.pesanProgress.setVisibility(View.GONE);
                binding.recyclerKeranjang.setVisibility(View.GONE);
                binding.pesanMaaf.setVisibility(View.GONE);
                binding.bottom.setVisibility(View.GONE);
                Toast.makeText(Keranjang.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getListChange(List<ModelKeranjang> listKeranjangCheckout) {
        hargaItemPesanan=0;
        hargaTotalItemPesanan=0;
        listIdKeranjang=new ArrayList<>();

        for(int i=0; i<listKeranjangCheckout.size();i++){
            hargaItemPesanan = Integer.valueOf(listKeranjangCheckout.get(i).getHarga_produk()) * Integer.valueOf(listKeranjangCheckout.get(i).getJumlah_pesanan());
            hargaTotalItemPesanan += hargaItemPesanan;
            listIdKeranjang.add(listKeranjangCheckout.get(i).getId_keranjang());
        }

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        binding.totalHarga.setText(kursIndonesia.format(hargaTotalItemPesanan));

        if(listKeranjangCheckout.size()==0){
            binding.bottom.setVisibility(View.GONE);
        }else{
            binding.bottom.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getJumlahToko(List<ModelKeranjang> listKeranjangCheckout) {
        List<String> listIdToko = new ArrayList<>();
        jumlahToko=1;

        for(int i=0; i<listKeranjangCheckout.size();i++){
            if(i>0){
                if(!listIdToko.contains(listKeranjangCheckout.get(i).getId_toko())){
                    jumlahToko++;
                }
            }
            listIdToko.add(listKeranjangCheckout.get(i).getId_toko());
            idToko=listKeranjangCheckout.get(i).getId_toko();
        }
    }
}
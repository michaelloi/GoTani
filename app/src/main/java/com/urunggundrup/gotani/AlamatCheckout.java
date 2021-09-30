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

import com.urunggundrup.gotani.adapter.AdapterAlamatCheckout;
import com.urunggundrup.gotani.adapter.AdapterAlamatUser;
import com.urunggundrup.gotani.databinding.ActivityAlamatCheckoutBinding;
import com.urunggundrup.gotani.databinding.ActivityAturAlamatPengirimanBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelAlamatUser;

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

public class AlamatCheckout extends AppCompatActivity implements AlamatListListener {

    private ActivityAlamatCheckoutBinding binding;
    SessionManager sessionManager;
    String sId;
    AdapterAlamatCheckout adapterAlamatCheckout;
    List<ModelAlamatUser> listAlamatUser = new ArrayList<>();
    String idAlamatChecked;
    Intent getDataKeranjang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataKeranjang = getIntent();
        getSupportActionBar().setTitle("Pilih Alamat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityAlamatCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        adapterAlamatCheckout = new AdapterAlamatCheckout(AlamatCheckout.this, listAlamatUser, AlamatCheckout.this);
        RecyclerView.LayoutManager alamatUserLayout = new GridLayoutManager(getApplicationContext(), 1);
        binding.recyclerAlamat.setLayoutManager(alamatUserLayout);
        binding.recyclerAlamat.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerAlamat.setAdapter(adapterAlamatCheckout);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent goToHome = new Intent(AlamatCheckout.this, Keranjang.class);
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
                        adapterAlamatCheckout = new AdapterAlamatCheckout(AlamatCheckout.this, listAlamatUser, AlamatCheckout.this);
                        binding.recyclerAlamat.setAdapter(adapterAlamatCheckout);
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
                Toast.makeText(AlamatCheckout.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getIdAlamat(Integer position) {
        idAlamatChecked="";
        idAlamatChecked = listAlamatUser.get(position).getId_alamat();

        if(idAlamatChecked==null){
            binding.bottom.setVisibility(View.GONE);
        }else{
            binding.bottom.setVisibility(View.VISIBLE);
        }

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        Integer hargaOngkir = Integer.valueOf(getDataKeranjang.getStringExtra("jumlahToko")) * 10000;
        Integer hargaPesananTotal = Integer.valueOf(getDataKeranjang.getStringExtra("hargaPesanan")) + hargaOngkir;
        String sHargaPesanan = kursIndonesia.format(hargaPesananTotal);

        binding.ongkir.setText("(Rp 10.000 x "+getDataKeranjang.getStringExtra("jumlahToko")+")");
        binding.totalHarga.setText(sHargaPesanan);
    }
}
package com.urunggundrup.gotani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.adapter.AdapterKeranjangCheckout;
import com.urunggundrup.gotani.databinding.ActivityDetailPesananTokoBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelKeranjang;
import com.urunggundrup.gotani.model.ModelRekening;

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

public class DetailPesananToko extends AppCompatActivity {

    private ActivityDetailPesananTokoBinding binding;
    Intent getData;
    SessionManager sessionManager;
    String sId;
    private ProgressDialog progress;
    String listIdKeranjang, hargaPesanan, hargaOngkir, hargaPesananTotal, judulAlamatChecked, namaPenerimaChecked, nohpPenerimaChecked, alamatPenerimaChecked;
    Integer jumlahToko;
    List<ModelKeranjang> listKeranjangCheckout = new ArrayList<>();
    List<ModelRekening> listRekening = new ArrayList<>();
    AdapterKeranjangCheckout adapterKeranjangCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData = getIntent();
        getSupportActionBar().setTitle("Detail Pesanan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityDetailPesananTokoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //membaca session aplikasi
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        sId = user.get(SessionManager.USER_ID);

        //get data intent
        listIdKeranjang = getData.getStringExtra("listIdKeranjang");
        hargaPesanan = getData.getStringExtra("hargaPesanan");
        hargaOngkir = getData.getStringExtra("hargaOngkir");
        hargaPesananTotal = getData.getStringExtra("hargaPesananTotal");
        judulAlamatChecked = getData.getStringExtra("judulAlamatChecked");
        namaPenerimaChecked = getData.getStringExtra("namaPenerimaChecked");
        nohpPenerimaChecked = getData.getStringExtra("nohpPenerimaChecked");
        alamatPenerimaChecked = getData.getStringExtra("alamatPenerimaChecked");

        jumlahToko = Integer.valueOf(hargaOngkir)/10000;

        //get data list keranjang checkout
        loadKeranjangCheckout(listIdKeranjang.replace("[","").replace("]", ""));

        //set data to recycler view
        adapterKeranjangCheckout = new AdapterKeranjangCheckout(DetailPesananToko.this, listKeranjangCheckout);
        RecyclerView.LayoutManager keranjangCheckoutLayout = new GridLayoutManager(getApplicationContext(), 1);
        binding.recyclerPembelian.setLayoutManager(keranjangCheckoutLayout);
        binding.recyclerPembelian.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerPembelian.setAdapter(adapterKeranjangCheckout);

        //set total yang harus dibayar
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        String sHargaPesanan = kursIndonesia.format(Integer.valueOf(hargaPesananTotal));
        binding.ongkir.setText("(Rp 10.000 x "+String.valueOf(jumlahToko)+")");
        binding.totalHarga.setText(sHargaPesanan);

        //set text alamat pengiriman
        binding.judulAlamat.setText(judulAlamatChecked);
        binding.namaPenerima.setText(namaPenerimaChecked);
        binding.nohpPenerima.setText(nohpPenerimaChecked);
        binding.alamatPenerima.setText(alamatPenerimaChecked);

        //set text metode pembayaran
        binding.namaRekening.setText("");
        binding.noRekening.setText("");

        //get data rekening
        loadRekening();

        if(getData.getStringExtra("statusPesanan").equalsIgnoreCase("Menunggu Pembayaran")){
            binding.textFotoBukti.setVisibility(View.GONE);
            binding.cardViewFoto.setVisibility(View.GONE);
        }else{
            binding.textBiaya.setVisibility(View.VISIBLE);
            binding.cardviewBiaya.setVisibility(View.VISIBLE);
            Picasso.with(binding.fotoBukti.getContext()).load(getResources().getString(R.string.urlaccesfotopembyaran)+getData.getStringExtra("fotoBuktiPembayaran")).into(binding.fotoBukti);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void loadKeranjangCheckout(String idKeranjang) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listKeranjangCheckout(idKeranjang);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    listKeranjangCheckout = response.body().getList_keranjang();
                    adapterKeranjangCheckout = new AdapterKeranjangCheckout(DetailPesananToko.this, listKeranjangCheckout);
                    binding.recyclerPembelian.setAdapter(adapterKeranjangCheckout);
                }else{
                    Toast.makeText(DetailPesananToko.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(DetailPesananToko.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRekening(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listRekening("");
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                listRekening = response.body().getList_rekening();
                if(response.body().getValue().equalsIgnoreCase("1")){
                    binding.namaRekening.setText(listRekening.get(0).getNama_bank_rekening_pembayaran() + " A.N. " + listRekening.get(0).getAtas_nama_rekening_pembayaran());
                    binding.noRekening.setText(listRekening.get(0).getNo_rekening_pembayaran());
                }else{
                    Toast.makeText(DetailPesananToko.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(DetailPesananToko.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
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

import com.urunggundrup.gotani.adapter.AdapterKeranjangCheckout;
import com.urunggundrup.gotani.databinding.ActivityCheckOutBinding;
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
import retrofit2.http.Field;

public class CheckOut extends AppCompatActivity {

    private ActivityCheckOutBinding binding;
    SessionManager sessionManager;
    String sId;
    Intent getDataKeranjang;
    private ProgressDialog progress;
    String listIdKeranjang, jumlahToko, hargaPesanan, hargaOngkir, hargaPesananTotal, idAlamatChecked, judulAlamatChecked, namaPenerimaChecked, nohpPenerimaChecked, alamatPenerimaChecked;
    List<ModelKeranjang> listKeranjangCheckout = new ArrayList<>();
    List<ModelRekening> listRekening = new ArrayList<>();
    AdapterKeranjangCheckout adapterKeranjangCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataKeranjang = getIntent();
        getSupportActionBar().setTitle("Pesanan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //membaca session aplikasi
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        sId = user.get(SessionManager.USER_ID);

        //get data intent
        listIdKeranjang = getDataKeranjang.getStringExtra("listIdKeranjang");
        jumlahToko = getDataKeranjang.getStringExtra("jumlahToko");
        hargaPesanan = getDataKeranjang.getStringExtra("hargaPesanan");
        hargaOngkir = getDataKeranjang.getStringExtra("hargaOngkir");
        hargaPesananTotal = getDataKeranjang.getStringExtra("hargaPesananTotal");
        idAlamatChecked = getDataKeranjang.getStringExtra("idAlamat");
        judulAlamatChecked = getDataKeranjang.getStringExtra("judulAlamatChecked");
        namaPenerimaChecked = getDataKeranjang.getStringExtra("namaPenerimaChecked");
        nohpPenerimaChecked = getDataKeranjang.getStringExtra("nohpPenerimaChecked");
        alamatPenerimaChecked = getDataKeranjang.getStringExtra("alamatPenerimaChecked");

        //get data list keranjang checkout
        loadKeranjangCheckout(listIdKeranjang.replace("[","").replace("]", ""));

        //set data to recycler view
        adapterKeranjangCheckout = new AdapterKeranjangCheckout(CheckOut.this, listKeranjangCheckout);
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
        binding.ongkir.setText("(Rp 10.000 x "+jumlahToko+")");
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

        binding.buttonPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPesanan(sId,"1",idAlamatChecked,listRekening.get(0).getId_rekening_pembayaran(), listIdKeranjang.replace("[","").replace("]", ""), hargaPesanan, hargaOngkir, hargaPesananTotal);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent goToAlamatCheckout = new Intent(CheckOut.this, AlamatCheckout.class);
        goToAlamatCheckout.putExtra("listIdKeranjang", listIdKeranjang);
        goToAlamatCheckout.putExtra("jumlahToko", jumlahToko);
        goToAlamatCheckout.putExtra("hargaPesanan", hargaPesananTotal);
        goToAlamatCheckout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goToAlamatCheckout);
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
                    adapterKeranjangCheckout = new AdapterKeranjangCheckout(CheckOut.this, listKeranjangCheckout);
                    binding.recyclerPembelian.setAdapter(adapterKeranjangCheckout);
                }else{
                    Toast.makeText(CheckOut.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(CheckOut.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CheckOut.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(CheckOut.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPesanan(String id_user, String id_status_pesanan, String id_alamat, String id_rekening_pembayaran, String list_id_keranjang, String harga_pesanan, String harga_ongkir, String total_pembayaran){
        progress = new ProgressDialog(CheckOut.this);
        progress.setMessage("Tunggu Sebentar");
        progress.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.addPesanan(id_user, id_status_pesanan, id_alamat, id_rekening_pembayaran, list_id_keranjang, harga_pesanan, harga_ongkir, total_pembayaran);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if(response.body().getValue().equalsIgnoreCase("1")){
                    Toast.makeText(CheckOut.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent goToMainActivity = new Intent(CheckOut.this, MainActivity.class);
                    goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goToMainActivity);
                    progress.dismiss();
                }else{
                    Toast.makeText(CheckOut.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(CheckOut.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
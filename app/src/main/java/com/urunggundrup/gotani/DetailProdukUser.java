package com.urunggundrup.gotani;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.databinding.ActivityAturProdukPenjualanBinding;
import com.urunggundrup.gotani.databinding.ActivityDetailProdukUserBinding;
import com.urunggundrup.gotani.model.Model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailProdukUser extends AppCompatActivity {

    private ActivityDetailProdukUserBinding binding;
    SessionManager sessionManager;
    String sId;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //untuk mendapatkan data yang di oper dari recyclerview
        Intent getDataIntent = getIntent();

        getSupportActionBar().setTitle(getDataIntent.getStringExtra("nama_produk"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityDetailProdukUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get session
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        sId = user.get(SessionManager.USER_ID);

        Picasso.with(binding.fotoProduk.getContext()).load(getResources().getString(R.string.urlaccesdocuments)+getDataIntent.getStringExtra("foto_produk")).into(binding.fotoProduk);
        binding.namaProduk.setText(getDataIntent.getStringExtra("nama_produk"));

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        binding.hargaProduk.setText(kursIndonesia.format(Double.valueOf(getDataIntent.getStringExtra("harga_produk")))+" / "+getDataIntent.getStringExtra("nama_satuan"));
        binding.namaToko.setText(getDataIntent.getStringExtra("nama_toko"));
        binding.namaLokasi.setText(getDataIntent.getStringExtra("nama_lokasi"));
        binding.keteranganSatuan.setText("Masukkan jumlah pesanan (satuan "+getDataIntent.getStringExtra("keterangan_satuan")+ ")");
        binding.masukkanKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sessionManager.isLoggedIn()){
                    Toast.makeText(DetailProdukUser.this, "Maaf tidak dapat di proses. Kamu harus login terlebih dahulu", Toast.LENGTH_SHORT).show();
                }else{
                    if(binding.jumlahPesanan.getText().toString().isEmpty()){
                        binding.jumlahPesanan.setError("Kamu harus masukkan jumlah pesanan");
                    }else if(Integer.valueOf(binding.jumlahPesanan.getText().toString())<1){
                        binding.jumlahPesanan.setError("Kamu harus memesan lebih dari 0");
                    }else{
                        masukkanKeranjang(getDataIntent.getStringExtra("id_produk"), sId, binding.jumlahPesanan.getText().toString());
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void masukkanKeranjang(String id_produk, String id_user, String jumlah_pesanan) {
        progress = new ProgressDialog(this);
        progress.setMessage("Tunggu Sebentar");
        progress.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.addKeranjang(id_produk, id_user, jumlah_pesanan);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    Intent goHome = new Intent(DetailProdukUser.this, Keranjang.class);
                    goHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goHome);
                }
                progress.dismiss();
                Toast.makeText(DetailProdukUser.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(DetailProdukUser.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
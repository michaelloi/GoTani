package com.urunggundrup.gotani;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.urunggundrup.gotani.databinding.ActivityAddAlamatBinding;
import com.urunggundrup.gotani.model.Model;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Add_Alamat extends AppCompatActivity {

    private ActivityAddAlamatBinding binding;
    private String sId;
    SessionManager sessionManager;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Tambah Alamat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityAddAlamatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //membaca session aplikasi
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        sId = user.get(SessionManager.USER_ID);

        //button simpan alamat action
        binding.simpanAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.addJudulAlamat.getText().toString().length()==0){
                    binding.addJudulAlamat.setError("Field ini harus di isi");
                }else if(binding.addNamaPenerima.getText().toString().length()==0){
                    binding.addNamaPenerima.setError("Field ini harus di isi");
                }else if(binding.addNohpPenerima.getText().toString().length()==0){
                    binding.addNohpPenerima.setError("Field ini harus di isi");
                }else if(binding.addAlamatLengkap.getText().toString().length()==0){
                    binding.addAlamatLengkap.setError("Field ini harus di isi");
                }else{
                    simpanAlamatUser(binding.addJudulAlamat.getText().toString(), binding.addNamaPenerima.getText().toString(), binding.addNohpPenerima.getText().toString(), binding.addAlamatLengkap.getText().toString(), sId, "Aktif");
                }
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void simpanAlamatUser(String judulAlamat, String namaPenerima, String nohpPenerima, String alamatLengkap, String sId, String statusAlamat) {
        progress = new ProgressDialog(this);
        progress.setMessage("Tunggu Sebentar");
        progress.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.addAlamatUser(sId, judulAlamat, alamatLengkap, namaPenerima,nohpPenerima, statusAlamat);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    Intent goToAturAlamat = new Intent(Add_Alamat.this, AturAlamatPengiriman.class);
                    goToAturAlamat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goToAturAlamat);
                }
                progress.dismiss();
                Toast.makeText(Add_Alamat.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(Add_Alamat.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
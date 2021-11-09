package com.urunggundrup.gotani.ui.dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.urunggundrup.gotani.ActivityPesananToko;
import com.urunggundrup.gotani.Add_Produk;
import com.urunggundrup.gotani.AturAlamatPengiriman;
import com.urunggundrup.gotani.AturProdukPenjualan;
import com.urunggundrup.gotani.MainActivity;
import com.urunggundrup.gotani.adapter.AdapterSpinnerLokasi;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.SessionManager;
import com.urunggundrup.gotani.databinding.FragmentDashboardBinding;
import com.urunggundrup.gotani.model.ModelLokasi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardFragment extends Fragment {

    SessionManager sessionManager;
    private FragmentDashboardBinding binding;
    private String slokasi="";
    private String sStatus="";
    private ProgressDialog progress;
    String sId, sNama, sStatusLogin, sNamaToko;
    AdapterSpinnerLokasi adapterSpinnerLokasi;
    List<ModelLokasi> listLokasi = new ArrayList<>();
    List<String> listNamaLokasi = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        //Session Login Petani Element
        LinearLayout linearSessionLoginPetani = binding.dashboardSessionLoginPetani;
        TextView namaToko = binding.namaToko;
        TextView namaPetani = binding.namaPetani;
        LinearLayout btnLogoutPetani = binding.dashboardLogoutButtonPetani;


        //Check session login
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        sId = user.get(SessionManager.USER_ID);
        sNama = user.get(SessionManager.USER_NAMA);
        sStatusLogin = user.get(SessionManager.USER_STATUS);
        sNamaToko = user.get(SessionManager.USER_NAMA_TOKO);

        if(sessionManager.isLoggedIn()&&sStatusLogin!=null&&sStatusLogin.equalsIgnoreCase("Pembeli")){
            binding.dashboardSessionLogin.setVisibility(View.VISIBLE);
            linearSessionLoginPetani.setVisibility(View.GONE);
            binding.linearLoginDashboard.setVisibility(View.GONE);
            binding.linearRegisterDashboard.setVisibility(View.GONE);
            binding.linearNamaTokoDashboard.setVisibility(View.GONE);

            //Set Session Login Pembeli Element
            binding.namaSessionLoginPembeli.setText("Hai "+sNama);
            binding.dashboardLogoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sessionManager.logoutUser();
                }
            });

            binding.alamatPengirimanSessionLoginPembeli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent goToAturAlamatPengiriman = new Intent(getActivity(), AturAlamatPengiriman.class);
                    startActivity(goToAturAlamatPengiriman);
                }
            });

        }else if(sessionManager.isLoggedIn()&&sStatusLogin!=null&&sStatusLogin.equalsIgnoreCase("Petani")){
            linearSessionLoginPetani.setVisibility(View.VISIBLE);
            binding.dashboardSessionLogin.setVisibility(View.GONE);
            binding.linearLoginDashboard.setVisibility(View.GONE);
            binding.linearRegisterDashboard.setVisibility(View.GONE);
            binding.linearNamaTokoDashboard.setVisibility(View.GONE);

            //Set Session Login Petani Element
            namaToko.setText(sNamaToko);
            namaPetani.setText("Pengelola : "+sNama);
            btnLogoutPetani.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sessionManager.logoutUser();
                }
            });

        }else{
            binding.linearLoginDashboard.setVisibility(View.VISIBLE);
            binding.linearRegisterDashboard.setVisibility(View.GONE);
            binding.linearNamaTokoDashboard.setVisibility(View.GONE);
            linearSessionLoginPetani.setVisibility(View.GONE);
            binding.dashboardSessionLogin.setVisibility(View.GONE);
        }

        //Register to Login
        binding.dashboardLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.linearLoginDashboard.setVisibility(View.VISIBLE);
                binding.linearRegisterDashboard.setVisibility(View.GONE);
            }
        });

        //Login to Register
        binding.dashboardRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.linearRegisterDashboard.setVisibility(View.VISIBLE);
                binding.linearLoginDashboard.setVisibility(View.GONE);
            }
        });

        //Spinner Register Lokasi
        loadListLokasi("");
        binding.dashboardLokasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(binding.dashboardLokasi.getSelectedItemPosition()!=0){
                    slokasi = listLokasi.get(binding.dashboardLokasi.getSelectedItemPosition()-1).getId_lokasi();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                slokasi = "";
            }
        });

        //Register sebagai Pembeli
        binding.dashboardPembeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sStatus="Pembeli";

                if(binding.dashboardNama.getText().length()==0){
                    binding.dashboardNama.setError("Nama harus di isi");
                }else if(binding.dashboardNohp.getText().length()==0){
                    binding.dashboardNohp.setError("Nomor Handphone harus di isi");
                }else if(binding.dashboardUsername.getText().length()==0){
                    binding.dashboardUsername.setError("Nama Pengguna / Username harus di isi");
                }else if(binding.dashboardPassword.getText().length()==0){
                    binding.dashboardPassword.setError("Password harus di isi");
                }else if(slokasi.isEmpty()) {
                    Toast.makeText(getActivity(), "Pilih lokasi kamu", Toast.LENGTH_SHORT).show();
                }else{
                    registerPengguna(binding.dashboardNama.getText().toString(), binding.dashboardNohp.getText().toString(), binding.dashboardUsername.getText().toString(), binding.dashboardPassword.getText().toString(), slokasi, sStatus,"");
                }
            }
        });

        //Register sebaai Petani
        binding.dashboardPetani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sStatus="Petani";

                if(binding.dashboardNama.getText().length()==0){
                    binding.dashboardNama.setError("Nama harus di isi");
                }else if(binding.dashboardNohp.getText().length()==0){
                    binding.dashboardNohp.setError("Nomor Handphone harus di isi");
                }else if(binding.dashboardUsername.getText().length()==0){
                    binding.dashboardUsername.setError("Nama Pengguna / Username harus di isi");
                }else if(binding.dashboardPassword.getText().length()==0){
                    binding.dashboardPassword.setError("Password harus di isi");
                }else if(slokasi.isEmpty()) {
                    Toast.makeText(getActivity(), "Pilih lokasi kamu", Toast.LENGTH_SHORT).show();
                }else{
                    binding.linearNamaTokoDashboard.setVisibility(View.VISIBLE);
                    binding.linearLoginDashboard.setVisibility(View.GONE);
                    binding.linearRegisterDashboard.setVisibility(View.GONE);
                    linearSessionLoginPetani.setVisibility(View.GONE);
                    binding.dashboardSessionLogin.setVisibility(View.GONE);
                }
            }
        });

        //Simpan Nama Toko
        binding.dashboardSimpanNamaToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.dashboardNamaToko.getText().length()==0){
                    binding.dashboardNamaToko.setError("Nama Toko harus di isi");
                }else{
                    registerPengguna(binding.dashboardNama.getText().toString(), binding.dashboardNohp.getText().toString(), binding.dashboardUsername.getText().toString(), binding.dashboardPassword.getText().toString(), slokasi, sStatus,binding.dashboardNamaToko.getText().toString());
                }
            }
        });


        //Login proses
        binding.dashboardLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.dashboardUsernameLogin.getText().length()==0){
                    binding.dashboardUsernameLogin.setError("Username harus di isi");
                }else if(binding.dashboardPasswordLogin.getText().length()==0){
                    binding.dashboardPasswordLogin.setError("Password harus di isi");
                }else{
                    loginPengguna(binding.dashboardUsernameLogin.getText().toString(), binding.dashboardPasswordLogin.getText().toString());
                }
            }
        });

        //dashboard Petani
        binding.alamatPengirimanSessionLoginPetani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAturAlamatPengiriman = new Intent(getActivity(), AturAlamatPengiriman.class);
                startActivity(goToAturAlamatPengiriman);
            }
        });

        //pindah ke halaman atur produk penjualan
        binding.aturProdukPenjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAturProdukPenjualan = new Intent(getActivity(), AturProdukPenjualan.class);
                startActivity(goToAturProdukPenjualan);
            }
        });

        binding.pesananMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToActivityPesananToko = new Intent(getActivity(), ActivityPesananToko.class);
                goToActivityPesananToko.putExtra("judulActivity", "Pesanan Masuk");
                goToActivityPesananToko.putExtra("statusPesanan", "1,2");
                startActivity(goToActivityPesananToko);
            }
        });

        binding.pesananDalamPengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToActivityPesananToko = new Intent(getActivity(), ActivityPesananToko.class);
                goToActivityPesananToko.putExtra("judulActivity", "Pesanan Dalam Pengiriman");
                goToActivityPesananToko.putExtra("statusPesanan", "3");
                startActivity(goToActivityPesananToko);
            }
        });

        binding.pesananSampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToActivityPesananToko = new Intent(getActivity(), ActivityPesananToko.class);
                goToActivityPesananToko.putExtra("judulActivity", "Pesanan Sampai");
                goToActivityPesananToko.putExtra("statusPesanan", "4");
                startActivity(goToActivityPesananToko);
            }
        });

        binding.pesananSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToActivityPesananToko = new Intent(getActivity(), ActivityPesananToko.class);
                goToActivityPesananToko.putExtra("judulActivity", "Pesanan Selesai");
                goToActivityPesananToko.putExtra("statusPesanan", "5");
                startActivity(goToActivityPesananToko);
            }
        });

        binding.pesananBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToActivityPesananToko = new Intent(getActivity(), ActivityPesananToko.class);
                goToActivityPesananToko.putExtra("judulActivity", "Pesanan Batal");
                goToActivityPesananToko.putExtra("statusPesanan", "6");
                startActivity(goToActivityPesananToko);
            }
        });

        return root;
    }

    private void registerPengguna(String nama, String nohp, String username, String password, String lokasi, String status, String namatoko) {
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Tunggu Sebentar");
        progress.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getActivity().getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.registerUser(nama, nohp, username, password, lokasi, status, namatoko);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    loginPengguna(username, password);
                }
                progress.dismiss();
                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(getActivity(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginPengguna(String username, String password){
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Tunggu Sebentar");
        progress.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getActivity().getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.loginUser(username, password);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    sessionManager.createLoginSession(
                            response.body().getUser().getId_user(),
                            response.body().getUser().getUsername(),
                            response.body().getUser().getPassword(),
                            response.body().getUser().getNama_user(),
                            response.body().getUser().getNo_hp(),
                            response.body().getUser().getLokasi(),
                            response.body().getUser().getStatus(),
                            response.body().getUser().getId_toko(),
                            response.body().getUser().getNama_toko(),
                            response.body().getUser().getCreated_date()
                    );

                    progress.dismiss();
                    Intent a = new Intent(getActivity(), MainActivity.class);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(a);
                    Toast.makeText(getActivity(), "Selamat datang "+response.body().getUser().getNama_user(), Toast.LENGTH_SHORT).show();
                }else{
                    progress.dismiss();
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(getActivity(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadListLokasi(String iduser){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getActivity().getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listLokasi(iduser);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    listLokasi = response.body().getList_lokasi();

                    listNamaLokasi.add("Pilih Lokasi Domisili");

                    for(int i=0;i<listLokasi.size();i++){
                        listNamaLokasi.add(listLokasi.get(i).getNama_lokasi());
                    }

                    adapterSpinnerLokasi = new AdapterSpinnerLokasi(getActivity().getApplicationContext(), listNamaLokasi);
                    binding.dashboardLokasi.setAdapter(adapterSpinnerLokasi);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(getActivity(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
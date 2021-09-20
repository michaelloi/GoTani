package com.urunggundrup.gotani.ui.dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.urunggundrup.gotani.Add_Produk;
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
    Spinner spinnerLokasiRegister;
    List<ModelLokasi> listLokasi = new ArrayList<>();
    List<String> listNamaLokasi = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        //Login Page Element
        LinearLayout linearLogin = binding.linearLoginDashboard;
        EditText eUsernameLogin = binding.dashboardUsernameLogin;
        EditText ePasswordLogin = binding.dashboardPasswordLogin;
        LinearLayout btnLogin = binding.dashboardLoginButton;
        TextView textToRegister = binding.dashboardRegisterText;

        //Register Page Element
        LinearLayout linearRegister = binding.linearRegisterDashboard;
        EditText eNamaRegister = binding.dashboardNama;
        EditText eNohpRegister = binding.dashboardNohp;
        EditText eUsernameRegister = binding.dashboardUsername;
        EditText ePasswordRegister = binding.dashboardPassword;
        TextView textToLogin = binding.dashboardLoginText;
        spinnerLokasiRegister = binding.dashboardLokasi;
        LinearLayout btnPembeli = binding.dashboardPembeli;
        LinearLayout btnPetani = binding.dashboardPetani;

        //Simpan Nama Toko Page Element
        LinearLayout linearSimpanNamaToko = binding.linearNamaTokoDashboard;
        EditText eNamaToko = binding.dashboardNamaToko;
        LinearLayout btnSimpanNamaToko = binding.dashboardSimpanNamaToko;

        //Session Login Pembeli Element
        LinearLayout linearSessionLoginPembeli = binding.dashboardSessionLogin;
        TextView tNamaLoginPembeli = binding.namaSessionLoginPembeli;
        LinearLayout btnAturAlamat = binding.alamatPengirimanSessionLoginPembeli;
        LinearLayout btnLogutPembeli = binding.dashboardLogoutButton;

        //Session Login Petani Element
        LinearLayout linearSessionLoginPetani = binding.dashboardSessionLoginPetani;
        TextView namaToko = binding.namaToko;
        TextView namaPetani = binding.namaPetani;
        LinearLayout btnAturProdukPenjualan = binding.aturProdukPenjualan;
        LinearLayout btnPesananMasuk = binding.pesananMasuk;
        LinearLayout btnPesananDalamPengiriman = binding.pesananDalamPengiriman;
        LinearLayout btnPesananSampai = binding.pesananSampai;
        LinearLayout btnPesananSelesai = binding.pesananSelesai;
        LinearLayout btnPesananBatal = binding.pesananSelesai;
        LinearLayout btnLihatPendapatan = binding.lihatPendapatan;
        LinearLayout btnLogoutPetani = binding.dashboardLogoutButtonPetani;


        //Check session login
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        sId = user.get(SessionManager.USER_ID);
        sNama = user.get(SessionManager.USER_NAMA);
        sStatusLogin = user.get(SessionManager.USER_STATUS);
        sNamaToko = user.get(SessionManager.USER_NAMA_TOKO);

        if(sessionManager.isLoggedIn()&&sStatusLogin!=null&&sStatusLogin.equalsIgnoreCase("Pembeli")){
            linearSessionLoginPembeli.setVisibility(View.VISIBLE);
            linearSessionLoginPetani.setVisibility(View.GONE);
            linearLogin.setVisibility(View.GONE);
            linearRegister.setVisibility(View.GONE);
            linearSimpanNamaToko.setVisibility(View.GONE);

            //Set Session Login Pembeli Element
            tNamaLoginPembeli.setText("Hai "+sNama);
            btnLogutPembeli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sessionManager.logoutUser();
                }
            });
        }else if(sessionManager.isLoggedIn()&&sStatusLogin!=null&&sStatusLogin.equalsIgnoreCase("Petani")){
            linearSessionLoginPetani.setVisibility(View.VISIBLE);
            linearSessionLoginPembeli.setVisibility(View.GONE);
            linearLogin.setVisibility(View.GONE);
            linearRegister.setVisibility(View.GONE);
            linearSimpanNamaToko.setVisibility(View.GONE);

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
            linearLogin.setVisibility(View.VISIBLE);
            linearRegister.setVisibility(View.GONE);
            linearSimpanNamaToko.setVisibility(View.GONE);
            linearSessionLoginPetani.setVisibility(View.GONE);
            linearSessionLoginPembeli.setVisibility(View.GONE);
        }

        //Register to Login
        textToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLogin.setVisibility(View.VISIBLE);
                linearRegister.setVisibility(View.GONE);
            }
        });

        //Login to Register
        textToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearRegister.setVisibility(View.VISIBLE);
                linearLogin.setVisibility(View.GONE);
            }
        });

        //Spinner Register Lokasi
        loadListLokasi("");
        spinnerLokasiRegister.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerLokasiRegister.getSelectedItemPosition()!=0){
                    slokasi = listLokasi.get(spinnerLokasiRegister.getSelectedItemPosition()-1).getId_lokasi();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                slokasi = "";
            }
        });

        //Register sebagai Pembeli
        btnPembeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sStatus="Pembeli";

                if(eNamaRegister.getText().length()==0){
                    eNamaRegister.setError("Nama harus di isi");
                }else if(eNohpRegister.getText().length()==0){
                    eNohpRegister.setError("Nomor Handphone harus di isi");
                }else if(eUsernameRegister.getText().length()==0){
                    eUsernameRegister.setError("Nama Pengguna / Username harus di isi");
                }else if(ePasswordRegister.getText().length()==0){
                    ePasswordRegister.setError("Password harus di isi");
                }else if(slokasi.isEmpty()) {
                    Toast.makeText(getActivity(), "Pilih lokasi kamu", Toast.LENGTH_SHORT).show();
                }else{
                    registerPengguna(eNamaRegister.getText().toString(), eNohpRegister.getText().toString(), eUsernameRegister.getText().toString(), ePasswordRegister.getText().toString(), slokasi, sStatus,"");
                }
            }
        });

        //Register sebaai Petani
        btnPetani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sStatus="Petani";

                if(eNamaRegister.getText().length()==0){
                    eNamaRegister.setError("Nama harus di isi");
                }else if(eNohpRegister.getText().length()==0){
                    eNohpRegister.setError("Nomor Handphone harus di isi");
                }else if(eUsernameRegister.getText().length()==0){
                    eUsernameRegister.setError("Nama Pengguna / Username harus di isi");
                }else if(ePasswordRegister.getText().length()==0){
                    ePasswordRegister.setError("Password harus di isi");
                }else if(slokasi.isEmpty()) {
                    Toast.makeText(getActivity(), "Pilih lokasi kamu", Toast.LENGTH_SHORT).show();
                }else{
                    linearSimpanNamaToko.setVisibility(View.VISIBLE);
                    linearLogin.setVisibility(View.GONE);
                    linearRegister.setVisibility(View.GONE);
                    linearSessionLoginPetani.setVisibility(View.GONE);
                    linearSessionLoginPembeli.setVisibility(View.GONE);
                }
            }
        });

        //Simpan Nama Toko
        btnSimpanNamaToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eNamaToko.getText().length()==0){
                    eNamaToko.setError("Nama Toko harus di isi");
                }else{
                    registerPengguna(eNamaRegister.getText().toString(), eNohpRegister.getText().toString(), eUsernameRegister.getText().toString(), ePasswordRegister.getText().toString(), slokasi, sStatus,eNamaToko.getText().toString());
                }
            }
        });


        //Login proses
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eUsernameLogin.getText().length()==0){
                    eUsernameLogin.setError("Username harus di isi");
                }else if(ePasswordLogin.getText().length()==0){
                    ePasswordLogin.setError("Password harus di isi");
                }else{
                    loginPengguna(eUsernameLogin.getText().toString(), ePasswordLogin.getText().toString());
                }
            }
        });

        //dashboard Petani
        //pindah ke halaman atur produk penjualan
        btnAturProdukPenjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAturProdukPenjualan = new Intent(getActivity(), Add_Produk.class);
                startActivity(goToAturProdukPenjualan);
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
                }

                progress.dismiss();
                Intent a = new Intent(getActivity(), MainActivity.class);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(a);
                Toast.makeText(getActivity(), "Selamat datang "+response.body().getUser().getNama_user(), Toast.LENGTH_SHORT).show();
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
                    spinnerLokasiRegister.setAdapter(adapterSpinnerLokasi);
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
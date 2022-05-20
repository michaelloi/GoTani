package com.urunggundrup.gotani;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.adapter.AdapterSpinnerKategoriProduk;
import com.urunggundrup.gotani.adapter.AdapterSpinnerSatuanProduk;
import com.urunggundrup.gotani.databinding.ActivityAddProdukBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelKategori;
import com.urunggundrup.gotani.model.ModelProdukPetani;
import com.urunggundrup.gotani.model.ModelSatuan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Add_Produk extends AppCompatActivity {

    private ActivityAddProdukBinding binding;
    AdapterSpinnerKategoriProduk adapterSpinnerKategoriProduk;
    AdapterSpinnerSatuanProduk adapterSpinnerSatuanProduk;
    List<ModelKategori> listKategori = new ArrayList<>();
    List<ModelSatuan> listSatuan = new ArrayList<>();
    List<String> listNamaKategori = new ArrayList<>();
    List<String> listNamaSatuan = new ArrayList<>();
    String sKategoriProduk="0";
    String sSatuanProdukk;
    private ProgressDialog progress;
    int a;
    int idSatuan = 0, idKategori = 0;
    Intent getEditData;
    ModelProdukPetani modelProdukPetani = new ModelProdukPetani();
    SessionManager sessionManager;
    String sIdToko, sFotoProduk="", ubahFoto="";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Tambah Produk");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityAddProdukBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try{
            getEditData = getIntent();
            modelProdukPetani = setupEditDataItem(getEditData);

            if(modelProdukPetani.getId_produk()!=null){
                String urlFoto = getResources().getString(R.string.urlaccesdocuments);
                Picasso.with(binding.fotoProduk.getContext()).load(urlFoto+modelProdukPetani.getFoto_produk()).into(binding.fotoProduk);
                sFotoProduk = modelProdukPetani.getFoto_produk();
                binding.namaProduk.setText(modelProdukPetani.getNama_produk());
                binding.hargaProduk.setText(modelProdukPetani.getHarga_produk());
                idSatuan = Integer.valueOf(modelProdukPetani.getId_satuan().isEmpty() ? modelProdukPetani.getId_satuan() : "0");
                idKategori = Integer.valueOf(modelProdukPetani.getId_kategori().isEmpty() ? modelProdukPetani.getId_kategori() : "0");
            }

            //Check session login
            sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            sIdToko = user.get(SessionManager.USER_ID_TOKO);

            //add image
            binding.fotoProduk.setOnClickListener(view -> {
                if(checkAndRequestPermissions(Add_Produk.this)){
                    chooseImage(Add_Produk.this);
                }
            });

            //spinner kategori produk
            loadListKategoriProduk("");
            binding.spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    sKategoriProduk = listKategori.get(binding.spinnerKategori.getSelectedItemPosition()).getId_kategori();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    if (idKategori != 0){
                        sKategoriProduk = listKategori.get(idKategori).getId_kategori();
                    }
                    sKategoriProduk = listKategori.get(0).getId_kategori();
                }
            });

            //spinner satuan produk
            loadListSatuanProduk("");
            binding.spinnerSatuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    sSatuanProdukk = listSatuan.get(binding.spinnerSatuan.getSelectedItemPosition()).getId_satuan();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    if (idSatuan != 0){
                        sSatuanProdukk = listSatuan.get(idSatuan).getId_satuan();
                    }
                    sSatuanProdukk = listSatuan.get(0).getId_satuan();
                }
            });

            //simpan ke database
            binding.simpanProduk.setOnClickListener(view -> {
                if(sFotoProduk.equalsIgnoreCase("")){
                    Toast.makeText(Add_Produk.this, "Pilih gambar untuk produk yang kamu jual", Toast.LENGTH_SHORT).show();
                }else if(binding.namaProduk.getText().toString().length()==0){
                    binding.namaProduk.setError("Masukkan nama produk kamu");
                }else if(binding.hargaProduk.getText().toString().length()==0){
                    binding.hargaProduk.setError("Masukkan harga produk kamu");
                }else{
                    if(modelProdukPetani.getId_produk()!=null){
                        if(!sFotoProduk.equals(modelProdukPetani.getFoto_produk())){
                            ubahFoto = "1";
                        }
                        editProdukToko(
                                modelProdukPetani.getId_produk(),
                                ubahFoto,
                                sFotoProduk,
                                binding.namaProduk.getText().toString(),
                                binding.hargaProduk.getText().toString(),
                                sSatuanProdukk,
                                sKategoriProduk
                        );
                    }else{
                        addProdukToko(
                                sFotoProduk,
                                binding.namaProduk.getText().toString(),
                                sIdToko,
                                binding.hargaProduk.getText().toString(),
                                sSatuanProdukk,
                                sKategoriProduk,
                                "Aktif"
                        );
                    }
                }
            });
        } catch (Exception exception){
            System.out.println(exception);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(Add_Produk.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camera.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(Add_Produk.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                }else if (ContextCompat.checkSelfPermission(Add_Produk.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chooseImage(Add_Produk.this);
                }
                break;
        }
    }


    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        binding.fotoProduk.setImageBitmap(selectedImage);
                        sFotoProduk = getStringImage(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                binding.fotoProduk.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                sFotoProduk = getStringImage(BitmapFactory.decodeFile(picturePath));
                                Log.d("LOCATION", picturePath);
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void loadListKategoriProduk(String s) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listKategori(s);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    listKategori = response.body().getList_kategori();

                    for(int i=0;i<listKategori.size();i++){
                        listNamaKategori.add(listKategori.get(i).getNama_kategori());
                    }

                    adapterSpinnerKategoriProduk = new AdapterSpinnerKategoriProduk(getApplicationContext(), listNamaKategori);
                    binding.spinnerKategori.setAdapter(adapterSpinnerKategoriProduk);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(Add_Produk.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadListSatuanProduk(String s) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listSatuan(s);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    listSatuan = response.body().getList_satuan();
                    sSatuanProdukk = listSatuan.get(0).getId_satuan();

                    for(int i=0;i<listSatuan.size();i++){
                        listNamaSatuan.add(listSatuan.get(i).getNama_satuan());
                    }

                    adapterSpinnerSatuanProduk = new AdapterSpinnerSatuanProduk(getApplicationContext(), listNamaSatuan);
                    binding.spinnerSatuan.setAdapter(adapterSpinnerSatuanProduk);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(Add_Produk.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addProdukToko(
            String foto,
            String nama,
            String idToko,
            String harga,
            String idSatuan,
            String idKategori,
            String status
    ){
        progress = new ProgressDialog(Add_Produk.this);
        progress.setMessage("Tunggu Sebentar");
        progress.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.addProduk(foto, nama, idToko, harga, idSatuan, idKategori, status);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if(response.body().getValue().equalsIgnoreCase("1")){
                    Toast.makeText(Add_Produk.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent goToMainActivity = new Intent(Add_Produk.this, AturProdukPenjualan.class);
                    goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goToMainActivity);
                    progress.dismiss();
                }else{
                    Toast.makeText(Add_Produk.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(Add_Produk.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editProdukToko(
            String idProduk,
            String ubahFoto,
            String foto,
            String namaProduk,
            String hargaProduk,
            String idSatuan,
            String idKategori
    ){
        progress = new ProgressDialog(Add_Produk.this);
        progress.setMessage("Tunggu Sebentar");
        progress.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.editProdukToko(
                idProduk,
                ubahFoto,
                foto,
                namaProduk,
                hargaProduk,
                idSatuan,
                idKategori
        );
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if(response.body().getValue().equalsIgnoreCase("1")){
                    Toast.makeText(Add_Produk.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent goToMainActivity = new Intent(Add_Produk.this, AturProdukPenjualan.class);
                    goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goToMainActivity);
                    progress.dismiss();
                }else{
                    Toast.makeText(Add_Produk.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(Add_Produk.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ModelProdukPetani setupEditDataItem(Intent getEditData){
        ModelProdukPetani modelProdukPetani = new ModelProdukPetani();
        if(getEditData!=null){
            modelProdukPetani.setId_produk(getEditData.getStringExtra("idProduk"));
            modelProdukPetani.setFoto_produk(getEditData.getStringExtra("foto"));
            modelProdukPetani.setNama_produk(getEditData.getStringExtra("namaProduk"));
            modelProdukPetani.setHarga_produk(getEditData.getStringExtra("hargaProduk"));
            modelProdukPetani.setId_satuan(getEditData.getStringExtra("idSatuan"));
            modelProdukPetani.setId_kategori(getEditData.getStringExtra("idKategori"));
        }
        return modelProdukPetani;
    }
}
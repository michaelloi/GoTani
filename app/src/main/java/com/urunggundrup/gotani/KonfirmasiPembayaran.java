package com.urunggundrup.gotani;

import static com.urunggundrup.gotani.Add_Produk.checkAndRequestPermissions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.urunggundrup.gotani.databinding.ActivityAddProdukBinding;
import com.urunggundrup.gotani.databinding.ActivityKonfirmasiPembayaranBinding;
import com.urunggundrup.gotani.model.Model;

import java.io.ByteArrayOutputStream;
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

public class KonfirmasiPembayaran extends AppCompatActivity {

    private ActivityKonfirmasiPembayaranBinding binding;
    SessionManager sessionManager;
    String sIdUser, sFotoBuktiTransfer="";
    Intent getData;
    private ProgressDialog progress;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Konfirmasi Pembayaran");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityKonfirmasiPembayaranBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try{
            //get data Intent
            getData = getIntent();

            //Check session login
            sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            sIdUser = user.get(SessionManager.USER_ID);

            //set total yang harus dibayar
            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("Rp ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);

            String sHargaPesanan = kursIndonesia.format(Integer.valueOf(getData.getStringExtra("total_pembayaran")));
            binding.totalBelanja.setText("Total Belanja "+sHargaPesanan);

            binding.nomorOrderan.setText(getData.getStringExtra("no_pesanan"));
            binding.namaRekening.setText(getData.getStringExtra("nama_bank") + " A.N. " + getData.getStringExtra("atas_nama"));
            binding.noRekening.setText(getData.getStringExtra("nomor_rekening"));

            binding.fotoTransferan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkAndRequestPermissions(KonfirmasiPembayaran.this)){
                        chooseImage(KonfirmasiPembayaran.this);
                    }
                }
            });

            binding.simpanPerubahan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(sFotoBuktiTransfer.equalsIgnoreCase("")){
                        Toast.makeText(KonfirmasiPembayaran.this, "Pilih gambar untuk bukti pembayaran", Toast.LENGTH_SHORT).show();
                    }else{
                        konfirmasiPembayaranUser(sFotoBuktiTransfer, sIdUser, getData.getStringExtra("id_pesanan"), "1", getData.getStringExtra("no_pesanan"));
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
                if (ContextCompat.checkSelfPermission(KonfirmasiPembayaran.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camera.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(KonfirmasiPembayaran.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                }else if (ContextCompat.checkSelfPermission(KonfirmasiPembayaran.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chooseImage(KonfirmasiPembayaran.this);
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
                        binding.fotoTransferan.setImageBitmap(selectedImage);
                        sFotoBuktiTransfer = getStringImage(selectedImage);
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
                                binding.fotoTransferan.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                sFotoBuktiTransfer = getStringImage(BitmapFactory.decodeFile(picturePath));
                                Log.d("LOCATION", picturePath);
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void konfirmasiPembayaranUser(String foto_bukti_pembayaran, String id_user, String id_status_pesanan, String id_rekening_pembayaran, String no_pesanan){
        progress = new ProgressDialog(KonfirmasiPembayaran.this);
        progress.setMessage("Tunggu Sebentar");
        progress.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.konfirmasiPembayaran(foto_bukti_pembayaran, id_user, id_status_pesanan, id_rekening_pembayaran, no_pesanan);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if(response.body().getValue().equalsIgnoreCase("1")){
                    Toast.makeText(KonfirmasiPembayaran.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent goToMainActivity = new Intent(KonfirmasiPembayaran.this, MainActivity.class);
                    goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goToMainActivity);
                    progress.dismiss();
                }else{
                    Toast.makeText(KonfirmasiPembayaran.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(KonfirmasiPembayaran.this, "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
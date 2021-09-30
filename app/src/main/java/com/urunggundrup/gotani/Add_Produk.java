package com.urunggundrup.gotani;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.urunggundrup.gotani.databinding.ActivityAddProdukBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Add_Produk extends AppCompatActivity {

    private ActivityAddProdukBinding binding;
    MarshMallowPermission marshMallowPermission;
    int a;
    SessionManager sessionManager;
    String sIdToko, userChoosenTask, sFotoProduk="";
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1,PLACE_PICKER_REQUEST = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Tambah Produk");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding = ActivityAddProdukBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Check session login
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        sIdToko = user.get(SessionManager.USER_ID_TOKO);

        //add image
        binding.fotoProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!marshMallowPermission.checkPermissionForCamera()) {
                    marshMallowPermission.requestPermissionForCamera();
                } else {
                    a = 3;
                    selectImage();
                }
            }
        });

        //simpan ke database
        binding.simpanProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sFotoProduk.equalsIgnoreCase("")){
                    Toast.makeText(Add_Produk.this, "Pilih gambar untuk produk yang kamu jual", Toast.LENGTH_SHORT).show();
                }else if(binding.namaProduk.getText().toString().length()==0){
                    binding.namaProduk.setError("Masukkan nama produk kamu");
                }else if(binding.hargaProduk.getText().toString().length()==0){
                    binding.hargaProduk.setError("Masukkan harga produk kamu");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void selectImage() {
        final CharSequence[] items = {"Ambil Foto", "Pilih dari Galeri",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Add_Produk.this);
        builder.setTitle("Tambah Gambar");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(Add_Produk.this);

                if (items[item].equals("Ambil Foto")) {
                    userChoosenTask = "Ambil Foto";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Pilih dari Galeri")) {
                    userChoosenTask = "Pilih dari Galeri";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Ambil Foto"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Pilih dari Galeri"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data);
            }
            else if (requestCode == REQUEST_CAMERA){
                onCaptureImageResult(data);
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (a == 3) {
            //foto.setBackground(thumbnail);
            //Drawable d = new BitmapDrawable(getResources(), thumbnail);
            binding.fotoProduk.setImageBitmap(thumbnail);
            sFotoProduk = getStringImage(thumbnail);
        }
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        Bitmap newbm = null;
        int w1,h1,w2,h2;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(bm.getWidth()>=500)
        {
            w1=500;
            h1=(w1*bm.getHeight())/bm.getWidth();

            newbm = Bitmap.createScaledBitmap(bm, w1, h1, false);
        }
        //ktp.setImageBitmap(bm);
        if (a == 3) {
            //Drawable d = new BitmapDrawable(getResources(), bm);
            binding.fotoProduk.setImageBitmap(newbm);
            sFotoProduk = getStringImage(newbm);
        }
        /*
        if(a==2) {
            belakang.setImageBitmap(bm);
            belakang2=getStringImage(bm);
            //profil=getStringImage(thumbnail);
        }
        */

    }
}
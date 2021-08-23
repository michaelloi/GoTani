package com.example.gotani;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class PetaniDashboard extends AppCompatActivity {

    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petani_dashboard);

        relativeLayout = findViewById(R.id.produk_pejualan);

        relativeLayout.setOnClickListener(v -> {
            Intent a = new Intent(this, AturProdukPenjualan.class);
        });
    }
}
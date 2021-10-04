package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.urunggundrup.gotani.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterSpinnerSatuanProduk extends BaseAdapter {
    Context context;
    private List<String> listSatuanProduk;
    LayoutInflater inflter;

    public AdapterSpinnerSatuanProduk(Context applicationContext, List<String> listSatuanProduk) {
        this.context = applicationContext;
        this.listSatuanProduk = listSatuanProduk;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return listSatuanProduk.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_kategori_item, null);
        TextView namaKategori = view.findViewById(R.id.textSpinner);
        namaKategori.setText(listSatuanProduk.get(i));
        return view;
    }
}

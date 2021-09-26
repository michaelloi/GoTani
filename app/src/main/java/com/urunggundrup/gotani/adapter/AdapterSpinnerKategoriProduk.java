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

public class AdapterSpinnerKategoriProduk extends BaseAdapter {
    Context context;
    private List<String> listKategoriProduk = new ArrayList<>();
    LayoutInflater inflter;

    public AdapterSpinnerKategoriProduk(Context applicationContext, List<String> listKategoriProduk) {
        this.context = applicationContext;
        this.listKategoriProduk = listKategoriProduk;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return listKategoriProduk.size();
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
        namaKategori.setText(listKategoriProduk.get(i));
        return view;
    }
}

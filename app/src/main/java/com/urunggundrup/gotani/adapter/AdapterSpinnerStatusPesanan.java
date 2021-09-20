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

public class AdapterSpinnerStatusPesanan extends BaseAdapter {
    Context context;
    private List<String> listStatusPesanan = new ArrayList<>();
    LayoutInflater inflter;

    public AdapterSpinnerStatusPesanan(Context applicationContext, List<String> listStatusPesanan) {
        this.context = applicationContext;
        this.listStatusPesanan = listStatusPesanan;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return listStatusPesanan.size();
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
        view = inflter.inflate(R.layout.spinner_item, null);
        TextView namaLokasiText = view.findViewById(R.id.textSpinner);

        namaLokasiText.setText(listStatusPesanan.get(i));

        return view;
    }
}

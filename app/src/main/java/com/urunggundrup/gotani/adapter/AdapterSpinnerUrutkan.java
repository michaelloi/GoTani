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

public class AdapterSpinnerUrutkan extends BaseAdapter {
    Context context;
    private List<String> listUrutkan = new ArrayList<>();
    LayoutInflater inflter;

    public AdapterSpinnerUrutkan(Context applicationContext, List<String> listUrutkan) {
        this.context = applicationContext;
        this.listUrutkan = listUrutkan;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return listUrutkan.size();
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
        TextView urutkan = view.findViewById(R.id.textSpinner);
        urutkan.setText(listUrutkan.get(i));
        return view;
    }
}

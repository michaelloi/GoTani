package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.model.ModelLokasi;

import java.util.ArrayList;
import java.util.List;

public class AdapterSpinnerLokasi extends BaseAdapter {
    Context context;
    private List<String> listLokasi = new ArrayList<>();
    LayoutInflater inflter;

    public AdapterSpinnerLokasi(Context applicationContext, List<String> listLokasi) {
        this.context = applicationContext;
        this.listLokasi = listLokasi;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return listLokasi.size();
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

        namaLokasiText.setText(listLokasi.get(i));

        return view;
    }
}

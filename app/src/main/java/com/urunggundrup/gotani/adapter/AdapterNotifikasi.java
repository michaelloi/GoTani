package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.model.ModelAlamatUser;
import com.urunggundrup.gotani.model.ModelNotifikasi;

import java.util.ArrayList;
import java.util.List;

public class AdapterNotifikasi extends RecyclerView.Adapter<AdapterNotifikasi.ViewHolder> {
    Context context;
    private List<ModelNotifikasi> listNotifikasi = new ArrayList<>();

    public AdapterNotifikasi(Context context, List<ModelNotifikasi> listNotifikasi) {
        this.context = context;
        this.listNotifikasi = listNotifikasi;
    }

    @Override
    public AdapterNotifikasi.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_notifikasi_item, parent, false);
        AdapterNotifikasi.ViewHolder holder = new AdapterNotifikasi.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterNotifikasi.ViewHolder holder, final int position) {
        final ModelNotifikasi modelNotifikasi = listNotifikasi.get(position);

        holder.judulNotifikasi.setText(modelNotifikasi.getJudul_notifikasi());
        holder.tanggalNotifikasi.setText(modelNotifikasi.getCreated_date());
        holder.isiNotifikasi.setText(modelNotifikasi.getIsi_notifikasi());
    }

    @Override
    public int getItemCount() {
        return listNotifikasi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView judulNotifikasi, tanggalNotifikasi, isiNotifikasi;

        public ViewHolder(View itemView) {
            super(itemView);
            judulNotifikasi = itemView.findViewById(R.id.judulNotifikasi);
            tanggalNotifikasi = itemView.findViewById(R.id.tanggalNotifikasi);
            isiNotifikasi = itemView.findViewById(R.id.isiNotifikasi);

        }
    }
}

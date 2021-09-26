package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.model.ModelAlamatUser;

import java.util.ArrayList;
import java.util.List;

public class AdapterAlamatUser extends RecyclerView.Adapter<AdapterAlamatUser.ViewHolder> {
    Context context;
    private List<ModelAlamatUser> listAlamat = new ArrayList<>();

    public AdapterAlamatUser(Context context, List<ModelAlamatUser> listAlamat) {
        this.context = context;
        this.listAlamat = listAlamat;
    }

    @Override
    public AdapterAlamatUser.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_alamat_item, parent, false);
        AdapterAlamatUser.ViewHolder holder = new AdapterAlamatUser.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterAlamatUser.ViewHolder holder, final int position) {
        final ModelAlamatUser modelAlamatUser = listAlamat.get(position);

        holder.judulAlamat.setText(modelAlamatUser.getJudul_alamat());
        holder.namaPenerima.setText(modelAlamatUser.getNama_penerima());
        holder.nohpPenerima.setText(modelAlamatUser.getNohp_penerima());
        holder.alamatPenerima.setText(modelAlamatUser.getAlamat());
    }

    @Override
    public int getItemCount() {
        return listAlamat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView judulAlamat, namaPenerima, nohpPenerima, alamatPenerima;

        public ViewHolder(View itemView) {
            super(itemView);
            judulAlamat = itemView.findViewById(R.id.judulAlamat);
            namaPenerima = itemView.findViewById(R.id.namaPenerima);
            nohpPenerima = itemView.findViewById(R.id.nohpPenerima);
            alamatPenerima = itemView.findViewById(R.id.alamatPenerima);

        }
    }
}

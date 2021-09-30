package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.urunggundrup.gotani.AlamatListListener;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.model.ModelAlamatUser;

import java.util.ArrayList;
import java.util.List;

public class AdapterAlamatCheckout extends RecyclerView.Adapter<AdapterAlamatCheckout.ViewHolder> {
    Context context;
    private List<ModelAlamatUser> listAlamat;
    AlamatListListener alamatListListener;
    public Integer selected =-1;

    public AdapterAlamatCheckout(Context context, List<ModelAlamatUser> listAlamat, AlamatListListener alamatListListener) {
        this.context = context;
        this.listAlamat = listAlamat;
        this.alamatListListener=alamatListListener;
    }

    @Override
    public AdapterAlamatCheckout.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_alamat_checkout_item, parent, false);
        AdapterAlamatCheckout.ViewHolder holder = new AdapterAlamatCheckout.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterAlamatCheckout.ViewHolder holder, final int position) {
        final ModelAlamatUser modelAlamatUser = listAlamat.get(position);
        Integer positionChecked = position;

        holder.judulAlamat.setText(modelAlamatUser.getJudul_alamat());
        holder.namaPenerima.setText(modelAlamatUser.getNama_penerima());
        holder.nohpPenerima.setText(modelAlamatUser.getNohp_penerima());
        holder.alamatPenerima.setText(modelAlamatUser.getAlamat());

        if (selected==position){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected=positionChecked;
                notifyDataSetChanged();
                alamatListListener.getIdAlamat(selected);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listAlamat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView judulAlamat, namaPenerima, nohpPenerima, alamatPenerima;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            judulAlamat = itemView.findViewById(R.id.judulAlamat);
            namaPenerima = itemView.findViewById(R.id.namaPenerima);
            nohpPenerima = itemView.findViewById(R.id.nohpPenerima);
            alamatPenerima = itemView.findViewById(R.id.alamatPenerima);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}

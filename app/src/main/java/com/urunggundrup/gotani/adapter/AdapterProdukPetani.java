package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.model.ModelNotifikasi;
import com.urunggundrup.gotani.model.ModelProdukPetani;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterProdukPetani extends RecyclerView.Adapter<AdapterProdukPetani.ViewHolder> {
    Context context;
    private List<ModelProdukPetani> listProdukPetani = new ArrayList<>();

    public AdapterProdukPetani(Context context, List<ModelProdukPetani> listProdukPetani) {
        this.context = context;
        this.listProdukPetani = listProdukPetani;
    }

    @Override
    public AdapterProdukPetani.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_produk_petani_item, parent, false);
        AdapterProdukPetani.ViewHolder holder = new AdapterProdukPetani.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterProdukPetani.ViewHolder holder, final int position) {
        final ModelProdukPetani modelProdukPetani = listProdukPetani.get(position);

        Picasso.with(holder.fotoProduk.getContext()).load(holder.urlFoto+modelProdukPetani.getFoto_produk()).into(holder.fotoProduk);
        holder.namaProduk.setText(modelProdukPetani.getNama_produk());

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        holder.hargaProduk.setText(kursIndonesia.format(Double.valueOf(modelProdukPetani.getHarga_produk()))+" / "+modelProdukPetani.getNama_satuan());
        holder.tanggalProduk.setText(modelProdukPetani.getCreated_date());
        holder.kategoriProduk.setText(modelProdukPetani.getNama_kategori());
    }

    @Override
    public int getItemCount() {
        return listProdukPetani.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView fotoProduk;
        public TextView namaProduk, hargaProduk, tanggalProduk, kategoriProduk;
        public String urlFoto;

        public ViewHolder(View itemView) {
            super(itemView);
            fotoProduk = itemView.findViewById(R.id.fotoProduk);
            namaProduk = itemView.findViewById(R.id.namaProduk);
            hargaProduk = itemView.findViewById(R.id.hargaProduk);
            tanggalProduk = itemView.findViewById(R.id.tanggalProduk);
            kategoriProduk = itemView.findViewById(R.id.kategoriProduk);
            urlFoto = itemView.getResources().getString(R.string.urlaccesdocuments);
        }
    }
}

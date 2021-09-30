package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.DetailProdukUser;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelProdukUser;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterProdukUserVertical extends RecyclerView.Adapter<AdapterProdukUserVertical.ViewHolder> implements Filterable{
    Context context;
    private List<ModelProdukUser> listProdukUser;
    private List<ModelProdukUser> listProdukUserFilter;

    public AdapterProdukUserVertical(Context context, List<ModelProdukUser> listProdukUser) {
        this.context = context;
        this.listProdukUser = listProdukUser;
        listProdukUserFilter=new ArrayList<>(listProdukUser);
    }

    @Override
    public AdapterProdukUserVertical.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_lihat_semua_produk_user_item, parent, false);
        AdapterProdukUserVertical.ViewHolder holder = new AdapterProdukUserVertical.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterProdukUserVertical.ViewHolder holder, final int position) {
        final ModelProdukUser modelProdukUser = listProdukUser.get(position);

        Picasso.with(holder.fotoProduk.getContext()).load(holder.urlFoto+modelProdukUser.getFoto_produk()).into(holder.fotoProduk);
        holder.namaProduk.setText(modelProdukUser.getNama_produk());

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        holder.hargaProduk.setText(kursIndonesia.format(Double.valueOf(modelProdukUser.getHarga_produk()))+" / "+modelProdukUser.getNama_satuan());
        holder.lokasiProduk.setText(modelProdukUser.getNama_lokasi());

        holder.cardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailProdukUser = new Intent(view.getContext(), DetailProdukUser.class);
                goToDetailProdukUser.putExtra("id_produk", modelProdukUser.getId_produk());
                goToDetailProdukUser.putExtra("nama_produk", modelProdukUser.getNama_produk());
                goToDetailProdukUser.putExtra("foto_produk", modelProdukUser.getFoto_produk());
                goToDetailProdukUser.putExtra("harga_produk", modelProdukUser.getHarga_produk());
                goToDetailProdukUser.putExtra("nama_satuan", modelProdukUser.getNama_satuan());
                goToDetailProdukUser.putExtra("nama_kategori", modelProdukUser.getNama_kategori());
                goToDetailProdukUser.putExtra("status", modelProdukUser.getStatus());
                goToDetailProdukUser.putExtra("created_date", modelProdukUser.getCreated_date());
                goToDetailProdukUser.putExtra("nama_lokasi", modelProdukUser.getNama_lokasi());
                goToDetailProdukUser.putExtra("nama_toko", modelProdukUser.getNama_toko());
                goToDetailProdukUser.putExtra("keterangan_satuan", modelProdukUser.getKeterangan_satuan());
                view.getContext().startActivity(goToDetailProdukUser);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProdukUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView fotoProduk;
        public TextView namaProduk, hargaProduk, lokasiProduk;
        public String urlFoto;
        public CardView cardViewItem;

        public ViewHolder(View itemView) {
            super(itemView);
            fotoProduk = itemView.findViewById(R.id.fotoProduk);
            namaProduk = itemView.findViewById(R.id.namaProduk);
            hargaProduk = itemView.findViewById(R.id.hargaProduk);
            lokasiProduk = itemView.findViewById(R.id.lokasiProduk);
            cardViewItem = itemView.findViewById(R.id.cardViewProduk);
            urlFoto = itemView.getResources().getString(R.string.urlaccesdocuments);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ModelProdukUser> filteredList = new ArrayList<>();

            if (charSequence==null||charSequence.length()==0){
                filteredList.addAll(listProdukUserFilter);
            }
            else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (ModelProdukUser item : listProdukUserFilter){
                    if (item.getNama_produk().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listProdukUser.clear();
            listProdukUser.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };
}

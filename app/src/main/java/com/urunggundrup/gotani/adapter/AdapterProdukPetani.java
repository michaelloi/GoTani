package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelProdukPetani;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterProdukPetani extends RecyclerView.Adapter<AdapterProdukPetani.ViewHolder> {
    Context context;
    private List<ModelProdukPetani> listProdukPetani = new ArrayList<>();
    private List<ModelProdukPetani> listProdukPetaniFinal = new ArrayList<>();

    public AdapterProdukPetani(Context context, List<ModelProdukPetani> listProdukPetani) {
        this.context = context;
        this.listProdukPetani = listProdukPetani;
        listProdukPetaniFinal = new ArrayList<>(listProdukPetani);
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
        int positionChecked = position;

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

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(holder.urlAccess)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Register_Api api = retrofit.create(Register_Api.class);
                Call<Model> call = api.deleteProdukPertanian(listProdukPetani.get(positionChecked).getId_produk());
                call.enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {

                        if(response.body().getValue().equalsIgnoreCase("1")){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                listProdukPetaniFinal.removeIf(e -> e.getId_produk().equals(listProdukPetani.get(positionChecked).getId_produk()));
                                listProdukPetani.remove(positionChecked);
                                Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                notifyItemRemoved(positionChecked);
                                notifyItemRangeRemoved(positionChecked, listProdukPetani.size());
                            }
                        }else{
                            Toast.makeText(view.getContext(), "Maaf terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Model> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(view.getContext(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProdukPetani.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView fotoProduk, deleteItem;
        public TextView namaProduk, hargaProduk, tanggalProduk, kategoriProduk;
        public String urlFoto, urlAccess;

        public ViewHolder(View itemView) {
            super(itemView);
            fotoProduk = itemView.findViewById(R.id.fotoProduk);
            deleteItem = itemView.findViewById(R.id.deleteItem);
            namaProduk = itemView.findViewById(R.id.namaProduk);
            hargaProduk = itemView.findViewById(R.id.hargaProduk);
            tanggalProduk = itemView.findViewById(R.id.tanggalProduk);
            kategoriProduk = itemView.findViewById(R.id.kategoriProduk);
            urlFoto = itemView.getResources().getString(R.string.urlaccesdocuments);
            urlAccess = itemView.getResources().getString(R.string.urlacces);
        }
    }
}

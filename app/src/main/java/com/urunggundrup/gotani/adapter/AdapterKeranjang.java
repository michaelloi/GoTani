package com.urunggundrup.gotani.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.KeranjangListListener;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelKeranjang;
import com.urunggundrup.gotani.model.ModelProdukUser;

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

public class AdapterKeranjang extends RecyclerView.Adapter<AdapterKeranjang.ViewHolder> {
    Context context;
    private List<ModelKeranjang> listKeranjang = new ArrayList<>();
    private List<ModelKeranjang> listKeranjangCheckout = new ArrayList<>();
    KeranjangListListener keranjangListListener;

    public AdapterKeranjang(Context context, List<ModelKeranjang> listKeranjang, KeranjangListListener keranjangListListener) {
        this.context = context;
        this.listKeranjang = listKeranjang;
        listKeranjangCheckout = new ArrayList<>(listKeranjang);
        this.keranjangListListener = keranjangListListener;
    }

    @Override
    public AdapterKeranjang.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_keranjang_item, parent, false);
        AdapterKeranjang.ViewHolder holder = new AdapterKeranjang.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterKeranjang.ViewHolder holder, final int position) {
        final ModelKeranjang modelKeranjang = listKeranjang.get(position);
        int positionChecked = position;

        Picasso.with(holder.fotoProduk.getContext()).load(holder.urlFoto+modelKeranjang.getFoto_produk()).into(holder.fotoProduk);
        holder.namaProduk.setText(modelKeranjang.getNama_produk());
        holder.jumlahPesanan.setText(modelKeranjang.getJumlah_pesanan()+" "+modelKeranjang.getNama_satuan());

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        Integer hargaPesanan = Integer.valueOf(modelKeranjang.getJumlah_pesanan()) * Integer.valueOf(modelKeranjang.getHarga_produk());
        String sHargaPesanan = kursIndonesia.format(hargaPesanan);

        holder.hargaProduk.setText(sHargaPesanan);
        holder.hargaProdukDetail.setText("(@"+modelKeranjang.getJumlah_pesanan()+" x "+kursIndonesia.format(Double.valueOf(modelKeranjang.getHarga_produk()))+")");
        holder.namaToko.setText(modelKeranjang.getNama_toko());

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(holder.urlAccess)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Register_Api api = retrofit.create(Register_Api.class);
                Call<Model> call = api.deleteKeranjangItem(listKeranjang.get(positionChecked).getId_keranjang());
                call.enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {

                        if(response.body().getValue().equalsIgnoreCase("1")){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                listKeranjangCheckout.removeIf(e -> e.getId_keranjang().equals(listKeranjang.get(positionChecked).getId_keranjang()));
                                listKeranjang.remove(positionChecked);
                                Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                keranjangListListener.getListChange(listKeranjangCheckout);
                                keranjangListListener.getJumlahToko(listKeranjangCheckout);
                                notifyItemRemoved(positionChecked);
                                notifyItemRangeRemoved(positionChecked, listKeranjang.size());
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

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()){
                    listKeranjangCheckout.add(listKeranjang.get(positionChecked));
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        listKeranjangCheckout.removeIf(e -> e.getId_keranjang().equals(listKeranjang.get(positionChecked).getId_keranjang()));
                    }
                }
                keranjangListListener.getListChange(listKeranjangCheckout);
            }
        });

        holder.ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(view.getRootView().getContext());
                View dialogView= LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.dialog_item_keranjang,null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                final AlertDialog show = builder.show();

                TextView namaProduk, satuanProduk;
                ImageView fotoProduk;
                EditText eJumlahPesanan;
                LinearLayout simpanPerubahan;

                namaProduk = dialogView.findViewById(R.id.namaProduk);
                satuanProduk = dialogView.findViewById(R.id.satuanProduk);
                fotoProduk = dialogView.findViewById(R.id.fotoProduk);
                eJumlahPesanan = dialogView.findViewById(R.id.editJumlahPesanan);
                simpanPerubahan = dialogView.findViewById(R.id.simpanPerubahan);

                Picasso.with(fotoProduk.getContext()).load(holder.urlFoto+listKeranjang.get(positionChecked).getFoto_produk()).into(fotoProduk);
                namaProduk.setText(listKeranjang.get(positionChecked).getNama_produk());
                satuanProduk.setText(listKeranjang.get(positionChecked).getNama_satuan());
                eJumlahPesanan.setText(listKeranjang.get(positionChecked).getJumlah_pesanan());

                simpanPerubahan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(holder.urlAccess)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        Register_Api api = retrofit.create(Register_Api.class);
                        Call<Model> call = api.editKeranjangItem(listKeranjang.get(positionChecked).getId_keranjang(), eJumlahPesanan.getText().toString());
                        call.enqueue(new Callback<Model>() {
                            @Override
                            public void onResponse(Call<Model> call, Response<Model> response) {

                                if(response.body().getValue().equalsIgnoreCase("1")){
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        listKeranjang.get(positionChecked).setJumlah_pesanan(eJumlahPesanan.getText().toString());
                                        keranjangListListener.getListChange(listKeranjangCheckout);
                                        notifyItemChanged(positionChecked);
                                        notifyItemRangeChanged(positionChecked, listKeranjang.size());
                                        show.dismiss();
                                    }
                                }else{
                                    show.dismiss();
                                    Toast.makeText(view.getContext(), "Maaf terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Model> call, Throwable t) {
                                t.printStackTrace();
                                show.dismiss();
                                Toast.makeText(view.getContext(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
                            }
                        });



                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return listKeranjang.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView fotoProduk, deleteItem;
        public TextView namaProduk, hargaProduk, hargaProdukDetail, jumlahPesanan, ubah, namaToko;
        public String urlFoto, urlAccess;
        public CheckBox checkBox;


        public ViewHolder(View itemView) {
            super(itemView);
            fotoProduk = itemView.findViewById(R.id.fotoProduk);
            deleteItem = itemView.findViewById(R.id.deleteItem);
            namaProduk = itemView.findViewById(R.id.namaProduk);
            hargaProduk = itemView.findViewById(R.id.hargaProduk);
            hargaProdukDetail = itemView.findViewById(R.id.hargaProdukDetail);
            jumlahPesanan = itemView.findViewById(R.id.jumlahPesanan);
            namaToko = itemView.findViewById(R.id.namaToko);
            checkBox = itemView.findViewById(R.id.checkbox);
            ubah = itemView.findViewById(R.id.ubah);
            urlFoto = itemView.getResources().getString(R.string.urlaccesdocuments);
            urlAccess = itemView.getResources().getString(R.string.urlacces);
        }
    }
}

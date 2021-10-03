package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.KeranjangListListener;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelKeranjang;

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

public class AdapterKeranjangCheckout extends RecyclerView.Adapter<AdapterKeranjangCheckout.ViewHolder> {
    Context context;
    private List<ModelKeranjang> listKeranjangCheckout;

    public AdapterKeranjangCheckout(Context context, List<ModelKeranjang> listKeranjangCheckout) {
        this.context = context;
        this.listKeranjangCheckout = listKeranjangCheckout;
    }

    @Override
    public AdapterKeranjangCheckout.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_keranjang_checkout_item, parent, false);
        AdapterKeranjangCheckout.ViewHolder holder = new AdapterKeranjangCheckout.ViewHolder(v);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterKeranjangCheckout.ViewHolder holder, final int position) {
        final ModelKeranjang modelKeranjang = listKeranjangCheckout.get(position);

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
    }

    @Override
    public int getItemCount() {
        return listKeranjangCheckout.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView fotoProduk;
        public TextView namaProduk, hargaProduk, hargaProdukDetail, jumlahPesanan, namaToko;
        public String urlFoto, urlAccess;


        public ViewHolder(View itemView) {
            super(itemView);
            fotoProduk = itemView.findViewById(R.id.fotoProduk);
            namaProduk = itemView.findViewById(R.id.namaProduk);
            hargaProduk = itemView.findViewById(R.id.hargaProduk);
            hargaProdukDetail = itemView.findViewById(R.id.hargaProdukDetail);
            jumlahPesanan = itemView.findViewById(R.id.jumlahPesanan);
            namaToko = itemView.findViewById(R.id.namaToko);
            urlFoto = itemView.getResources().getString(R.string.urlaccesdocuments);
            urlAccess = itemView.getResources().getString(R.string.urlacces);
        }
    }
}

package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.KeranjangListListener;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelKeranjang;
import com.urunggundrup.gotani.model.ModelProdukUser;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

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

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        Integer hargaPesanan = Integer.valueOf(modelKeranjang.getJumlah_pesanan()) * Integer.valueOf(modelKeranjang.getHarga_produk());
        String sHargaPesanan = kursIndonesia.format(hargaPesanan);

        holder.hargaProduk.setText(sHargaPesanan);
        holder.hargaProdukDetail.setText("(@"+modelKeranjang.getJumlah_pesanan()+" x "+kursIndonesia.format(Double.valueOf(modelKeranjang.getHarga_produk()))+")");

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
    }

    @Override
    public int getItemCount() {
        return listKeranjang.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView fotoProduk;
        public TextView namaProduk, hargaProduk, hargaProdukDetail, jumlahPesanan, ubah;
        public String urlFoto;
        public CheckBox checkBox;


        public ViewHolder(View itemView) {
            super(itemView);
            fotoProduk = itemView.findViewById(R.id.fotoProduk);
            namaProduk = itemView.findViewById(R.id.namaProduk);
            hargaProduk = itemView.findViewById(R.id.hargaProduk);
            hargaProdukDetail = itemView.findViewById(R.id.hargaProdukDetail);
            jumlahPesanan = itemView.findViewById(R.id.jumlahPesanan);
            checkBox = itemView.findViewById(R.id.checkbox);
            ubah = itemView.findViewById(R.id.ubah);
            urlFoto = itemView.getResources().getString(R.string.urlaccesdocuments);
        }
    }
}

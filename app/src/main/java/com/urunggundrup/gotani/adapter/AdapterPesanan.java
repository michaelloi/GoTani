package com.urunggundrup.gotani.adapter;

import static com.urunggundrup.gotani.Add_Produk.checkAndRequestPermissions;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urunggundrup.gotani.Add_Produk;
import com.urunggundrup.gotani.DetailPesanan;
import com.urunggundrup.gotani.DetailProdukUser;
import com.urunggundrup.gotani.KonfirmasiPembayaran;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelKeranjang;
import com.urunggundrup.gotani.model.ModelPesanan;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterPesanan extends RecyclerView.Adapter<AdapterPesanan.ViewHolder> {
    Context context;
    private List<ModelPesanan> listPesanan;

    public AdapterPesanan(Context context, List<ModelPesanan> listPesanan) {
        this.context = context;
        this.listPesanan = listPesanan;
    }

    @Override
    public AdapterPesanan.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_pesanan_item, parent, false);
        AdapterPesanan.ViewHolder holder = new AdapterPesanan.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterPesanan.ViewHolder holder, final int position) {
        final ModelPesanan modelPesanan = listPesanan.get(position);
        int positionChecked = position;

        holder.nomorOrderan.setText("No.Orderan : "+modelPesanan.getNo_pesanan());

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        holder.totalBelanja.setText("Total Belanja : "+kursIndonesia.format(Integer.valueOf(modelPesanan.getTotal_pembayaran())));
        holder.tanggalOrderan.setText(modelPesanan.getCreated_date());

        if(modelPesanan.getNama_status_pesanan().equals("Menunggu Pembayaran")){
            holder.bottomLayoutSatu.setVisibility(View.GONE);
            holder.bottomLayoutDua.setVisibility(View.VISIBLE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_kuning);
            holder.statusPesanan.setText(modelPesanan.getNama_status_pesanan());
        }else if(modelPesanan.getNama_status_pesanan().equals("Pesanan di Proses")){
            holder.bottomLayoutSatu.setVisibility(View.VISIBLE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_ungu);
            holder.statusPesanan.setText("Di Proses");
        }else if(modelPesanan.getNama_status_pesanan().equals("Pesanan di Kirim")){
            holder.bottomLayoutSatu.setVisibility(View.VISIBLE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_orange);
            holder.statusPesanan.setText("Di Kirim");
        }else if(modelPesanan.getNama_status_pesanan().equals("Tiba di Tujuan")){
            holder.bottomLayoutSatu.setVisibility(View.VISIBLE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_hijau_muda);
            holder.statusPesanan.setText("Tiba");
        }else if(modelPesanan.getNama_status_pesanan().equals("Pesanan Selesai")){
            holder.bottomLayoutSatu.setVisibility(View.VISIBLE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_hijau);
            holder.statusPesanan.setText("Selesai");
        }else{
            holder.bottomLayoutSatu.setVisibility(View.VISIBLE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_merah);
            holder.statusPesanan.setText("Batal");
        }

        holder.lakukanPembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToKonfirmasiPembayaran = new Intent(view.getContext(), KonfirmasiPembayaran.class);
                goToKonfirmasiPembayaran.putExtra("id_pesanan", listPesanan.get(positionChecked).getId_pesanan());
                goToKonfirmasiPembayaran.putExtra("nomor_rekening", listPesanan.get(positionChecked).getNo_rekening_pembayaran());
                goToKonfirmasiPembayaran.putExtra("nama_bank", listPesanan.get(positionChecked).getNama_bank_rekening_pembayaran());
                goToKonfirmasiPembayaran.putExtra("atas_nama", listPesanan.get(positionChecked).getAtas_nama_rekening_pembayaran());
                goToKonfirmasiPembayaran.putExtra("total_pembayaran", listPesanan.get(positionChecked).getTotal_pembayaran());
                goToKonfirmasiPembayaran.putExtra("no_pesanan", listPesanan.get(positionChecked).getNo_pesanan());
                view.getContext().startActivity(goToKonfirmasiPembayaran);
            }
        });

        holder.lihatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailPesanan = new Intent(view.getContext(), DetailPesanan.class);
                goToDetailPesanan.putExtra("id_pesanan", listPesanan.get(positionChecked).getId_pesanan());
                goToDetailPesanan.putExtra("listIdKeranjang", listPesanan.get(positionChecked).getList_id_keranjang());
                goToDetailPesanan.putExtra("hargaOngkir", listPesanan.get(positionChecked).getHarga_ongkir());
                goToDetailPesanan.putExtra("hargaPesananTotal", listPesanan.get(positionChecked).getHarga_pesanan());
                goToDetailPesanan.putExtra("judulAlamatChecked", listPesanan.get(positionChecked).getJudul_alamat());
                goToDetailPesanan.putExtra("namaPenerimaChecked", listPesanan.get(positionChecked).getNama_penerima());
                goToDetailPesanan.putExtra("nohpPenerimaChecked", listPesanan.get(positionChecked).getNohp_penerima());
                goToDetailPesanan.putExtra("alamatPenerimaChecked", listPesanan.get(positionChecked).getAlamat());
                goToDetailPesanan.putExtra("statusPesanan", listPesanan.get(positionChecked).getNama_status_pesanan());
                view.getContext().startActivity(goToDetailPesanan);
            }
        });

        holder.bottomLayoutSatu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailPesanan = new Intent(view.getContext(), DetailPesanan.class);
                goToDetailPesanan.putExtra("id_pesanan", listPesanan.get(positionChecked).getId_pesanan());
                goToDetailPesanan.putExtra("listIdKeranjang", listPesanan.get(positionChecked).getList_id_keranjang());
                goToDetailPesanan.putExtra("hargaOngkir", listPesanan.get(positionChecked).getHarga_ongkir());
                goToDetailPesanan.putExtra("hargaPesananTotal", listPesanan.get(positionChecked).getHarga_pesanan());
                goToDetailPesanan.putExtra("judulAlamatChecked", listPesanan.get(positionChecked).getJudul_alamat());
                goToDetailPesanan.putExtra("namaPenerimaChecked", listPesanan.get(positionChecked).getNama_penerima());
                goToDetailPesanan.putExtra("nohpPenerimaChecked", listPesanan.get(positionChecked).getNohp_penerima());
                goToDetailPesanan.putExtra("alamatPenerimaChecked", listPesanan.get(positionChecked).getAlamat());
                goToDetailPesanan.putExtra("statusPesanan", listPesanan.get(positionChecked).getNama_status_pesanan());
                view.getContext().startActivity(goToDetailPesanan);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listPesanan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nomorOrderan, totalBelanja, tanggalOrderan, statusPesanan;
        public LinearLayout tagLayout, bottomLayoutSatu, bottomLayoutDua, lakukanPembayaran, lihatDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            nomorOrderan = itemView.findViewById(R.id.nomorOrderan);
            totalBelanja = itemView.findViewById(R.id.totalBelanja);
            tanggalOrderan = itemView.findViewById(R.id.tanggalOrderan);
            statusPesanan = itemView.findViewById(R.id.statusPesanan);
            tagLayout = itemView.findViewById(R.id.tagLayout);
            bottomLayoutSatu = itemView.findViewById(R.id.bottomLayoutSatu);
            bottomLayoutDua = itemView.findViewById(R.id.bottomLayoutDua);
            lakukanPembayaran = itemView.findViewById(R.id.lakukanPembayaran);
            lihatDetail = itemView.findViewById(R.id.lihatDetail);
        }
    }
}

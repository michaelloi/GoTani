package com.urunggundrup.gotani.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.urunggundrup.gotani.DetailPesanan;
import com.urunggundrup.gotani.DetailPesananToko;
import com.urunggundrup.gotani.KonfirmasiPembayaran;
import com.urunggundrup.gotani.MainActivity;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelPesanan;
import com.urunggundrup.gotani.model.ModelPesananToko;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterPesananToko extends RecyclerView.Adapter<AdapterPesananToko.ViewHolder> {
    Context context;
    private List<ModelPesananToko> listPesananToko;

    public AdapterPesananToko(Context context, List<ModelPesananToko> listPesananToko) {
        this.context = context;
        this.listPesananToko = listPesananToko;
    }

    @Override
    public AdapterPesananToko.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_pesanan_toko_item, parent, false);
        AdapterPesananToko.ViewHolder holder = new AdapterPesananToko.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AdapterPesananToko.ViewHolder holder, final int position) {
        final ModelPesananToko modelPesananToko = listPesananToko.get(position);
        int positionChecked = position;

        holder.nomorOrderan.setText("No.Orderan : "+modelPesananToko.getNo_pesanan());

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.JAPAN);
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);

        holder.totalBelanja.setText("Total Belanja : "+kursIndonesia.format(Integer.valueOf(modelPesananToko.getTotal_pembayaran())));
        holder.tanggalOrderan.setText(modelPesananToko.getCreated_date());

        if(modelPesananToko.getNama_status_pesanan().equals("Menunggu Pembayaran")){
            holder.bottomLayoutSatu.setVisibility(View.VISIBLE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.bottomLayoutTiga.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_kuning);
            holder.statusPesanan.setText(modelPesananToko.getNama_status_pesanan());
        }else if(modelPesananToko.getNama_status_pesanan().equals("Pesanan di Proses")){
            holder.bottomLayoutSatu.setVisibility(View.GONE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.bottomLayoutTiga.setVisibility(View.VISIBLE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_ungu);
            holder.statusPesanan.setText("Di Proses");
        }else if(modelPesananToko.getNama_status_pesanan().equals("Pesanan di Kirim")){
            holder.bottomLayoutSatu.setVisibility(View.GONE);
            holder.bottomLayoutDua.setVisibility(View.VISIBLE);
            holder.bottomLayoutTiga.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.GONE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_orange);
            holder.statusPesanan.setText("Di Kirim");
        }else if(modelPesananToko.getNama_status_pesanan().equals("Tiba di Tujuan")){
            holder.bottomLayoutSatu.setVisibility(View.VISIBLE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.bottomLayoutTiga.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_hijau_muda);
            holder.statusPesanan.setText("Tiba");
        }else if(modelPesananToko.getNama_status_pesanan().equals("Pesanan Selesai")){
            holder.bottomLayoutSatu.setVisibility(View.VISIBLE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.bottomLayoutTiga.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_hijau);
            holder.statusPesanan.setText("Selesai");
        }else{
            holder.bottomLayoutSatu.setVisibility(View.VISIBLE);
            holder.bottomLayoutDua.setVisibility(View.GONE);
            holder.bottomLayoutTiga.setVisibility(View.GONE);
            holder.tagLayout.setVisibility(View.VISIBLE);
            holder.tagLayout.setBackgroundResource(R.drawable.rounded_merah);
            holder.statusPesanan.setText("Batal");
        }

        holder.batalkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(holder.urlAccess)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Register_Api api = retrofit.create(Register_Api.class);
                Call<Model> call = api.selesaikanPesanan(modelPesananToko.getId_pesanan(), "6");
                call.enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {
                        if(response.body().getValue().equalsIgnoreCase("1")){
                            listPesananToko.remove(positionChecked);
                            Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            notifyItemChanged(positionChecked);
                            notifyItemRangeChanged(positionChecked, listPesananToko.size());
                        }else{
                            Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

        holder.lihatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailPesanan = new Intent(view.getContext(), DetailPesananToko.class);
                goToDetailPesanan.putExtra("id_pesanan", listPesananToko.get(positionChecked).getId_pesanan());
                goToDetailPesanan.putExtra("listIdKeranjang", listPesananToko.get(positionChecked).getList_id_keranjang());
                goToDetailPesanan.putExtra("hargaOngkir", listPesananToko.get(positionChecked).getHarga_ongkir());
                goToDetailPesanan.putExtra("hargaPesananTotal", listPesananToko.get(positionChecked).getHarga_pesanan());
                goToDetailPesanan.putExtra("judulAlamatChecked", listPesananToko.get(positionChecked).getJudul_alamat());
                goToDetailPesanan.putExtra("namaPenerimaChecked", listPesananToko.get(positionChecked).getNama_penerima());
                goToDetailPesanan.putExtra("nohpPenerimaChecked", listPesananToko.get(positionChecked).getNohp_penerima());
                goToDetailPesanan.putExtra("alamatPenerimaChecked", listPesananToko.get(positionChecked).getAlamat());
                goToDetailPesanan.putExtra("statusPesanan", listPesananToko.get(positionChecked).getNama_status_pesanan());
                goToDetailPesanan.putExtra("fotoBuktiPembayaran", listPesananToko.get(positionChecked).getFoto_bukti_pembayaran());
                view.getContext().startActivity(goToDetailPesanan);
            }
        });

        holder.lihatDetailDua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailPesanan = new Intent(view.getContext(), DetailPesananToko.class);
                goToDetailPesanan.putExtra("id_pesanan", listPesananToko.get(positionChecked).getId_pesanan());
                goToDetailPesanan.putExtra("listIdKeranjang", listPesananToko.get(positionChecked).getList_id_keranjang());
                goToDetailPesanan.putExtra("hargaOngkir", listPesananToko.get(positionChecked).getHarga_ongkir());
                goToDetailPesanan.putExtra("hargaPesananTotal", listPesananToko.get(positionChecked).getHarga_pesanan());
                goToDetailPesanan.putExtra("judulAlamatChecked", listPesananToko.get(positionChecked).getJudul_alamat());
                goToDetailPesanan.putExtra("namaPenerimaChecked", listPesananToko.get(positionChecked).getNama_penerima());
                goToDetailPesanan.putExtra("nohpPenerimaChecked", listPesananToko.get(positionChecked).getNohp_penerima());
                goToDetailPesanan.putExtra("alamatPenerimaChecked", listPesananToko.get(positionChecked).getAlamat());
                goToDetailPesanan.putExtra("statusPesanan", listPesananToko.get(positionChecked).getNama_status_pesanan());
                goToDetailPesanan.putExtra("fotoBuktiPembayaran", listPesananToko.get(positionChecked).getFoto_bukti_pembayaran());
                view.getContext().startActivity(goToDetailPesanan);
            }
        });

        holder.bottomLayoutSatu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetailPesanan = new Intent(view.getContext(), DetailPesananToko.class);
                goToDetailPesanan.putExtra("id_pesanan", listPesananToko.get(positionChecked).getId_pesanan());
                goToDetailPesanan.putExtra("listIdKeranjang", listPesananToko.get(positionChecked).getList_id_keranjang());
                goToDetailPesanan.putExtra("hargaOngkir", listPesananToko.get(positionChecked).getHarga_ongkir());
                goToDetailPesanan.putExtra("hargaPesananTotal", listPesananToko.get(positionChecked).getHarga_pesanan());
                goToDetailPesanan.putExtra("judulAlamatChecked", listPesananToko.get(positionChecked).getJudul_alamat());
                goToDetailPesanan.putExtra("namaPenerimaChecked", listPesananToko.get(positionChecked).getNama_penerima());
                goToDetailPesanan.putExtra("nohpPenerimaChecked", listPesananToko.get(positionChecked).getNohp_penerima());
                goToDetailPesanan.putExtra("alamatPenerimaChecked", listPesananToko.get(positionChecked).getAlamat());
                goToDetailPesanan.putExtra("statusPesanan", listPesananToko.get(positionChecked).getNama_status_pesanan());
                goToDetailPesanan.putExtra("fotoBuktiPembayaran", listPesananToko.get(positionChecked).getFoto_bukti_pembayaran());
                view.getContext().startActivity(goToDetailPesanan);
            }
        });

        holder.proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(holder.urlAccess)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Register_Api api = retrofit.create(Register_Api.class);
                Call<Model> call = api.selesaikanPesanan(modelPesananToko.getId_pesanan(), "3");
                call.enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {
                        if(response.body().getValue().equalsIgnoreCase("1")){
                            listPesananToko.remove(positionChecked);
                            Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            notifyItemChanged(positionChecked);
                            notifyItemRangeChanged(positionChecked, listPesananToko.size());
                        }else{
                            Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

        holder.tibaDitujuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(holder.urlAccess)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Register_Api api = retrofit.create(Register_Api.class);
                Call<Model> call = api.selesaikanPesanan(modelPesananToko.getId_pesanan(), "4");
                call.enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {
                        if(response.body().getValue().equalsIgnoreCase("1")){
                            listPesananToko.remove(positionChecked);
                            Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            notifyItemChanged(positionChecked);
                            notifyItemRangeChanged(positionChecked, listPesananToko.size());
                        }else{
                            Toast.makeText(view.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
        return listPesananToko.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nomorOrderan, totalBelanja, tanggalOrderan, statusPesanan;
        public LinearLayout tagLayout, bottomLayoutSatu, bottomLayoutDua, batalkan, lihatDetail, proses, bottomLayoutTiga, lihatDetailDua, tibaDitujuan;
        public String urlFoto, urlAccess;

        public ViewHolder(View itemView) {
            super(itemView);
            nomorOrderan = itemView.findViewById(R.id.nomorOrderan);
            totalBelanja = itemView.findViewById(R.id.totalBelanja);
            tanggalOrderan = itemView.findViewById(R.id.tanggalOrderan);
            statusPesanan = itemView.findViewById(R.id.statusPesanan);
            tagLayout = itemView.findViewById(R.id.tagLayout);
            bottomLayoutSatu = itemView.findViewById(R.id.bottomLayoutSatu);
            bottomLayoutDua = itemView.findViewById(R.id.bottomLayoutDua);
            bottomLayoutTiga = itemView.findViewById(R.id.bottomLayoutTiga);
            lihatDetailDua = itemView.findViewById(R.id.lihatDetailDua);
            tibaDitujuan = itemView.findViewById(R.id.tibaDitujuan);
            batalkan = itemView.findViewById(R.id.batalkan);
            proses = itemView.findViewById(R.id.proses);
            lihatDetail = itemView.findViewById(R.id.lihatDetail);
            urlFoto = itemView.getResources().getString(R.string.urlaccesdocuments);
            urlAccess = itemView.getResources().getString(R.string.urlacces);
        }
    }
}

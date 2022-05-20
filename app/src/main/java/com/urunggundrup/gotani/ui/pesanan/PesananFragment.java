package com.urunggundrup.gotani.ui.pesanan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.SessionManager;
import com.urunggundrup.gotani.adapter.AdapterNotifikasi;
import com.urunggundrup.gotani.adapter.AdapterPesanan;
import com.urunggundrup.gotani.adapter.AdapterSpinnerLokasi;
import com.urunggundrup.gotani.adapter.AdapterSpinnerStatusPesanan;
import com.urunggundrup.gotani.databinding.FragmentPesananBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelLokasi;
import com.urunggundrup.gotani.model.ModelPesanan;
import com.urunggundrup.gotani.model.ModelStatusPesanan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PesananFragment extends Fragment {

    private FragmentPesananBinding binding;
    AdapterSpinnerStatusPesanan adapterSpinnerStatusPesanan;
    String sStatusPesanan, sIdStatusPesanan = "0", sId;
    List<ModelStatusPesanan> listStatusPesanan = new ArrayList<>();
    List<String> listNamaStatusPesanan = new ArrayList<>();
    List<ModelPesanan> listPesanan = new ArrayList<>();
    AdapterPesanan adapterPesanan;
    SessionManager sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPesananBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            //Spinner Status Pesanan
            loadListStatusPesanan("");
            binding.pesananStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(binding.pesananStatus.getSelectedItemPosition()!=0){
                        sStatusPesanan = listStatusPesanan.get(binding.pesananStatus.getSelectedItemPosition()-1).getNama_status_pesanan();
                        sIdStatusPesanan = listStatusPesanan.get(binding.pesananStatus.getSelectedItemPosition()-1).getId_status_pesanan();
                        loadListPesanan(sId, sIdStatusPesanan);
                    }else{
                        sIdStatusPesanan="0";
                        loadListPesanan(sId,sIdStatusPesanan);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    sStatusPesanan = "";
                    sIdStatusPesanan = "0";
                    Toast.makeText(getActivity(), "ini dari nothing "+sIdStatusPesanan, Toast.LENGTH_SHORT).show();
                }
            });

            //membaca session aplikasi
            sessionManager = new SessionManager(getActivity().getApplicationContext());
            if(sessionManager.isLoggedIn()){
                HashMap<String, String> user = sessionManager.getUserDetails();
                sId = user.get(SessionManager.USER_ID);
            }else{
                sId="";
            }

            //request data list pesanan
            loadListPesanan(sId, sIdStatusPesanan);

            //swipe refresh action
            binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadListPesanan(sId, sIdStatusPesanan);
                    binding.swipeRefresh.setRefreshing(false);
                }
            });

            //set data list alamat to recyclerview
            adapterPesanan = new AdapterPesanan(getActivity(), listPesanan);
            RecyclerView.LayoutManager pesananLayout = new GridLayoutManager(getActivity().getApplicationContext(), 1);
            binding.recyclerPesanan.setLayoutManager(pesananLayout);
            binding.recyclerPesanan.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerPesanan.setAdapter(adapterPesanan);
        }catch (Exception e){
            System.out.println(e);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadListStatusPesanan(String s) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getActivity().getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listStatusPesanan(s);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    listStatusPesanan = response.body().getList_status_pesanan();

                    listNamaStatusPesanan.add("Semua Status Pesanan");

                    for(int i=0;i<listStatusPesanan.size();i++){
                        listNamaStatusPesanan.add(listStatusPesanan.get(i).getNama_status_pesanan());
                    }

                    adapterSpinnerStatusPesanan = new AdapterSpinnerStatusPesanan(getActivity().getApplicationContext(), listNamaStatusPesanan);
                    binding.pesananStatus.setAdapter(adapterSpinnerStatusPesanan);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getActivity(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadListPesanan(String idUser, String idStatusPesanan) {
        binding.pesanProgress.setVisibility(View.VISIBLE);
        binding.recyclerPesanan.setVisibility(View.GONE);
        binding.pesanMaaf.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listPesanan(idUser, idStatusPesanan);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                if(response.body().getValue().equalsIgnoreCase("1")){
                    binding.pesanProgress.setVisibility(View.GONE);
                    listPesanan = response.body().getList_pesanan();
                    if(listPesanan.size()<1){
                        binding.pesanMaaf.setVisibility(View.VISIBLE);
                        binding.recyclerPesanan.setVisibility(View.GONE);
                    }else{
                        binding.pesanMaaf.setVisibility(View.GONE);
                        binding.recyclerPesanan.setVisibility(View.VISIBLE);
                        adapterPesanan = new AdapterPesanan(getActivity(), listPesanan);
                        binding.recyclerPesanan.setAdapter(adapterPesanan);
                    }
                }else{
                    binding.pesanProgress.setVisibility(View.GONE);
                    binding.recyclerPesanan.setVisibility(View.GONE);
                    binding.pesanMaaf.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                binding.pesanProgress.setVisibility(View.GONE);
                binding.recyclerPesanan.setVisibility(View.GONE);
                binding.pesanMaaf.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
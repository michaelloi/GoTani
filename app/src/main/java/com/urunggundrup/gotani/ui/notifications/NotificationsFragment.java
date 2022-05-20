package com.urunggundrup.gotani.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.urunggundrup.gotani.AturAlamatPengiriman;
import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.SessionManager;
import com.urunggundrup.gotani.adapter.AdapterAlamatUser;
import com.urunggundrup.gotani.adapter.AdapterNotifikasi;
import com.urunggundrup.gotani.databinding.FragmentNotificationsBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelNotifikasi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    SessionManager sessionManager;
    String sId="";
    AdapterNotifikasi adapterNotifikasi;
    List<ModelNotifikasi> listNotifikasi = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try{
            //membaca session aplikasi
            sessionManager = new SessionManager(getActivity().getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            sId = user.get(SessionManager.USER_ID);

            //request data list notifikasi
            loadListNotifikasi(sId, "Y");

            //swipe refresh action
            binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadListNotifikasi(sId, "Y");
                    binding.swipeRefresh.setRefreshing(false);
                }
            });

            //set data list alamat to recyclerview
            adapterNotifikasi = new AdapterNotifikasi(getActivity(), listNotifikasi);
            RecyclerView.LayoutManager notifikasiLayout = new GridLayoutManager(getActivity().getApplicationContext(), 1);
            binding.recyclerNotifikasi.setLayoutManager(notifikasiLayout);
            binding.recyclerNotifikasi.setItemAnimator(new DefaultItemAnimator());
            binding.recyclerNotifikasi.setAdapter(adapterNotifikasi);
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

    private void loadListNotifikasi(String idUser, String broadcast) {
        binding.pesanProgress.setVisibility(View.VISIBLE);
        binding.recyclerNotifikasi.setVisibility(View.GONE);
        binding.pesanMaaf.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.urlacces))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Register_Api api = retrofit.create(Register_Api.class);
        Call<Model> call = api.listNotifikasi(idUser,broadcast);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if(response.body().getValue().equalsIgnoreCase("1")){
                    binding.pesanProgress.setVisibility(View.GONE);
                    listNotifikasi = response.body().getList_notifikasi();
                    if(listNotifikasi.size()<1){
                        binding.pesanMaaf.setVisibility(View.VISIBLE);
                        binding.recyclerNotifikasi.setVisibility(View.GONE);
                    }else{
                        binding.pesanMaaf.setVisibility(View.GONE);
                        binding.recyclerNotifikasi.setVisibility(View.VISIBLE);
                        adapterNotifikasi = new AdapterNotifikasi(getActivity(), listNotifikasi);
                        binding.recyclerNotifikasi.setAdapter(adapterNotifikasi);
                    }
                }else{
                    binding.pesanProgress.setVisibility(View.GONE);
                    binding.recyclerNotifikasi.setVisibility(View.GONE);
                    binding.pesanMaaf.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                binding.pesanProgress.setVisibility(View.GONE);
                binding.recyclerNotifikasi.setVisibility(View.GONE);
                binding.pesanMaaf.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
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

import com.urunggundrup.gotani.R;
import com.urunggundrup.gotani.Register_Api;
import com.urunggundrup.gotani.adapter.AdapterSpinnerLokasi;
import com.urunggundrup.gotani.adapter.AdapterSpinnerStatusPesanan;
import com.urunggundrup.gotani.databinding.FragmentPesananBinding;
import com.urunggundrup.gotani.model.Model;
import com.urunggundrup.gotani.model.ModelLokasi;
import com.urunggundrup.gotani.model.ModelStatusPesanan;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PesananFragment extends Fragment {

    private FragmentPesananBinding binding;
    AdapterSpinnerStatusPesanan adapterSpinnerStatusPesanan;
    Spinner spinnerStatusPesanan;
    String sStatusPesanan;
    List<ModelStatusPesanan> listStatusPesanan = new ArrayList<>();
    List<String> listNamaStatusPesanan = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPesananBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        spinnerStatusPesanan = binding.pesananStatus;

        //Spinner Status Pesanan
        loadListStatusPesanan("");
        spinnerStatusPesanan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerStatusPesanan.getSelectedItemPosition()!=0){
                    sStatusPesanan = listStatusPesanan.get(spinnerStatusPesanan.getSelectedItemPosition()-1).getNama_status_pesanan();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sStatusPesanan = "";
            }
        });

        return root;
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
                    spinnerStatusPesanan.setAdapter(adapterSpinnerStatusPesanan);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getActivity(), "Maaf, terjadi kesalahan dalam aplikasi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
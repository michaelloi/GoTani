package com.urunggundrup.gotani;

import com.urunggundrup.gotani.model.ModelKeranjang;

import java.util.List;

public interface KeranjangListListener {
    public void getListChange(List<ModelKeranjang> listKeranjangCheckout);
    public void getJumlahToko(List<ModelKeranjang> listKeranjangCheckout);
}

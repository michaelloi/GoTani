package com.urunggundrup.gotani.ui.pesanan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PesananViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PesananViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is pesanan fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
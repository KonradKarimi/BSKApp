package com.konradkarimi.bskapp.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> mPublicKeyText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Here is your text to encrypt! 🔐");
        mPublicKeyText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getPublicKeyText() {
        return mPublicKeyText;
    }

    public void setText(CharSequence s) {
        this.mText.setValue(s.toString());
    }

    public void setPublicKeyText(CharSequence s) {
        this.mPublicKeyText.setValue(s.toString());
    }
}
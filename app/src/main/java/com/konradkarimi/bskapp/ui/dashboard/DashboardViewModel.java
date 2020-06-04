package com.konradkarimi.bskapp.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Here is your text to encrypt! 🔐");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(CharSequence s) {
        this.mText.setValue(s.toString());
    }
}
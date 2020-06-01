package com.konradkarimi.bskapp.ui.home;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.InverseMethod;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.konradkarimi.bskapp.BR;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Here is your text to encrypt! 🔐");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void setText(CharSequence s) {
        this.mText.setValue(s.toString());
    }

    public void readFile() {
        System.out.println(mText.getValue());
    }


}
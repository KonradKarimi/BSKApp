package com.konradkarimi.bskapp.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
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

import java.io.IOException;
import java.io.InputStream;

public class HomeViewModel extends ViewModel {


    private MutableLiveData<String> mText;
    private MutableLiveData<Uri> mUri;

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

    public LiveData<Uri> getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        this.mUri.setValue(uri);
    }

    public void readFile() {
        System.out.println(mText.getValue());
    }


}
package com.konradkarimi.bskapp.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.konradkarimi.bskapp.R;
import com.konradkarimi.bskapp.databinding.FragmentHomeBinding;
import com.konradkarimi.bskapp.utils.AESUtils;
import com.konradkarimi.bskapp.utils.FileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final int PICK_TXT_FILE = 1;


    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private AESUtils aesUtils = new AESUtils();
    private FileHandler fileHandler = new FileHandler(this);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.setViewmodel(homeViewModel);
        binding.setLifecycleOwner(this);
        View view = binding.getRoot();


        binding.readFBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileHandler.openFile(Uri.parse("download"));
            }
        });

        binding.encryptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, byte[]> encryptedData = aesUtils.encryptText(homeViewModel.getText().getValue());
                homeViewModel.setEncryptedData(encryptedData);
                String encryptedMSG = new String(encryptedData.get("CipherText"), StandardCharsets.UTF_8);
                homeViewModel.setText(encryptedMSG);
                fileHandler.sendFile(homeViewModel.getText().getValue());
            }
        });

        binding.decryptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String decryptedText = aesUtils.decrypt(homeViewModel.getEncryptedData().getValue().get("IV"), homeViewModel.getEncryptedData().getValue().get("CipherText"));
                homeViewModel.setText(decryptedText);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_TXT_FILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                try {
                    String fileText = fileHandler.readTextFromUri(uri);
                    homeViewModel.setText(fileText);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

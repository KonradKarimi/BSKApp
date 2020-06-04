package com.konradkarimi.bskapp.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.konradkarimi.bskapp.R;
import com.konradkarimi.bskapp.databinding.FragmentDashboardBinding;
import com.konradkarimi.bskapp.databinding.FragmentHomeBinding;
import com.konradkarimi.bskapp.utils.AESUtils;
import com.konradkarimi.bskapp.utils.FileHandler;
import com.konradkarimi.bskapp.utils.RSAUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

public class DashboardFragment extends Fragment {

    private static final int PICK_TXT_FILE = 1;


    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private FileHandler fileHandler = new FileHandler(this);
    private RSAUtils rsaUtils = new RSAUtils();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        binding.setViewmodel(dashboardViewModel);
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
                PublicKey publicKey = rsaUtils.generateKeyPair().getPublic();
                String encryptedData = rsaUtils.encryptData(dashboardViewModel.getText().getValue(), publicKey);
                dashboardViewModel.setText(encryptedData);
                fileHandler.sendFile(dashboardViewModel.getText().getValue());
            }
        });

        binding.decryptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivateKey privateKey = rsaUtils.getKeyPair().getPrivate();
                String decryptedData = rsaUtils.decryptData(dashboardViewModel.getText().getValue(), privateKey);
                dashboardViewModel.setText(decryptedData);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    dashboardViewModel.setText(fileText);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

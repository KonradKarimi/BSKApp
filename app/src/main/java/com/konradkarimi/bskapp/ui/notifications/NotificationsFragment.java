package com.konradkarimi.bskapp.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.konradkarimi.bskapp.BuildConfig;
import com.konradkarimi.bskapp.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class NotificationsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Element versionElement = new Element();
        versionElement.setTitle("Version " + BuildConfig.VERSION_NAME);

        return new AboutPage(getContext())
                .addItem(versionElement)
                .addGroup("Contact me:")
                .addEmail("konradkarimi@gmail.com")
                .addGitHub("KonradKarimi")
                .setDescription("This is an app prepared for 'Security of computer systems' lecture in UKW in Bydgoszcz.")
                .create();
    }
}

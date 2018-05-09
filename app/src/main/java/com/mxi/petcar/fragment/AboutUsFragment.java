package com.mxi.petcar.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mxi.petcar.R;
import com.mxi.petcar.comman.CommanClass;

/**
 * Created by mxi on 20/11/17.
 */

public class AboutUsFragment extends Fragment {

    WebView home_web;

    ProgressBar pb;
    ImageView iv_no_network;

    Bundle bundleUrl;

    String Url;

    String android_id;

    CommanClass cc;

    private SharedPreferences mPreferences;

    String token="";




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_us_fragment, container, false);

        cc = new CommanClass(getActivity());

        init(rootView);

        android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        return rootView;

    }

    private void init(View rootView) {

        iv_no_network = (ImageView) rootView.findViewById(R.id.iv_no_network);




    }

}


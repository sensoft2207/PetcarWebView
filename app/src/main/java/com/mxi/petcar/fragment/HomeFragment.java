package com.mxi.petcar.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mxi.petcar.R;
import com.mxi.petcar.comman.CheckNetworkConnection;
import com.mxi.petcar.comman.CommanClass;

import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    WebView home_web;

    ProgressBar pb;
    ImageView iv_no_network;

    Bundle bundleUrl;

    String Url;

    String android_id;

    CommanClass cc;

    private SharedPreferences mPreferences;

    String token="";

    String mainCookie;

    Map<String, String> abc,abcd;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        cc = new CommanClass(getActivity());

        init(rootView);

        android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        return rootView;

    }

    private void init(View rootView) {

        home_web = (WebView) rootView.findViewById(R.id.home_web);
        pb = (ProgressBar) rootView.findViewById(R.id.pb);
        iv_no_network = (ImageView) rootView.findViewById(R.id.iv_no_network);

        abc = new HashMap<String, String>();

        abc.put("android", "android");

       /* cc.savePrefString("CookieStore", String.valueOf(abc));


        mainCookie = cc.loadPrefString("CookieStore");

        Log.e("CookieStore",mainCookie);
*/
        bundleUrl = this.getArguments();



        if (bundleUrl != null) {
            Url = bundleUrl.getString("URL");
            startWebView(Url,abc);

            Log.e("URL",Url);
        }else {
            startWebView("http://mbdbtechnology.com/projects/petcare/",abc);
        }

    }

    private void startWebView(String url, final Map<String, String> mainCookie) {

        if(CheckNetworkConnection.isInternetAvailable(getActivity()))
        {


            home_web.getSettings().setJavaScriptEnabled(true);

           /* home_web.getSettings().setAppCacheEnabled(true);
            // settings.setBuiltInZoomControls(true);
            home_web.getSettings().setPluginState(WebSettings.PluginState.ON);
            home_web.getSettings().setJavaScriptEnabled(true);
            home_web.getSettings().setBuiltInZoomControls(false);
            home_web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);*/
            home_web.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    pb.setProgress(progress);
                    if (progress == 100) {
                        pb.setVisibility(View.GONE);
                        //iv_no_network.setVisibility(View.GONE);

                    } else {
                        pb.setVisibility(View.VISIBLE);
                        //iv_no_network.setVisibility(View.GONE);
                    }
                }
            });
            home_web.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                    home_web.loadData("<center><b></b></center>", "text/html", null);
                    iv_no_network.setVisibility(View.VISIBLE);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    view.loadUrl(url,mainCookie);
                    //view.loadUrl(url);

                    return false;
                }
            });

            home_web.setOnKeyListener(new View.OnKeyListener(){

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == MotionEvent.ACTION_UP
                            && home_web.canGoBack()) {
                        handler.sendEmptyMessage(1);
                        return true;
                    }

                    return false;
                }

            });



            home_web.loadUrl(url,mainCookie);
           // home_web.loadUrl(url);

        }
        else{

            iv_no_network.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }

    }

    private void webViewGoBack() {

        home_web.goBack();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}

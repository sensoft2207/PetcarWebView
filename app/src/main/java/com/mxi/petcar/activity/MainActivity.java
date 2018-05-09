package com.mxi.petcar.activity;

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mxi.petcar.R;
import com.mxi.petcar.comman.CommanClass;
import com.mxi.petcar.comman.ConnectionUrl;
import com.mxi.petcar.fragment.AboutUsFragment;
import com.mxi.petcar.fragment.EditProfileFragment;
import com.mxi.petcar.fragment.HomeFragment;
import com.mxi.petcar.fragment.SignupFragment;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private SlidingRootNav slidingRootNav;

    LinearLayout ln_home,ln_profile,ln_about_us,ln_happy_village,ln_village_story,ln_village_assis,ln_add_pet,ln_feedback,ln_logout,ln_signup;

    String title = "";

    Bundle bundleUrl;

    TextView tv_login;

    CommanClass cc;

    ProgressDialog pDialog;
    String status_code_signup = "";
    String status_message_signup = "";

    Dialog dialog;

    String android_id;

    String json_str = "";

    Map<String, String> abc;

    boolean doubleBackToExitPressedOnce = false;


   // abc.put("cookie", "android");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        cc = new CommanClass(MainActivity.this);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_drawer)
                .inject();



        init();
    }

    private void init() {

        abc = new HashMap<String, String>();

        abc.put("cookie", "android");

        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String fragmentSignup = getIntent().getStringExtra("EXTRA");

        ln_home = (LinearLayout)findViewById(R.id.ln_home);
        ln_profile = (LinearLayout)findViewById(R.id.ln_profile);
        ln_about_us = (LinearLayout)findViewById(R.id.ln_about_us);
        ln_happy_village = (LinearLayout)findViewById(R.id.ln_happy_village);
        ln_village_story = (LinearLayout)findViewById(R.id.ln_village_story);
        ln_village_assis = (LinearLayout)findViewById(R.id.ln_village_assis);
        ln_add_pet = (LinearLayout)findViewById(R.id.ln_add_pet);
        ln_logout = (LinearLayout)findViewById(R.id.ln_logout);
        ln_feedback = (LinearLayout)findViewById(R.id.ln_feedback);
        ln_signup = (LinearLayout)findViewById(R.id.ln_signup);
        tv_login = (TextView) findViewById(R.id.tv_login);

        ln_home.setOnClickListener(this);
        ln_about_us.setOnClickListener(this);
        ln_happy_village.setOnClickListener(this);
        ln_village_story.setOnClickListener(this);
        ln_village_assis.setOnClickListener(this);
        ln_add_pet.setOnClickListener(this);
        ln_feedback.setOnClickListener(this);
        ln_logout.setOnClickListener(this);
        ln_signup.setOnClickListener(this);
        tv_login.setOnClickListener(this);

        if (cc.loadPrefBoolean("isLogin")){

            ln_profile.setOnClickListener(this);

            ln_logout.setVisibility(View.VISIBLE);
            ln_signup.setVisibility(View.INVISIBLE);
            tv_login.setText(cc.loadPrefString("firstname"));
            tv_login.setClickable(false);


        }else {


            Log.e("LoginMainData","Not Login");
        }

        if (fragmentSignup.equals("openFragment")){

            android.app.FragmentTransaction   tra = getFragmentManager().beginTransaction();
            Fragment newFragment = new SignupFragment();
            tra.replace(R.id.container, newFragment);
            title = "Sign Up";
            getSupportActionBar().setTitle(title);
            tra.commit();

        }else {

            firstTimeLoadHome();

        }
    }

    private void firstTimeLoadHome() {

        android.app.FragmentTransaction   tra = getFragmentManager().beginTransaction();
        Fragment newFragment = new HomeFragment();
        tra.replace(R.id.container, newFragment);
        title = "Home";
        getSupportActionBar().setTitle(title);
        tra.commit();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ln_signup:

                slidingRootNav.closeMenu();

                android.app.FragmentTransaction   tra0 = getFragmentManager().beginTransaction();
                Fragment newFragment0 = new SignupFragment();
                tra0.replace(R.id.container, newFragment0);
                title = "Sign Up";
                getSupportActionBar().setTitle(title);
                tra0.commit();

                break;

            case R.id.ln_home:

                slidingRootNav.closeMenu();

                android.app.FragmentTransaction   tra = getFragmentManager().beginTransaction();
                Fragment newFragment = new HomeFragment();
                tra.replace(R.id.container, newFragment);
                title = "HOME";
                bundleUrl = new Bundle();
                bundleUrl.putString("URL", "http://mbdbtechnology.com/projects/petcare/");
                bundleUrl.putString("COOKIES", String.valueOf(abc));
                newFragment.setArguments(bundleUrl);
                getSupportActionBar().setTitle(title);
                tra.commit();

                break;

            case R.id.ln_about_us:

                slidingRootNav.closeMenu();

                android.app.FragmentTransaction   tra2 = getFragmentManager().beginTransaction();
                Fragment newFragment2 = new HomeFragment();
                tra2.replace(R.id.container, newFragment2);
                title = "ABOUT US";
                bundleUrl = new Bundle();
                bundleUrl.putString("URL", "http://mbdbtechnology.com/projects/petcare/about-us/");
                bundleUrl.putString("COOKIES", String.valueOf(abc));
                newFragment2.setArguments(bundleUrl);
                getSupportActionBar().setTitle(title);
                //tra2.addToBackStack(null);
                tra2.commit();

                break;

            case R.id.ln_happy_village:

                slidingRootNav.closeMenu();

                android.app.FragmentTransaction   tra3 = getFragmentManager().beginTransaction();
                Fragment newFragment3 = new HomeFragment();
                tra3.replace(R.id.container, newFragment3);
                title = "HAPPY VILLAGE";
                bundleUrl = new Bundle();
                bundleUrl.putString("URL", "http://mbdbtechnology.com/projects/petcare/happy-village/");
                bundleUrl.putString("COOKIES", String.valueOf(abc));
                newFragment3.setArguments(bundleUrl);
                getSupportActionBar().setTitle(title);
                //tra3.addToBackStack(null);
                tra3.commit();

                break;

            case R.id.ln_village_story:

                slidingRootNav.closeMenu();

                android.app.FragmentTransaction   tra4 = getFragmentManager().beginTransaction();
                Fragment newFragment4 = new HomeFragment();
                tra4.replace(R.id.container, newFragment4);
                title = "VILLAGE STORY";
                bundleUrl = new Bundle();
                bundleUrl.putString("URL", "http://mbdbtechnology.com/projects/petcare/village-story/");
                bundleUrl.putString("COOKIES", String.valueOf(abc));
                newFragment4.setArguments(bundleUrl);
                getSupportActionBar().setTitle(title);
                //tra4.addToBackStack(null);
                tra4.commit();

                break;

            case R.id.ln_village_assis:

                slidingRootNav.closeMenu();

                android.app.FragmentTransaction   tra5 = getFragmentManager().beginTransaction();
                Fragment newFragment5 = new HomeFragment();
                tra5.replace(R.id.container, newFragment5);
                title = "VILLAGE ASSISTANTS";
                bundleUrl = new Bundle();
                bundleUrl.putString("URL", "http://mbdbtechnology.com/projects/petcare/village-assistants/");
                bundleUrl.putString("COOKIES", String.valueOf(abc));
                newFragment5.setArguments(bundleUrl);
                getSupportActionBar().setTitle(title);
                //tra5.addToBackStack(null);
                tra5.commit();

                break;

            case R.id.ln_add_pet:


                if(cc.loadPrefBoolean("isLogin")){

                    slidingRootNav.closeMenu();

                   /* android.app.FragmentTransaction   tra6 = getFragmentManager().beginTransaction();
                    Fragment newFragment6 = new HomeFragment();
                    tra6.replace(R.id.container, newFragment6);
                    title = "ADD PET";
                    bundleUrl = new Bundle();
                    bundleUrl.putString("URL", "http://mbdbtechnology.com/projects/petcare/contact-us/");
                    bundleUrl.putString("COOKIES", String.valueOf(abc));
                    newFragment6.setArguments(bundleUrl);
                    getSupportActionBar().setTitle(title);
                    //tra6.addToBackStack(null);
                    tra6.commit();*/

                    android.app.FragmentTransaction   tra6 = getFragmentManager().beginTransaction();
                    Fragment newFragment6 = new AboutUsFragment();
                    tra6.replace(R.id.container, newFragment6);
                    title = "ADD PET";
                    bundleUrl = new Bundle();
                    bundleUrl.putString("URL", "http://mbdbtechnology.com/projects/petcare/contact-us/");
                    bundleUrl.putString("COOKIES", String.valueOf(abc));
                    newFragment6.setArguments(bundleUrl);
                    getSupportActionBar().setTitle(title);
                    //tra6.addToBackStack(null);
                    tra6.commit();

                }else{

                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }



                break;

            case R.id.ln_feedback:

                slidingRootNav.closeMenu();

                android.app.FragmentTransaction   tra7 = getFragmentManager().beginTransaction();
                Fragment newFragment7 = new HomeFragment();
                tra7.replace(R.id.container, newFragment7);
                title = "FEEDBACK";
                bundleUrl = new Bundle();
                bundleUrl.putString("URL", "http://mbdbtechnology.com/projects/petcare/feedback/");
                newFragment7.setArguments(bundleUrl);
                getSupportActionBar().setTitle(title);
                //tra6.addToBackStack(null);
                tra7.commit();


                break;

            case R.id.ln_logout:

               // slidingRootNav.closeMenu();

                logoutDialog();

                break;

            case R.id.tv_login:

                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);

                break;

            case R.id.ln_profile:

                slidingRootNav.closeMenu();

                android.app.FragmentTransaction   tra8 = getFragmentManager().beginTransaction();
                Fragment newFragment8 = new EditProfileFragment();
                tra8.replace(R.id.container, newFragment8);
                title = "EDIT PROFILE";
                getSupportActionBar().setTitle(title);
                //tra6.addToBackStack(null);
                tra8.commit();

                break;

        }
    }

    private void logoutDialog() {

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);


        TextView tv_dialog_yes = (TextView) dialog.findViewById(R.id.tv_exit_yes);
        TextView tv_dialog_no = (TextView) dialog.findViewById(R.id.tv_exit_no);


        tv_dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {
                    cc.showToast(getString(R.string.no_internet));
                }
                else{
                    new LogoutJsonCall().execute();
                }

            }
        });

        tv_dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();

    }


    public class LogoutJsonCall extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            JSONObject json = new JSONObject();

            try {
                json.put("device_id",android_id);


            } catch (Exception e) {
                e.printStackTrace();
            }
            json_str = json.toString();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {


            OkHttpClient client = getUnsafeOkHttpClient(getApplicationContext());
            String res = "";
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType,json_str);

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(ConnectionUrl.logout)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("petcare-token", cc.loadPrefString("token"))
                    .build();
            Log.e("@Request", request.headers() + "");
            try {
                okhttp3.Response respo = client.newCall(request).execute();

                status_code_signup = respo.code() + "";
                status_message_signup = respo.message() + "";
                res = respo.body().string();
                Log.e("@Response", respo.code() + "");
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("@Response", res);
            return res;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pDialog.dismiss();


            String Message = status_message_signup;
            if (status_code_signup.equals("200")) {
                Message = "Logout Successfully";
                cc.savePrefBoolean("isLogin", false);
                cc.logoutapp();

                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.putExtra("EXTRA","openFragment2");
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
                dialog.dismiss();

            } else if (status_code_signup.equals("406")) {

            } else {
                Message = "Something Went Wrong";
            }
            cc.showToast(Message);

        }

    }

    private static OkHttpClient getUnsafeOkHttpClient(Context context) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };


            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

   /* @Override
    protected void onResume() {



        if (slidingRootNav.isMenuOpened()) {
            adaptee.onDrawerOpened(drawer);

            Log.e("MenuState","open");


        } else {
            adaptee.onDrawerClosed(drawer);
            Log.e("MenuState","close");
        }
        adaptee.onDrawerStateChanged(DrawerLayout.STATE_IDLE);

        super.onResume();
    }*/
}

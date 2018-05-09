package com.mxi.petcar.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mxi.petcar.R;
import com.mxi.petcar.comman.AndyUtils;
import com.mxi.petcar.comman.CommanClass;
import com.mxi.petcar.comman.ConnectionUrl;

import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by mxi on 4/11/17.
 */

public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    EditText ed_login_email,ed_login_password;

    TextView tv_login,tv_signup_link,tv_forgotpass;

    CheckBox ch_remember_me;

    CommanClass cc;

    public boolean checked = false;

    ProgressDialog pDialog;

    String res = "";
    String json_str = "";

    String status_code_signup = "";
    String status_message_signup = "";

    String android_id;

    String email_id_forgot_pass = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        cc = new CommanClass(this);

        init();
    }

    private void init() {

        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ch_remember_me = (CheckBox)findViewById(R.id.ch_remember_me);
        ch_remember_me.setOnCheckedChangeListener(this);

        ed_login_email = (EditText) findViewById(R.id.ed_login_email);
        ed_login_password = (EditText) findViewById(R.id.ed_login_password);

        tv_login = (TextView)findViewById(R.id.tv_login);
        tv_signup_link = (TextView)findViewById(R.id.tv_signup_link);
        tv_forgotpass = (TextView)findViewById(R.id.tv_forgotpass);

        if (cc.loadPrefBoolean2("check") == true){

            ed_login_email.setText(cc.loadPrefString2("email_remember"));
            ed_login_password.setText(cc.loadPrefString2("password_remember"));

            ch_remember_me.setChecked(true);

        }else {

            ch_remember_me.setChecked(false);
        }

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!cc.isConnectingToInternet()) {
                    cc.showToast(getString(R.string.no_internet));

                }else {
                    if (isValidate()) {

                        new OkHttpHandler().execute();

                    }
                }


            }
        });

        tv_signup_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent (LoginActivity.this,ActivityMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("EXTRA","openFragment");
                overridePendingTransition(0,0);
                startActivity(intent);
                finish();
            }
        });

        tv_forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                forgotPassDialog();
            }
        });
    }

    private void forgotPassDialog() {

        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.forgot_pass_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);


        TextView tv_dialog_yes = (TextView) dialog.findViewById(R.id.tv_exit_yes);
        ImageView close_dialog = (ImageView) dialog.findViewById(R.id.close_dialog);
        final EditText ed_forgot_email = (EditText) dialog.findViewById(R.id.ed_forgot_email);


        tv_dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fp_email = ed_forgot_email.getText().toString().trim();

                if (!cc.isConnectingToInternet()) {
                    cc.showToast(getString(R.string.no_internet));
                } else if (fp_email.equals("")) {
                    cc.showToast("Enter Valid Email Id");
                } else {

                    dialog.dismiss();

                    email_id_forgot_pass=fp_email;

                    new CallForgotPassword().execute();

                }

            }
        });

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public boolean isValidate() {
        String msg = null;


        if (!AndyUtils.eMailValidation(ed_login_email.getText().toString())) {
            msg = "Enter Valid Email Id";
            ed_login_email.requestFocus();

        } else if (TextUtils.isEmpty(ed_login_password.getText().toString())) {
            msg = "Enter Valid Password";
            ed_login_password.requestFocus();
        }

        if (msg != null) {
            AndyUtils.showToast(this, msg);
            return false;
        }
        if (msg == null) {
            return true;
        }
        AndyUtils.showToast(this,msg);
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (checked) {
            checked = false;
            ch_remember_me.setChecked(false);
            Log.e("checkbox_false", checked + "");

            cc.logoutapp2();

        } else {
            checked = true;
            ch_remember_me.setChecked(true);

            String email = ed_login_email.getText().toString().trim();
            String password = ed_login_password.getText().toString().trim();

            cc.savePrefString2("email_remember",email);
            cc.savePrefString2("password_remember",password);
            cc.savePrefBoolean2("check",true);


            Log.e("checkbox_true", checked + "");

        }
    }

    public class CallForgotPassword extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            json_str="";
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            JSONObject json = new JSONObject();


            try {
                json.put("email", email_id_forgot_pass);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("JSON could not be made");
            }
            json_str = json.toString();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            OkHttpClient client = getUnsafeOkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json_str);

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(ConnectionUrl.forgot)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try {
                okhttp3.Response response = client.newCall(request).execute();
                status_code_signup=response.code()+"";
                status_message_signup=response.message()+"";
                res = response.body().string();

                Log.e("@@Response", res + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("@@Token", s + "");
            pDialog.dismiss();

           // String message="Reset Password Link is successfully sent on email address";

            if(status_code_signup.equals("200")){

                try {

                    JSONObject jsonObject = new JSONObject(s);

                    String   message = jsonObject.getString("message");
                    String statusLogin = jsonObject.getString("result");

                    if (statusLogin.equals("false")){

                        cc.showToast(message);


                    }else {


                        cc.showToast(message);
                    }


                } catch (Exception e) {

                    cc.showToast("Something Went wrong");

                    e.printStackTrace();
                }
            }else if(status_code_signup.equals("401")){

                cc.showToast("Something Went wrong");
            }



        }

    }

    public class OkHttpHandler extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            status_code_signup = "";
            status_message_signup = "";
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);


            JSONObject json = new JSONObject();

            try {
                json.put("email", ed_login_email.getText().toString());
                json.put("password", ed_login_password.getText().toString());
                json.put("device_id",android_id);


            } catch (Exception e) {
                e.printStackTrace();
            }
            json_str = json.toString();

            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {


            OkHttpClient client = getUnsafeOkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json_str);

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(ConnectionUrl.login)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try {
                okhttp3.Response response = client.newCall(request).execute();

                status_code_signup = response.code() + "";
                status_message_signup = response.message() + "";

                Headers headers = response.headers();

                headers.get("petcare-token");

                cc.savePrefString("token",headers.get("petcare-token"));

               // Log.e("HeaderMainPetcar",headers.get("petcare-token"));

                Log.e("@@@ResultSUHeader", response.message() + "");
                Log.e("@@@ResultSUHeader", response + "");
                Log.e("@@@ResultSUHeader", response.code() + "");
                res = response.body().string();
                Log.e("@@@ResultSU", res);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();


            String Message = status_message_signup;

            if(status_code_signup.equals("200")){


                try {

                    JSONObject jsonObject = new JSONObject(s);

                    String message = jsonObject.getString("message");
                    String statusLogin = jsonObject.getString("result");

                    if (statusLogin.equals("false")){

                        cc.showToast(message);


                    }else {

                        clearField();

                        JSONObject jsonObject2 = jsonObject.getJSONObject("result");

                        Log.e("firstname",jsonObject2.getString("first_name"));

                        cc.savePrefString("firstname",jsonObject2.getString("first_name"));


                        Intent i = new Intent(LoginActivity.this, ActivityMenu.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        cc.savePrefBoolean("isLogin",true);
                        i.putExtra("EXTRA","openFragment2");
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        finish();

                        cc.showToast(message);
                    }


                } catch (Exception e) {

                    cc.showToast("Something Went wrong");

                    e.printStackTrace();
                }


            }else if(status_code_signup.equals("401")){

                Message=status_code_signup;
                cc.showToast(Message);
            }

        }

    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
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

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
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

    public void clearField() {
        ed_login_password.setText("");
        ed_login_email.setText("");

    }
}

package com.mxi.petcar.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.mxi.petcar.R;
import com.mxi.petcar.activity.ActivityMenu;
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
 * Created by mxi on 16/11/17.
 */

public class SignupFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {


    EditText ed_username,ed_firstname,ed_lastname,ed_nickname,ed_email,ed_password,ed_repeat_password;

    TextView tv_signup;

    CheckBox ch_term_condition;

    CommanClass cc;

    public boolean checked = false;

    ProgressDialog pDialog;

    String res = "";
    String json_str = "";

    String status_code_signup = "";
    String status_message_signup = "";

    String android_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_fragment, container, false);

        cc = new CommanClass(getActivity());

        initView(rootView);

        return rootView;

    }

    private void initView(View rootView) {

        android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ed_username = (EditText)rootView.findViewById(R.id.ed_username);
        ed_firstname = (EditText)rootView.findViewById(R.id.ed_firstname);
        ed_lastname = (EditText)rootView.findViewById(R.id.ed_lastname);
        ed_nickname = (EditText)rootView.findViewById(R.id.ed_nickname);
        ed_email = (EditText)rootView.findViewById(R.id.ed_email);
        ed_password = (EditText)rootView.findViewById(R.id.ed_password);
        ed_repeat_password = (EditText)rootView.findViewById(R.id.ed_repeat_password);

        tv_signup = (TextView) rootView.findViewById(R.id.tv_signup);

        ch_term_condition = (CheckBox) rootView.findViewById(R.id.ch_term_condition);
        ch_term_condition.setOnCheckedChangeListener(this);


        clickListner();

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (checked) {
            checked = false;
            ch_term_condition.setChecked(false);
            Log.e("checkbox_false", checked + "");

        } else {
            checked = true;
            ch_term_condition.setChecked(true);

            Log.e("checkbox_true", checked + "");

        }

    }

    private void clickListner() {

        tv_signup.setOnClickListener(new View.OnClickListener() {
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
    }

    public boolean isValidate() {
        String msg = null;

        if (!AndyUtils.nameValidation(ed_username.getText().toString())) {
            msg = "Enter Valid UserName";
            ed_username.requestFocus();


        }else if (!AndyUtils.nameValidation(ed_firstname.getText().toString())) {
            msg = "Enter Valid FirstName";
            ed_firstname.requestFocus();

        }else if (!AndyUtils.nameValidation(ed_lastname.getText().toString())) {
            msg = "Enter Valid LastName";
            ed_lastname.requestFocus();

        }else if (!AndyUtils.nameValidation(ed_nickname.getText().toString())) {
            msg = "Enter Valid NickName";
            ed_nickname.requestFocus();

        }
        else if (!AndyUtils.eMailValidation(ed_email.getText().toString())) {
            msg = "Enter Valid Email Id";
            ed_email.requestFocus();

        } else if (TextUtils.isEmpty(ed_password.getText().toString())) {
            msg = "Enter Valid Password";
            ed_password.requestFocus();
        } else if (TextUtils.isEmpty(ed_repeat_password.getText().toString())) {
            msg = "Enter Valid Repeat Password";
            ed_repeat_password.requestFocus();
        } else if (!ed_repeat_password.getText().toString().equals(ed_password.getText().toString())) {
            msg = "Password didn't match";
            ed_repeat_password.requestFocus();

        } else if (checked != true) {
            msg = getString(R.string.select_term);
        }

        if (msg != null) {
            AndyUtils.showToast(getActivity(), msg);
            return false;
        }
        if (msg == null) {
            return true;
        }
        AndyUtils.showToast(getActivity(), msg);
        return false;
    }

    public class OkHttpHandler extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            status_code_signup = "";
            status_message_signup = "";
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            JSONObject json = new JSONObject();

            try {
                json.put("email", ed_email.getText().toString());
                json.put("password", ed_password.getText().toString());
                json.put("username", ed_username.getText().toString());
                json.put("device_id", android_id);
                json.put("first_name", ed_firstname.getText().toString());
                json.put("last_name", ed_lastname.getText().toString());
                json.put("nickname", ed_nickname.getText().toString());


            } catch (Exception e) {
                e.printStackTrace();
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
                    .url(ConnectionUrl.signup)
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
                    //JSONObject jsonObject2 = jsonObject.getJSONObject("data");

                    String message = jsonObject.getString("message");
                    String statusRegister = jsonObject.getString("result");

                    if (statusRegister.equals("false")){

                        cc.showToast(message);


                    }else {

                        clearField();

                        JSONObject jsonObject2 = jsonObject.getJSONObject("result");

                        Log.e("firstname",jsonObject2.getString("first_name"));

                        cc.savePrefString("firstname",jsonObject2.getString("first_name"));

                        Intent i = new Intent(getActivity(), ActivityMenu.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("LoginMain","loginmain");
                        i.putExtra("EXTRA","openFragment2");
                        cc.savePrefBoolean("isLogin",true);
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        getActivity().finish();

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
        ed_username.setText("");
        ed_firstname.setText("");
        ed_lastname.setText("");
        ed_nickname.setText("");
        ed_email.setText("");
        ed_password.setText("");
        ed_repeat_password.setText("");
    }


}

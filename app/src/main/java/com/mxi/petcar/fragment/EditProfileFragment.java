package com.mxi.petcar.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by mxi on 6/11/17.
 */

public class EditProfileFragment extends Fragment {

    EditText ed_username,ed_firstname,ed_lastname,ed_nickname,ed_email;
    String username,firstname,lastname,nickname,email;

    TextView tv_edit_profile;

    CommanClass cc;

    ProgressDialog pDialog;

    String res = "";
    String json_str = "";

    String status_code_signup = "";
    String status_message_signup = "";

    String android_id;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_profile_fragment, container, false);

        initView(rootView);

        return rootView;

    }

    private void initView(View rootView) {

        cc = new CommanClass(getActivity());

        android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ed_username = (EditText)rootView.findViewById(R.id.ed_username);
        ed_firstname = (EditText)rootView.findViewById(R.id.ed_firstname);
        ed_lastname = (EditText)rootView.findViewById(R.id.ed_lastname);
        ed_nickname = (EditText)rootView.findViewById(R.id.ed_nickname);
        ed_email = (EditText)rootView.findViewById(R.id.ed_email);

        ed_username.setFocusable(false);
        ed_username.setClickable(false);

        tv_edit_profile = (TextView) rootView.findViewById(R.id.tv_edit_profile);

        loadProfileData();

        clickListner();
    }

    private void loadProfileData() {

        if (!cc.isConnectingToInternet()) {

            cc.showToast(getString(R.string.no_internet));
        } else {

            new GetProfile().execute();

        }
    }

    private void clickListner() {

        tv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!cc.isConnectingToInternet()) {

                    cc.showToast(getString(R.string.no_internet));
                } else {

                    if (isValidate()) {

                        new EditProfile().execute();
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

    public class GetProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            json_str="";
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            JSONObject json = new JSONObject();


            try {
                json.put("device_id",android_id);

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
                    .url(ConnectionUrl.get_profile)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("petcare-token", cc.loadPrefString("token"))
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

                        JSONObject jsonObject2 = jsonObject.getJSONObject("result");

                        username = jsonObject2.getString("username");
                        firstname = jsonObject2.getString("first_name");
                        lastname = jsonObject2.getString("last_name");
                        nickname = jsonObject2.getString("nickname");
                        email = jsonObject2.getString("email");

                        ed_username.setText(username);
                        ed_firstname.setText(firstname);
                        ed_lastname.setText(lastname);
                        ed_nickname.setText(nickname);
                        ed_email.setText(email);

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

    public class EditProfile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            json_str="";
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            JSONObject json = new JSONObject();


            try {
                json.put("email",ed_email.getText().toString().trim());
                json.put("username",ed_username.getText().toString().trim());
                json.put("device_id",android_id);
                json.put("first_name",ed_firstname.getText().toString().trim());
                json.put("last_name",ed_lastname.getText().toString().trim());
                json.put("nickname",ed_nickname.getText().toString().trim());

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("JSON could not be made");
            }
            json_str = json.toString();

            Log.e("InputText",json_str);

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            OkHttpClient client = getUnsafeOkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json_str);

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(ConnectionUrl.edit_profile)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("petcare-token", cc.loadPrefString("token"))
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

                        new GetProfile().execute();


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
}

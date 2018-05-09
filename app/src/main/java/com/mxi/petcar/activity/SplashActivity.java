package com.mxi.petcar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mxi.petcar.R;
import com.mxi.petcar.comman.CommanClass;


public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    CommanClass cc;
    ImageView iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        cc = new CommanClass(this);

        iv_logo =(ImageView)findViewById(R.id.iv_logo);

        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_anim_logo);
        iv_logo.startAnimation(pulse);

        CountDown();

    }


    private void CountDown() {

        new Handler().postDelayed(new Runnable() {




            @Override
            public void run() {

                if(cc.isConnectingToInternet()){

                    if(cc.loadPrefBoolean("isLogin")){
                        Intent i = new Intent(SplashActivity.this, ActivityMenu.class);
                        i.putExtra("LoginMain","loginmain");
                        i.putExtra("EXTRA","openFragment2");
                        startActivity(i);
                        finish();
                    }else{
                        Intent i = new Intent(SplashActivity.this, ActivityMenu.class);
                        i.putExtra("EXTRA","openFragment2");
                        startActivity(i);
                        finish();
                    }

                }else{
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);
                    alertDialogBuilder
                            .setMessage(R.string.dialog_internet_alert)
                            .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }
        }, SPLASH_TIME_OUT);

    }

}

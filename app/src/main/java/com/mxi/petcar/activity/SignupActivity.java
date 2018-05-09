package com.mxi.petcar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mxi.petcar.R;

/**
 * Created by mxi on 4/11/17.
 */

public class SignupActivity extends AppCompatActivity {

    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_signup);

        init();

    }

    private void init() {

        iv_back = (ImageView)findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finish();
    }
}

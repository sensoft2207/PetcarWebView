package com.mxi.petcar;

import android.app.Application;
import android.util.Log;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Created by mxi on 16/11/17.
 */

public class application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CookieManager cmrCookieMan = new CookieManager(new MyCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cmrCookieMan);

        Log.e("PetcareCookies", String.valueOf(cmrCookieMan.getCookieStore()));
    }
}

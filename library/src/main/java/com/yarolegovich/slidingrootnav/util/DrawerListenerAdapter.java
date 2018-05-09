package com.yarolegovich.slidingrootnav.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yarolegovich.slidingrootnav.callback.DragListener;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

/**
 * Created by yarolegovich on 26.03.2017.
 */

public class DrawerListenerAdapter implements DragListener, DragStateListener {

    public static DrawerLayout.DrawerListener adaptee;
    public static View drawer;

    Context context;

    Activity ac;

    public DrawerListenerAdapter(DrawerLayout.DrawerListener adaptee, View drawer, Activity ac) {
        this.adaptee = adaptee;
        this.drawer = drawer;
        this.ac = ac;
    }

    @Override
    public void onDrag(float progress) {
        adaptee.onDrawerSlide(drawer, progress);
    }

    @Override
    public void onDragStart() {
        adaptee.onDrawerStateChanged(DrawerLayout.STATE_DRAGGING);
    }

    @Override
    public void onDragEnd(boolean isMenuOpened) {
        if (isMenuOpened) {
            adaptee.onDrawerOpened(drawer);

            hideKeyboard(drawer,ac);

           Log.e("MenuState","open");


        } else {
            adaptee.onDrawerClosed(drawer);
            Log.e("MenuState","close");
        }
        adaptee.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
    }

    public static void hideKeyboard(View pView, Activity pActivity) {
        if (pView == null) {
            pView = pActivity.getWindow().getCurrentFocus();
        }
        if (pView != null) {
            InputMethodManager imm = (InputMethodManager) pActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(pView.getWindowToken(), 0);
            }
        }
    }
}

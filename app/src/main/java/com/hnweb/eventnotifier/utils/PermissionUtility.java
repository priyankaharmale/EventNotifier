package com.hnweb.eventnotifier.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by PC-21 on 28-Mar-18.
 */

public class PermissionUtility {

    private static final int REQ_PERMISSIONS = 112;
    private final Context context;
    private OnPermissionCallback listner;

    public void setListner(OnPermissionCallback listner) {
        this.listner = listner;
    }


    public interface OnPermissionCallback {
        public void OnComplete(boolean is_granted);
    }

    public PermissionUtility(Context context) {
        this.context = context;
    }

    public boolean checkPermission(ArrayList<String> permission_list) {

        boolean have_granted = false;
        for (int i = 0; i < permission_list.size(); i++) {
        // Here, thisActivity is the current com.hnweb.qrcodeattandance.activity
            if (ContextCompat.checkSelfPermission(context, permission_list.get(i)) != PackageManager.PERMISSION_GRANTED) {
                have_granted = false;

            } else {

                have_granted = true;
            }


        }
        if (!have_granted) {
            ActivityCompat.requestPermissions((Activity) context, permission_list.toArray(new String[permission_list.size()]),
                    REQ_PERMISSIONS);
        } else {
            if (listner != null) {
                listner.OnComplete(have_granted);
            }
        }


        return have_granted;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSIONS: {
                boolean have_granted = false;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    have_granted = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    have_granted = false;
                }
                if (listner != null) {
                    listner.OnComplete(have_granted);
                }

            }
        }
    }
}

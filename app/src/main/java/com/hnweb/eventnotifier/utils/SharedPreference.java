package com.hnweb.eventnotifier.utils;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.hnweb.eventnotifier.LoginActivity;
import com.hnweb.eventnotifier.LoginActivityDemo;
import com.hnweb.eventnotifier.contants.AppConstant;


public class SharedPreference {

    public static final String PREFS_NAME = "AOP_PREFS";
    public static final String PREFS_NAME_REMBER_ME = "AOP_PREFS_REM_ME";
    public static final String PREFS_KEY = "isLogin";

    public SharedPreference() {
        super();
    }


    public static Boolean IsLogin(Context context) {
        SharedPreferences settings;
        Boolean isLogin;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isLogin = settings.getBoolean(PREFS_KEY, false);
        return isLogin;
    }


    public static void profileSaveRemberMe(Context context, String social_id, String user_id, String login_through, String rember_me, String password, String email, String full_name) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME_REMBER_ME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString("social_id", social_id);
        editor.putString("user_id", user_id);
        editor.putString("full_name", full_name);
        editor.putString("login_through", login_through);
        editor.putString("rember_me", rember_me);
        editor.putString("password", password);
        editor.putString("email", email);
        editor.commit();
    }


    public static void profileSave(Context context, String full_name, String email, String mobileNo, String password, String camimage, String experience) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString(AppConstant.KEY_NAME, full_name);
        editor.putString(AppConstant.KEY_EMAIL, email);
        editor.putString(AppConstant.KEY_PHONE, mobileNo);
        editor.putString(AppConstant.KEY_PASSWORD, password);
        editor.putString(AppConstant.KEY_PASSWORD, password);
        if (!camimage.equals("")) {
            editor.putString(AppConstant.KEY_IMAGE, camimage);

        } else {
            editor.putString(AppConstant.KEY_IMAGE, "");

        }
        editor.commit();
    }

    public static void addressSave(Context context, String country, String state, String city, String street, String zipcode) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString(AppConstant.KEY_COUNTRY, country);
        editor.putString(AppConstant.KEY_STATE, state);
        editor.putString(AppConstant.KEY_CITY, city);
        editor.putString(AppConstant.KEY_STREET, street);
        editor.putString(AppConstant.KEY_ZIPCODE, zipcode);
        editor.commit();
    }

    public static void profileSaveAfterupdate(Context context, String full_name, String profile_pic, String email) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString("full_name", full_name);
        editor.putString("email_address", email);
        editor.putString("profile_photo", profile_pic);
        editor.putBoolean("login", true);

        editor.commit();
    }


    ///////////////Logout///////////////////////////////////////////////////////

    public static void logout(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure  you want to logout ? ");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                SharedPreference.clearSharedPreference(context);
                Intent in = new Intent(context, LoginActivityDemo.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(in);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    //////////////////////////////////////////////////////////////////////
    public static void user_type(Context context, String user_type) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString("user_type", user_type);


        editor.commit();
    }

    public static void clearSharedPreference(Context context) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public static void clearSharedPreferenceRemember(Context context) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME_REMBER_ME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(PREFS_KEY);
        editor.commit();
    }


    public static void editProfiledData(Context context, String user_name, String email_id, String gender, String profile_photo) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2


        editor.putString("full_name", user_name);
        editor.putString("email_address", email_id);
        editor.putString("gender", gender);
        editor.putString("profile_photo", profile_photo);


        editor.commit();

    }

    public static void editUnseencount(Context context, String unseenCount) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString("unseen_count", unseenCount);
        Log.d("unseen_count123", "" + unseenCount);
        editor.commit();

    }

    public static void statusLogin(Context context, String status) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString("status", status);
        Log.d("unseen_count123", "" + status);
        editor.commit();

    }

}
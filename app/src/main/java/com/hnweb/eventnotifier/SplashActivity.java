package com.hnweb.eventnotifier;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.hnweb.eventnotifier.utils.ConnectionDetector;
import com.hnweb.eventnotifier.utils.PermissionUtility;
import com.hnweb.eventnotifier.utils.PostDataTask;
import com.hnweb.eventnotifier.utils.SharedPrefManager;

import java.util.ArrayList;


/**
 * Created by Priyanka H on 12/06/2018.
 */
public class SplashActivity extends AppCompatActivity {
    Button btn_getstart;
    SharedPreferences pref;
    String useridUser, userType;
    ConnectionDetector connectionDetector;
    private PermissionUtility putility;
    ArrayList<String> permission_list;
  //  public static final MediaType FORM_DATA_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    //URL derived from form URL
 //   public static final String URL = "https://docs.google.com/forms/d/e/1FAIpQLSfmpBDW9FNcKQf4Pz9QRbCwcffB3zdEAU6bm8gInLf78Ho-jw/formResponse";

    //input element ids found from the live form page
    public static final String EMAIL_KEY = "entry.76980122";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        pref = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        connectionDetector = new ConnectionDetector(SplashActivity.this);

       /* PostDataTask postDataTask = new PostDataTask();

        postDataTask.execute(URL, deviceInfo());
*/
        runTimePermission();

      //  getdeviceToken();
      /*  useridUser = pref.getString(AppConstant.KEY_ID, null);
        userType = pref.getString(AppConstant.KEY_TYPE, null);
      */  btn_getstart = (Button) findViewById(R.id.btn_getstart);
        btn_getstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectionDetector.isConnectingToInternet()) {
                    /*if (useridUser != null && userType.equals("1")) {
*//*                        Intent intent = new Intent(SplashActivity.this, SplashActivityVendor.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }*//* else {
                  */      Intent intentLogin = new Intent(SplashActivity.this, LoginActivity.class);
                        intentLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentLogin);
                   // }
                } else {
                    Toast.makeText(SplashActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void runTimePermission() {

        putility = new PermissionUtility(this);
        permission_list = new ArrayList<String>();
        permission_list.add(android.Manifest.permission.INTERNET);
        permission_list.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
        permission_list.add(android.Manifest.permission.WAKE_LOCK);
        permission_list.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permission_list.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        permission_list.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permission_list.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permission_list.add(Manifest.permission.CAMERA);
        putility.setListner(new PermissionUtility.OnPermissionCallback() {
            @Override
            public void OnComplete(boolean is_granted) {
                Log.i("OnPermissionCallback", "is_granted = " + is_granted);
                if (is_granted) {

                } else {
                    putility.checkPermission(permission_list);
                }
            }
        });
        putility.checkPermission(permission_list);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (putility != null) {
            putility.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

/*
    private void getdeviceToken() {
        try {
            String t = SharedPrefManager.getInstance(this).getDeviceToken();

            if (t.equals("") || t==null || t.equals("null")) {
                Log.d("Tokan", "t-NULL");
            } else {
                Log.d("Tokan", t);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
*/

    public String deviceInfo() {

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;

        String s = "Debug-infos:";
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

        s += "\n RELEASE: " + android.os.Build.VERSION.RELEASE;
        s += "\n BRAND: " + android.os.Build.BRAND;
        s += "\n DISPLAY: " + android.os.Build.DISPLAY;
        s += "\n CPU_ABI: " + android.os.Build.CPU_ABI;
        s += "\n CPU_ABI2: " + android.os.Build.CPU_ABI2;
        s += "\n UNKNOWN: " + android.os.Build.UNKNOWN;
        s += "\n HARDWARE: " + android.os.Build.HARDWARE;
        s += "\n Build ID: " + android.os.Build.ID;
        s += "\n MANUFACTURER: " + android.os.Build.MANUFACTURER;
        s += "\n SERIAL: " + android.os.Build.SERIAL;
        s += "\n USER: " + android.os.Build.USER;
        s += "\n HOST: " + android.os.Build.HOST;
        s += "\n APK Version: " + version;

        return s;
    }

}

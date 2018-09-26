package com.hnweb.eventnotifier.fierbase;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hnweb.eventnotifier.utils.SharedPrefManager;

/**
 * Created by Priyanka on 26/09/2018.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    static String refreshedToken="";
    SharedPreferences.Editor editor1;

    @Override
    public void onTokenRefresh() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshedtoken: " + refreshedToken);

        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //saving the token on shared preferences
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    }


    //storing token to mysql server
    public static String sendTokenToServer(Activity activity) {
//        ProgressDialog progressDialog = new ProgressDialog(activity);
//        progressDialog.setMessage("Registering Device...");
//        progressDialog.show();

        final String token = SharedPrefManager.getInstance(activity.getApplicationContext()).getDeviceToken();
//        final String email = editTextEmail.getText().toString();

        if (token == null) {
//            progressDialog.dismiss();
            Toast.makeText(activity, "Token not generated", Toast.LENGTH_LONG).show();
            return token;
        }
        return token;
    }


    public static String sendRegistrationToServer() {
//You can implement this method to store the token on your server
//Not required for current project
        String regtoken = refreshedToken;
        Log.d(TAG, "Reg token:-" + regtoken);
        return regtoken;
    }

}

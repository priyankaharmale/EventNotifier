package com.hnweb.eventnotifier;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.utils.AlertUtility;
import com.hnweb.eventnotifier.utils.AppUtils;
import com.hnweb.eventnotifier.utils.LoadingDialog;
import com.hnweb.eventnotifier.utils.SharedPrefManager;
import com.hnweb.eventnotifier.utils.Utils;
import com.hnweb.eventnotifier.utils.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/* * Created by Priyanka H on 21/09/2018.
 */


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_register;
    Button btn_login;
    EditText et_password, et_email;
    String deviceToken = "";
    LoadingDialog loadingDialog;
    SharedPreferences prefUser;
    SharedPreferences.Editor editorUser;
    TextView tv_forgotpwd;
    EditText et_emailId;
    Dialog dialog;
    ImageView iv_facebook;
    private static final String TAG = "Androidapp";
    private FirebaseAuth mFirebaseAuth;
    LoginButton fbloginButton;
    //FaceBook callbackManager
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getdeviceToken();
        fbsha1key();
        loadingDialog = new LoadingDialog(this);
        tv_register = findViewById(R.id.tv_register);
        btn_login = findViewById(R.id.btn_login);
        fbloginButton = (LoginButton) findViewById(R.id.login_button);

        iv_facebook = findViewById(R.id.iv_facebook);
       // iv_facebook.setOnClickListener(this);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_forgotpwd = findViewById(R.id.tv_forgotpwd);
        et_email = (EditText) findViewById(R.id.et_email);
        prefUser = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        editorUser = prefUser.edit();
        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        tv_forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogforgotPwd();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation1()) {

                    if (Utils.isNetworkAvailable(LoginActivity.this)) {

                        String password = et_password.getText().toString();
                        String email = et_email.getText().toString();

                        login(email, password);

                        Toast.makeText(LoginActivity.this, "Login ", Toast.LENGTH_SHORT).show();

                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();

        fbloginButton.setReadPermissions("email", "public_profile");
        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                signInWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private boolean checkValidation1() {
        boolean ret = true;
        if (!Validations.hasText(et_email, "Please Enter Email ID "))
            ret = false;
        if (!Validations.isEmailAddress(et_email, true, "Please Enter Valid Email ID"))
            ret = false;
        if (!Validations.hasText(et_password, "Please Enter RE-Enter Password"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_password, "Password atleast 7 characters"))
            ret = false;
        return ret;
    }

    private void getdeviceToken() {
        try {
            String token = SharedPrefManager.getInstance(this).getDeviceToken();

            if (token.equals("")) {
                Log.d("Tokan", "t-NULL");
                deviceToken = token;
            } else {
                Log.d("Tokan", token);
                deviceToken = token;
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void login(final String email, final String password) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_LOGIN_USER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                try {
                                                    JSONObject jsonObject = j.getJSONObject("response");

                                                    String user_id = jsonObject.getString("user_id");
                                                    String user_name = jsonObject.getString("name");
                                                    String user_email = jsonObject.getString("email_address");
                                                    String user_phone = jsonObject.getString("phone_number");
                                                    String user_image = jsonObject.getString("profile_photo");
                                                    String device_type = jsonObject.getString("device_type");
                                                    String user_city = jsonObject.getString("city");
                                                    String user_state = jsonObject.getString("state");
                                                    String user_dob = jsonObject.getString("date_of_birth");
                                                    String created_datetime = jsonObject.getString("created_datetime");
                                                    String device_token = jsonObject.getString("device_token");
                                                    String status = jsonObject.getString("status");
                                                    String created_on = jsonObject.getString("created_on");

                                                    editorUser.putString(AppConstant.KEY_ID, user_id);
                                                    editorUser.putString(AppConstant.KEY_NAME, user_name);
                                                    editorUser.putString(AppConstant.KEY_EMAIL, user_email);
                                                    editorUser.putString(AppConstant.KEY_PHONE, user_phone);
                                                    editorUser.putString(AppConstant.KEY_IMAGE, user_image);
                                                    editorUser.putString(AppConstant.KEY_DEVICETYPE, device_type);
                                                    editorUser.putString(AppConstant.KEY_CITY, user_city);
                                                    editorUser.putString(AppConstant.KEY_STATE, user_state);
                                                    editorUser.commit();

                                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();


                                                } catch (JSONException e) {
                                                    System.out.println("jsonexeption" + e.toString());
                                                }
                                               /* Intent intent = new Intent(UserLoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();*/
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                        AlertUtility.showAlert(LoginActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.KEY_EMAIL, email);
                    params.put(AppConstant.KEY_PASSWORD, password);
                    params.put(AppConstant.KEY_DEVICETYPE, "Android");
                    if (deviceToken.equals("")) {
                        params.put(AppConstant.KEY_DEVICETOKEN, "");
                    } else {
                        params.put(AppConstant.KEY_DEVICETOKEN, deviceToken);
                    }

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void dialogforgotPwd() {
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_forgotpwd);
        //   final EditText et_exprydate = (EditText) dialog.findViewById(R.id.et_exprydate);
        TextView tv_submit = (TextView) dialog.findViewById(R.id.tv_submit);
        TextView tv_cancle = (TextView) dialog.findViewById(R.id.tv_cancle);
        et_emailId = (EditText) dialog.findViewById(R.id.et_emailId);

        dialog.show();
        dialog.setCancelable(true);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkValidation()) {
                    if (Utils.isNetworkAvailable(LoginActivity.this)) {
                        forgotpwd(et_emailId.getText().toString());
                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }

            }
        });

    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validations.hasText(et_emailId, "Please Enter Email ID "))
            ret = false;

        if (!Validations.isEmailAddress(et_emailId, true, "Please Enter Valid Email ID"))
            ret = false;
        return ret;

    }

    private void forgotpwd(final String email) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_FORGOTPWD_USER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("response");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog1, int id) {

                                                dialog1.dismiss();
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                        AlertUtility.showAlert(LoginActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.KEY_EMAIL, email);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void fbsha1key() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {


            case R.id.iv_facebook:
                fbloginButton.performClick();
            default:
                return;
        }
    }

    private void signInWithFacebook(AccessToken token) {
        Log.d(TAG, "signInWithFacebook:" + token);

        loadingDialog.show();


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "Success.",
                                    Toast.LENGTH_SHORT).show();
                            String uid=task.getResult().getUser().getUid();
                            String name=task.getResult().getUser().getDisplayName();
                            String email=task.getResult().getUser().getEmail();

                            String image=task.getResult().getUser().getPhotoUrl().toString();
                            String phone=task.getResult().getUser().getPhoneNumber();
                            Toast.makeText(LoginActivity.this, image,
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, phone,
                                    Toast.LENGTH_SHORT).show();
                            //Create a new User and Save it in Firebase database
                            // User user = new User(uid,name,null,email,null);

                            // mRef.child(uid).setValue(user);
                           // signInfrom="FB";
                          //  sendDataSocailMedia();

                        }

                        loadingDialog.dismiss();
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

    }

}

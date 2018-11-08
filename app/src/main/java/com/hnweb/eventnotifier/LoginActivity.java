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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.utils.AlertUtility;
import com.hnweb.eventnotifier.utils.AppUtils;
import com.hnweb.eventnotifier.utils.LoadingDialog;
import com.hnweb.eventnotifier.utils.SharedPrefManager;
import com.hnweb.eventnotifier.utils.Utils;
import com.hnweb.eventnotifier.utils.Validations;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/* * Created by Priyanka H on 21/09/2018.
 */


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    TextView tv_register;
    Button btn_login;
    EditText et_password, et_email;
    String deviceToken = "";
    LoadingDialog loadingDialog;
    SharedPreferences prefUser;
    SharedPreferences.Editor editorUser;
    TextView tv_forgotpwd;
    private static final int RC_SIGN_IN = 9001;

    EditText et_emailId;
    Dialog dialog;
    ImageView iv_facebook, iv_twitter, iv_google;
    private static final String TAG = "Androidapp";
    private FirebaseAuth mFirebaseAuth;
    LoginButton fbloginButton;
    //FaceBook callbackManager
    private CallbackManager callbackManager;
    TwitterAuthClient mTwitterAuthClient;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));


        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_login);

        getdeviceToken();
        fbsha1key();

        loadingDialog = new LoadingDialog(this);

        tv_register = findViewById(R.id.tv_register);

        btn_login = findViewById(R.id.btn_login);
        fbloginButton = (LoginButton) findViewById(R.id.login_button);
        iv_facebook = findViewById(R.id.iv_facebook);
        iv_google = findViewById(R.id.iv_google);
        iv_twitter = findViewById(R.id.iv_twitter);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_forgotpwd = findViewById(R.id.tv_forgotpwd);
        et_email = (EditText) findViewById(R.id.et_email);

        prefUser = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        editorUser = prefUser.edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
// Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Facebook Callback manager
        callbackManager = CallbackManager.Factory.create();
        //Twitter intialization
        mTwitterAuthClient = new TwitterAuthClient();


        iv_facebook.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        iv_twitter.setOnClickListener(this);
        tv_forgotpwd.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        iv_google.setOnClickListener(this);
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
                                                    String user_image = jsonObject.getString("profile_picture");
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


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {


            case R.id.iv_facebook:
                fbloginButton.performClick();
                break;
            case R.id.iv_twitter:
                // signInWithTwitter();
                break;
            case R.id.tv_register:
                intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forgotpwd:
                dialogforgotPwd();
                break;
            case R.id.btn_login:
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
                break;
            case R.id.iv_google:
                signIn();
                break;
            default:
                return;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    /*********************************FaceBook Login*************************************************/
    private void signInWithFacebook(AccessToken token) {
        Log.d(TAG, "signInWithFacebook:" + token);
        loadingDialog.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Success.",
                                    Toast.LENGTH_SHORT).show();
                            String uid = task.getResult().getUser().getUid();
                            String name = task.getResult().getUser().getDisplayName();
                            String email = task.getResult().getUser().getEmail();

                            String image = task.getResult().getUser().getPhotoUrl().toString();
                            String phone = task.getResult().getUser().getPhoneNumber();

                            registerwithFacebook(email, uid, name);

                        }

                        loadingDialog.dismiss();
                    }
                });
    }

    private void registerwithFacebook(final String email, final String facebookId, final String name) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_FACEBOOK_LOGIN_USER,
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
                                                    String facebook_id = jsonObject.getString("facebook_id");
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
                                                    dialog.dismiss();
                                                } catch (JSONException e) {
                                                    System.out.println("jsonexeption" + e.toString());

                                                }


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
                    params.put(AppConstant.KEY_NAME, name);
                    params.put("facebook_id", facebookId);
                    params.put(AppConstant.KEY_DEVICETYPE, "Android");
                    if (deviceToken.equals("")) {
                        params.put("token", "");
                    } else {
                        params.put("token", deviceToken);
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

    /*********************************Twitter Login*************************************************/
    private void signInWithTwitter() {
        mTwitterAuthClient.authorize(LoginActivity.this, new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
// Success
                Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                loginNew(twitterSessionResult);
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                Toast.makeText(LoginActivity.this, "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginNew(Result<TwitterSession> result) {

//Creating a twitter session with result's data
        final TwitterSession session = result.data;


//Getting the username from session
        final String username = session.getUserName();
        final TwitterAuthToken authToken = session.getAuthToken();

        String token = authToken.token;
        String secret = authToken.secret;
        Log.e("SESSION", username + " " + authToken);

        getUserData(session, username);

    }

    public void getUserData(TwitterSession session, final String username) {
        Call<User> userResult = TwitterCore.getInstance().getApiClient(session).getAccountService().verifyCredentials(true, false, true);
        userResult.enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> result) {
                User user = result.data;
                String twitterImage = user.profileImageUrl;
                try {
                    System.out.println("{" + " id : " + user.idStr + " name : " + user.name + " followers : " + String.valueOf(user.followersCount) + " createdAt : " + user.createdAt + " username :" + username + "}");
                    Log.d("useridid", user.idStr);
                    Log.d("name", user.name);
                    Log.d("followers ", String.valueOf(user.followersCount));
                    Log.d("createdAt", user.createdAt);
                    String[] name = user.name.split(" ");
                    Toast.makeText(LoginActivity.this, user.idStr + user.email + user.name, Toast.LENGTH_LONG).show();
                    //    LoginWithTwitter(user.idStr, user.email, user.name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(LoginActivity.this, "Failure", Toast.LENGTH_LONG).show();

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


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
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


                        } else {
                            Log.w(TAG, "success", task.getException());
                            Toast.makeText(LoginActivity.this, "Success.",
                                    Toast.LENGTH_SHORT).show();
                            String uid = task.getResult().getUser().getUid();
                            String name = task.getResult().getUser().getDisplayName();
                            String email = task.getResult().getUser().getEmail();

                            String image = task.getResult().getUser().getPhotoUrl().toString();
                            String phone = task.getResult().getUser().getPhoneNumber();
                            Toast.makeText(LoginActivity.this, "Success.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, name,
                                    Toast.LENGTH_SHORT).show();
                            registerwithGooglePlus(email,uid,name);
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void registerwithGooglePlus(final String email, final String googleplusId, final String name) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GOOGLE_LOGIN_USER,
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
                                                    String user_image = jsonObject.getString("profile_picture");
                                                    String device_type = jsonObject.getString("device_type");
                                                    String user_city = jsonObject.getString("city");
                                                    String user_state = jsonObject.getString("state");
                                                    String user_dob = jsonObject.getString("date_of_birth");
                                                    String created_datetime = jsonObject.getString("created_datetime");
                                                    String device_token = jsonObject.getString("device_token");
                                                    String status = jsonObject.getString("status");
                                                    String facebook_id = jsonObject.getString("facebook_id");
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
                                                    dialog.dismiss();
                                                } catch (JSONException e) {
                                                    System.out.println("jsonexeption" + e.toString());

                                                }


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
                    params.put(AppConstant.KEY_NAME, name);
                    params.put("google_id", googleplusId);
                    params.put(AppConstant.KEY_DEVICETYPE, "Android");
                    if (deviceToken.equals("")) {
                        params.put("token", "");
                    } else {
                        params.put("token", deviceToken);
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

}

package com.hnweb.eventnotifier;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.fragment.ChangePasswordFragment;
import com.hnweb.eventnotifier.fragment.HomeFragment;
import com.hnweb.eventnotifier.fragment.PastEventsFragment;
import com.hnweb.eventnotifier.fragment.PurchaseHistoryFragment;
import com.hnweb.eventnotifier.fragment.UpcomingEventsFragment;
import com.hnweb.eventnotifier.utils.ConnectionDetector;
import com.hnweb.eventnotifier.utils.LoadingDialog;
import com.hnweb.eventnotifier.utils.ProfileUpdateModel;
import com.hnweb.eventnotifier.utils.SharedPrefManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/* * Created by Priyanka H on 24/09/2018.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProfileUpdateModel.OnCustomStateListener, GoogleApiClient.OnConnectionFailedListener {

    LoadingDialog loadingDialog;
    DrawerLayout drawer;
    private View navHeader;
    public MenuItem liveitemList, liveitemMap;
    String profile_image, user_name, user_email, user_id;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    public Toolbar toolbar;
    String deviceToken = "";
    ConnectionDetector connectionDetector;
    ImageView imageViewProfile, imageViewClose, imageViewUpload;
    TextView textViewUserName, textViewAdrress;
    SharedPreferences prefs;
    ProgressBar progressBar;
    TextView textCartItemCount;
    String mCartItemCount = "";
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            stateChanged();
            //getNotificationCount();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        getdeviceToken();
        connectionDetector = new ConnectionDetector(HomeActivity.this);

        loadingDialog = new LoadingDialog(HomeActivity.this);

        prefs = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        profile_image = prefs.getString(AppConstant.KEY_IMAGE, null);
        user_name = prefs.getString(AppConstant.KEY_NAME, null);
        user_email = prefs.getString(AppConstant.KEY_EMAIL, null);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navHeader = navigationView.getHeaderView(0);
        textViewUserName = navHeader.findViewById(R.id.tv_user_name);
        textViewAdrress = navHeader.findViewById(R.id.textView_address);
        progressBar = navHeader.findViewById(R.id.progress_bar_nav);

        imageViewProfile = navHeader.findViewById(R.id.profile_image);
        imageViewClose = navHeader.findViewById(R.id.imageView_close);
        imageViewUpload = navHeader.findViewById(R.id.profile_image_photoupload);

        imageViewUpload.setVisibility(View.VISIBLE);

     /*   textViewUserName.setText(user_name);
        textViewAdrress.setText(user_street + ", " + user_city);
*/
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        imageViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
               /* Fragment fragment = null;
                fragment = new ProfileEditFragment();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();

  */
                Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });
        progressBar = navHeader.findViewById(R.id.progress_bar_nav);
        if (profile_image.equals("") || profile_image == null) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.img_no_pic_navigation)
                    .into(imageViewProfile);
            //Glide.with(getApplicationContext()).load(R.drawable.user_register).into(imageViewProfile);
        } else {

            progressBar.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(profile_image)
                    .placeholder(R.drawable.img_no_pic_navigation)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageViewProfile);
            // Glide.with(getApplicationContext()).load(profile_image).into(imageViewProfile);
        }

        if (savedInstanceState == null) {

            Fragment fragment = null;
            fragment = new HomeFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame_layout, fragment);
            transaction.commit();
            //toolbarmiddletext.setText("Home");
            //getSupportActionBar().setTitle("Find Craves");
            //drawerFragment.closeDrawer(GravityCompat.START);
        }
        if (connectionDetector.isConnectingToInternet()) {
            //  getNotificationCount();
        } else {
            Toast.makeText(HomeActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();

        }
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

            if (token.equals("")) {

            } else {
                updateDeviceToken(deviceToken);
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }


    }

    private void updateDeviceToken(String deviceToken) {

        /*//loadingDialog.show();
        String get_user_id = prefs.getString("user_id_user", null);
        Map<String, String> params = new HashMap<>();
        params.put("u_id", get_user_id);
        params.put("devicetoken", deviceToken);
        params.put("device_type", "Android");
        Log.e("Params", params.toString());

        RequestInfo request_info = new RequestInfo();
        request_info.setMethod(RequestInfo.METHOD_POST);
        request_info.setRequestTag("login");
        request_info.setUrl(WebsServiceURLUser.USER_UPDATE_TOKEN_DEVICE);
        request_info.setParams(params);

        DataUtility.submitRequest(loadingDialog, MainActivityUser.this, request_info, false, new DataUtility.OnDataCallbackListner() {
            @Override
            public void OnDataReceived(String data) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.i("Response", "MainActivityUser" + data);

                try {
                    JSONObject jobj = new JSONObject(data);
                    int message_code = jobj.getInt("message_code");

                    String msg = jobj.getString("message");
                    Log.e("FLag", message_code + " :: " + msg);
                    if (message_code == 1) {
                        Toast.makeText(MainActivityUser.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        //Utils.AlertDialog(MainActivityUser.this, msg);

                    }
                } catch (JSONException e) {
                    System.out.println("jsonexeption" + e.toString());
                }
            }

            @Override
            public void OnError(String message) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                AlertUtility.showAlert(MainActivityUser.this, false, "Network Error,Please Check Internet Connection");
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 1) {
                if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                    //super.onBackPressed();
                    finish();
                } else {
                   /* Snackbar snackbar = Snackbar.make(coordinatorLayout, "Press once again to exit!!", Snackbar.LENGTH_LONG);

                    snackbar.show();*/
                    Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
                }
                back_pressed = System.currentTimeMillis();
            } else {
                if (getFragmentManager().getBackStackEntryCount() > 1) {
                    getFragmentManager().popBackStack();

                    //getFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       /* getMenuInflater().inflate(R.menu.main, menu);

        liveitemList = menu.findItem(R.id.action_list);
        liveitemMap = menu.findItem(R.id.action_map);
        SpannableString sList = new SpannableString(liveitemList.getTitle().toString());
        SpannableString sMap = new SpannableString(liveitemMap.getTitle().toString());
        sList.setSpan(new ForegroundColorSpan(Color.WHITE), 0, sList.length(), 0);
        sMap.setSpan(new ForegroundColorSpan(Color.WHITE), 0, sMap.length(), 0);
        liveitemList.setTitle(sList);
        liveitemMap.setTitle(sMap);
        final MenuItem menuItem = menu.findItem(R.id.action_notification);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     /*   if (id == R.id.action_message) {
            Fragment fragment = new MessageFragment();

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
            }

        } else if (id == R.id.action_notification) {
            Fragment fragment = new NotificationFragment();
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
            }
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            // Handle the camera action
            fragment = new HomeFragment();
        } else if (id == R.id.nav_logout) {
            showLogoutAlert();
        } else if (id == R.id.nav_upevent) {
            fragment = new UpcomingEventsFragment();
            //toolbar.setTitle("FAVOURITES");
        } else if (id == R.id.nav_pasteevmt) {
            fragment = new PastEventsFragment();
        } else if (id == R.id.nav_tickets) {
            fragment = new PurchaseHistoryFragment();
        } else if (id == R.id.changePasswords) {
            fragment = new ChangePasswordFragment();

        }


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Are you Sure want to Logout");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                prefs.edit().clear().apply();
                //   showLogoutAlert();
                // postTokenRemoved();

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();


                Auth.GoogleSignInApi.signOut(mGoogleApiClient);


                Intent in = new Intent(HomeActivity.this, LoginActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                finish();
                dialog.cancel();


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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


/*
    private void postTokenRemoved() {

        loadingDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_LOGOUT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Logout", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String messagecode = jsonObject.getString("message_code");

                            if (messagecode.equalsIgnoreCase("1")) {
                                String message = jsonObject.getString("message");
                                Intent in = new Intent(HomeActivity.this, MainActivity.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(in);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.d("VolleyResponse", "Error: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @SuppressLint("LongLogTag")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("u_id", user_id);
                Log.e("Logout ", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);


    }
*/


    @SuppressLint("SetTextI18n")
    @Override
    public void stateChanged() {
        //getNotificationCount();

        user_id = prefs.getString(AppConstant.KEY_ID, null);
        profile_image = prefs.getString(AppConstant.KEY_IMAGE, null);
        user_name = prefs.getString(AppConstant.KEY_NAME, null);
        user_email = prefs.getString(AppConstant.KEY_EMAIL, null);

        textViewUserName.setText(user_name);
        textViewAdrress.setText(user_email);

        if (profile_image.equals("") || profile_image == null) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.img_no_pic_navigation)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .into(imageViewProfile);
            //Glide.with(getApplicationContext()).load(R.drawable.user_register).into(imageViewProfile);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(profile_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .dontAnimate()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            imageViewProfile.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            // linearLayoutBar.setVisibility(View.GONE);
                            imageViewProfile.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(imageViewProfile);
            // Glide.with(getApplicationContext()).load(profile_image).into(imageViewProfile);


            // Toast.makeText(this, "Notification call", Toast.LENGTH_SHORT).show();

        }
    }

/*
    private void getNotificationCount() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_NOTIFICATIONCOUNT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("NotificationCount", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int message_code = jsonObject.getInt("message_code");
                            String msg = jsonObject.getString("message");
                            String notification_count = jsonObject.getString("cnt");
                            Log.d("message_code:- ", String.valueOf(message_code));
                            if (message_code == 1) {
                                //Utils.AlertDialog(MainActivityUser.this, msg);
                                mCartItemCount = "";
                                if (notification_count.equals("0")) {
                                    mCartItemCount = "0";
                                    setupBadge();
                                } else {
                                    mCartItemCount = notification_count;
                                    setupBadge();
                                }

                            } else {
                                mCartItemCount = "0";
                                setupBadge();
                                // Utils.AlertDialog(MainActivityUser.this, msg);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        VolleyLog.d("VolleyResponse", "Error: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @SuppressLint("LongLogTag")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("u_id", user_id);
                Log.e("NotificationCount ", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }
*/

    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount.equals("0")) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(Integer.parseInt(mCartItemCount), 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }

    }

   /* @Override
    public void notificationStateChanged() {
        getNotificationCount();
    }*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}

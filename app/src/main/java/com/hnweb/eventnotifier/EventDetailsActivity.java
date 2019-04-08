package com.hnweb.eventnotifier;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hnweb.eventnotifier.bo.Event;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.utils.AlertUtility;
import com.hnweb.eventnotifier.utils.AppUtils;
import com.hnweb.eventnotifier.utils.ConnectionDetector;
import com.hnweb.eventnotifier.utils.LoadingDialog;
import com.hnweb.eventnotifier.utils.LocationSet;
import com.hnweb.eventnotifier.utils.MyLocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.hnweb.eventnotifier.utils.MainApplication.getContext;

public class EventDetailsActivity extends AppCompatActivity {
    ImageView iv_backButton;
    SharedPreferences sharedPreferences;
    Event events;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    ImageView iv_eventImage;
    TextView tv_eventName, tv_eventStartDate, tv_price, tv_eventStartTime, tv_eventEndDate, tv_eventEndTime, tv_description, tv_eventAddress;
    MyLocationListener myLocationListener;
    LocationSet locationSet = new LocationSet();
    double latitude = 0.0d;
    double longitude = 0.0d;
    private GoogleMap googleMap;
    private Circle mCircle;
    private Marker mMarker;
    Drawable drawable;
    ProgressBar progress_item;
    Dialog dialog;
    AlertDialog b;
    ImageView btn_share;
    Button iv_booknow;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    private int mYear, mMonth, mDay, mHour, mMinute;

    MapView mMapView;
    int minteger = 1;
    float event_total_price;
    String str_startTime, str_startDate, str_endTime, starttime, str_EventId,str_eventEndDate;

    String callFrom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        sharedPreferences = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("MyObject", "");
        events = gson.fromJson(json, Event.class);
        callFrom = sharedPreferences.getString("callFrom", "");
        drawable = ContextCompat.getDrawable(this, R.drawable.default_img_event_details);
        progress_item = (ProgressBar) findViewById(R.id.progress_item);


        myLocationListener = new MyLocationListener(this);
        if (locationSet.checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, this, this)) {
            fetchLocationData();
        } else {
            locationSet.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getContext(), this);
        }


        loadingDialog = new LoadingDialog(this);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 50, 180);


        tv_eventName = findViewById(R.id.tv_eventName);
        tv_eventStartDate = findViewById(R.id.tv_eventStartDate);
        tv_eventStartTime = findViewById(R.id.tv_eventStartTime);
        tv_eventEndDate = findViewById(R.id.tv_eventEndDate);
        tv_eventEndTime = findViewById(R.id.tv_eventEndTime);
        tv_description = findViewById(R.id.tv_description);
        tv_eventAddress = findViewById(R.id.tv_eventAddress);
        tv_price = findViewById(R.id.tv_price);
        iv_eventImage = findViewById(R.id.iv_eventImage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        iv_backButton = (ImageView) toolbar.findViewById(R.id.iv_backButton);
        iv_booknow = toolbar.findViewById(R.id.iv_booknow);
        btn_share = toolbar.findViewById(R.id.btn_share);

        if (callFrom.equals("1")) {
            iv_booknow.setVisibility(View.VISIBLE);
        } else {
            iv_booknow.setVisibility(View.GONE);

        }
        iv_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareIt();
            }
        });
        iv_booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogBook();
            }
        });
        connectionDetector = new ConnectionDetector(this);
        loadingDialog = new LoadingDialog(this);
        if (connectionDetector.isConnectingToInternet()) {
            getEventDetails();
        } else {
            Toast.makeText(this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }


    }

    private void getEventDetails() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_EVENTSDETAILS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("MessagesResponse" + response);
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);
                            if (message_code == 1) {
                                JSONArray userdetails = jobj.getJSONArray("response");
                                Log.d("ArrayLengthNails", String.valueOf(userdetails.length()));

                                for (int j = 0; j < userdetails.length(); j++) {
                                    JSONObject jsonObject = userdetails.getJSONObject(j);
                                    Event events = new Event();
                                    events.setId(jsonObject.getString("id"));
                                    events.setEvent_name(jsonObject.getString("event_name"));
                                    events.setEvent_date(jsonObject.getString("event_date"));
                                    events.setEvent_starttime(jsonObject.getString("event_starttime"));
                                    events.setEvent_endtime(jsonObject.getString("event_endtime"));
                                    events.setEvent_place(jsonObject.getString("event_place"));
                                    events.setEvent_descr(jsonObject.getString("event_descr"));
                                    events.setImage(jsonObject.getString("image"));
                                    events.setCreated_on(jsonObject.getString("created_on"));
                                    events.setLatitude(jsonObject.getString("latitude"));
                                    events.setLongitude(jsonObject.getString("longitude"));
                                    events.setAddress(jsonObject.getString("address"));
                                    events.setEvent_price(jsonObject.getString("price"));

                                    event_total_price = Float.valueOf(jsonObject.getString("price"));
                                    str_startTime = jsonObject.getString("event_starttime");
                                    str_endTime = jsonObject.getString("event_endtime");

                                    str_EventId = jsonObject.getString("id");
                                    String str_eventstartDate = jsonObject.getString("event_date");
                                    String str_event_endDate = jsonObject.getString("event_endDate");

                                    String pattern = "yyyy-MM-dd";

                                    SimpleDateFormat format1 = new SimpleDateFormat(pattern);
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");


                                    Date date = null;
                                    try {
                                        date = format1.parse(str_eventstartDate);
                                        str_startDate = formatter.format(date);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Date date2 = null;
                                    try {
                                        date2 = format1.parse(str_event_endDate);
                                        str_eventEndDate = formatter.format(date2);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    tv_eventStartDate.setText(str_startDate);
                                    tv_eventEndDate.setText(str_eventEndDate);


                                    try {
                                        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                                        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                                        Date time = sdf1.parse(str_startTime);
                                        Date endtime = sdf1.parse(str_endTime);
                                        System.out.println("MYTIME" + sdf2.format(time));
                                        starttime = sdf2.format(time);
                                        String endTime = sdf2.format(endtime);
                                        tv_eventStartTime.setText(starttime);
                                        tv_eventEndTime.setText(endTime);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                    googleMapView(events.getLatitude(), events.getLongitude(), events.getAddress());
                                    tv_eventName.setText(events.getEvent_name());
                                    tv_eventAddress.setText(events.getAddress());
                                    tv_description.setText(events.getEvent_descr());

                                    tv_price.setText("$" + events.getEvent_price());
                                    if (events.getImage().equals("")) {
                                        Glide.with(EventDetailsActivity.this).load(R.drawable.event_img_one).into(iv_eventImage);

                                    } else {

                                        try {
                                            Glide.with(EventDetailsActivity.this)
                                                    .load(events.getImage())
                                                    .error(drawable)
                                                    .centerCrop()
                                                    .crossFade()
                                                    .listener(new RequestListener<String, GlideDrawable>() {
                                                        @Override
                                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                            progress_item.setVisibility(View.GONE);
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                            progress_item.setVisibility(View.GONE);
                                                            return false;
                                                        }
                                                    })
                                                    .into(iv_eventImage);
                                        } catch (Exception e) {
                                            Log.e("Exception", e.getMessage());
                                        }
                                    }

                                }

                            } else {
                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
                                builder.setMessage(msg)
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
                        String reason = AppUtils.getVolleyError(EventDetailsActivity.this, error);
                        AlertUtility.showAlert(EventDetailsActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("event_id", events.getId());
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
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void fetchLocationData() {
        // check if myLocationListener enabled
        if (myLocationListener.canGetLocation()) {
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();

            //  Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            myLocationListener.showSettingsAlert();
            latitude = myLocationListener.getLatitude();
            longitude = myLocationListener.getLongitude();

            // Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        StringBuilder result = new StringBuilder();

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getAddressLine(1)).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void googleMapView(final String latitude, final String longitude, final String address) {


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(EventDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EventDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                /*    LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);*/
                double longitudeCurrent = Double.valueOf(longitude);
                double latitudeCurrent = Double.valueOf(latitude);
                Log.d("CurrentLocation", longitudeCurrent + " :: " + latitudeCurrent);

                   /* Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    String city;
                    String state;*/

                        /*addresses = geocoder.getFromLocation(longitudeCurrent, latitudeCurrent, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        city = addresses.get(0).getLocality();
                        state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        Log.d("Address", city + " :: " + state + " :: " + country + " :: " + postalCode + " :: " + knownName);*/
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.venue);
                LatLng lati_long_position = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                googleMap.addMarker(new MarkerOptions().position(lati_long_position).title(address)).setIcon(icon);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(lati_long_position).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                // For zooming automatically to the location of the marker

                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (mCircle == null || mMarker == null) {
                            drawMarkerWithCircle(latLng);
                        } else {
                            updateMarkerWithCircle(latLng);
                        }
                    }
                });

            }



                /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(getActivity(), "Clicked marker", Toast.LENGTH_SHORT).show();
                    }
                });*/

               /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        String marker_value = marker.getTitle();
                        Log.d("Title",marker_value);

                        String[] separated = marker_value.split(",");
                        String title = separated[0];
                        String user_id = separated[1];

                        Log.d("Title",marker_value);
                        Log.e("Title",title);
                        Log.d("Title",user_id);
                        Bundle bundle = new Bundle();
                        //fragment = new NailsFragment();
                        Fragment fragment = new BeauticianDetailsFragment();
                        bundle.putString("Nails","2");
                        fragment.setArguments(bundle);
                        changeFragment(fragment);
                        return false;
                    }
                });*/

        });

    }

    private void updateMarkerWithCircle(LatLng latLng) {
        mCircle.setCenter(latLng);
        mMarker.setPosition(latLng);
    }

    private void drawMarkerWithCircle(LatLng latLng) {
        double radiusInMeters = 3000.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = googleMap.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMarker = googleMap.addMarker(markerOptions);
    }

   /* public void dialogBook() {
        dialog = new Dialog(this,R.style.WideDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_fbook);
        //   final EditText et_exprydate = (EditText) dialog.findViewById(R.id.et_exprydate);

        dialog.show();
        dialog.setCancelable(true);


    }
*/

    public void dialogBook() {

        final TextView tv_date, tv_time;
        final AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(EventDetailsActivity.this);

        LinearLayout ll_time, ll_date;

        LayoutInflater inflater1 = getLayoutInflater();
        final View dialogView1 = inflater1.inflate(R.layout.dialog_fbook, null);
        final Button tv_totalprice = dialogView1.findViewById(R.id.tv_totalprice);
        Button btn_proceed = dialogView1.findViewById(R.id.btn_proceed);

        ll_time = dialogView1.findViewById(R.id.ll_time);
        ll_date = dialogView1.findViewById(R.id.ll_date);
        tv_date = dialogView1.findViewById(R.id.tv_date);
        tv_time = dialogView1.findViewById(R.id.tv_time);

        final TextView displayInteger = dialogView1.findViewById(
                R.id.integer_number);
        Button btn_decrease = dialogView1.findViewById(R.id.btn_decrease);
        Button btn_increase = dialogView1.findViewById(R.id.btn_increase);
        tv_totalprice.setText(String.valueOf(event_total_price));
        btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseInteger(displayInteger, tv_totalprice);
            }
        });
        btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseInteger(displayInteger, tv_totalprice);
            }
        });
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBookingDetails(str_startDate, starttime, tv_totalprice.getText().toString(), displayInteger.getText().toString());
            }
        });
        tv_date.setText(str_startDate);
        tv_time.setText(starttime);


        dialogBuilder1.setView(dialogView1);

    /*getOccassions(user_id);

    getChildList(user_id);*/


        b = dialogBuilder1.create();
        b.show();
    }


    public void increaseInteger(TextView displayInteger, TextView tv_totalprice) {
        minteger = minteger + 1;
        displayInteger.setText(String.valueOf(minteger));
        float total = event_total_price * minteger;
        tv_totalprice.setText(String.valueOf(total));

    }

    public void decreaseInteger(TextView displayInteger, TextView tv_totalprice) {
        if (minteger == 0) {

        } else {
            minteger = minteger - 1;
            displayInteger.setText(String.valueOf(minteger));
            float total = event_total_price * minteger;
            tv_totalprice.setText(String.valueOf(total));
           /* event_total_price = event_total_price * minteger;
            tv_totalprice.setText(String.valueOf(event_total_price));*/


        }


    }

    public void dialogBookingDetails(String date, String time, final String totalamount, String noOftickets) {

        final TextView tv_date, tv_time, tv_numberoftickets, tv_totalamount;
        Button btn_back, btn_pay;
        final AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(EventDetailsActivity.this);

        LayoutInflater inflater1 = getLayoutInflater();
        final View dialogView1 = inflater1.inflate(R.layout.dialog_fbookdetails, null);


        tv_date = dialogView1.findViewById(R.id.tv_date);
        tv_time = dialogView1.findViewById(R.id.tv_time);
        tv_numberoftickets = dialogView1.findViewById(R.id.tv_numberoftickets);
        tv_totalamount = dialogView1.findViewById(R.id.tv_totalamount);
        btn_back = dialogView1.findViewById(R.id.btn_back);
        btn_pay = dialogView1.findViewById(R.id.btn_pay);

        tv_date.setText(date);
        tv_time.setText(time);
        tv_numberoftickets.setText(noOftickets);
        tv_totalamount.setText("" + totalamount);

        dialogBuilder1.setView(dialogView1);

    /*getOccassions(user_id);

    getChildList(user_id);*/

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventDetailsActivity.this, PaymentActivity.class);


                i.putExtra("event_total_price", totalamount);
                i.putExtra("no_of_tickets", String.valueOf(minteger));
                i.putExtra("event_id", str_EventId);
                i.putExtra("event_price", String.valueOf(event_total_price));
                startActivity(i);
            }
        });
        b = dialogBuilder1.create();
        b.setCancelable(false);
        b.show();
    }

    private void shareIt() {
//sharing implementation here
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Event Notifier App");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Click here to Download Event Notifier App https://drive.google.com/file/d/1EPd-4HXnGsYSD6I5NsKNH_SmrPF-wnGG/view?usp=sharing\n ");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}

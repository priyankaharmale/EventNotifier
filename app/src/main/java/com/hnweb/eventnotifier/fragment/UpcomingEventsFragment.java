package com.hnweb.eventnotifier.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.eventnotifier.LoginActivity;
import com.hnweb.eventnotifier.R;
import com.hnweb.eventnotifier.adaptor.EventsAdaptor;
import com.hnweb.eventnotifier.bo.Event;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.utils.AlertUtility;
import com.hnweb.eventnotifier.utils.AppUtils;
import com.hnweb.eventnotifier.utils.ConnectionDetector;
import com.hnweb.eventnotifier.utils.LoadingDialog;
import com.hnweb.eventnotifier.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpcomingEventsFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerViewPostedList;
    SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    TextView textViewList;
    int position;
    private int mYear, mMonth, mDay, mHour, mMinute;

    AlertDialog b;
    RadioButton radioButton_All, radioButton_today, radioButton_Tommorrow, radioButton_weekend;
    ImageView imageViewFilter, imageViewSearch;

    LinearLayout linearLayout;
    SearchView searchView;
    String str_apply = "";
    ImageView mCloseButton;
    private ArrayList<Event> eventsArrayList = null;
    EventsAdaptor eventsAdaptor;
    LinearLayout ll_filter, ll_datepicker;
    Dialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        initViewById(view);
        return view;
    }

    private void initViewById(View view) {
        recyclerViewPostedList = view.findViewById(R.id.recylerview_posted_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewPostedList.setLayoutManager(layoutManager);

        textViewList = view.findViewById(R.id.textView_empty_list);
        ll_filter = view.findViewById(R.id.ll_filter);
        ll_datepicker = view.findViewById(R.id.ll_datepicker);
        imageViewFilter = view.findViewById(R.id.imageView_filter_booked);
        imageViewFilter.setOnClickListener(this);
        imageViewSearch = view.findViewById(R.id.imageView_search);

        radioButton_weekend = view.findViewById(R.id.radioButton_weekend);
        radioButton_All = view.findViewById(R.id.radioButton_All);
        radioButton_today = view.findViewById(R.id.radioButton_today);
        radioButton_Tommorrow = view.findViewById(R.id.radioButton_Tommorrow);

        imageViewSearch.setOnClickListener(this);
        ll_datepicker.setOnClickListener(this);

        linearLayout = view.findViewById(R.id.linearLayout_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = view.findViewById(R.id.searchView_my_task);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Add Text Change Listener to EditText
        mCloseButton = searchView.findViewById(R.id.search_close_btn);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.toString().trim().length() == 0) {
                    ///linearLayout.setVisibility(View.VISIBLE);
                    //searchView.setVisibility(View.GONE);
                    // mCloseButton.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                    mCloseButton.setVisibility(View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayout.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.GONE);
                        }
                    });
                }
                eventsAdaptor.getFilter().filter(query.toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(), "New Text "+newText, Toast.LENGTH_SHORT).show();
                if (newText.toString().trim().length() == 0) {
                    //linearLayout.setVisibility(View.VISIBLE);
                    //searchView.setVisibility(View.GONE);
                    mCloseButton.setVisibility(View.VISIBLE);
                    mCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayout.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.GONE);
                        }
                    });
                }

                try {
                    eventsAdaptor.getFilter().filter(newText.toString());

                } catch (NullPointerException e) {

                }

                return true;
            }
        });


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                linearLayout.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
                return false;
            }
        });

        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());
        if (connectionDetector.isConnectingToInternet()) {
            getEvents();
        } else {
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }
        ll_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFilter();
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_filter_booked:
                //        showAlertDialog();
                break;

            case R.id.imageView_search:
                linearLayout.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_datepicker:
                getDatePicker();

            default:
                break;
        }
    }

    private void getEvents() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_EVENTS,
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

                                recyclerViewPostedList.setVisibility(View.VISIBLE);
                                textViewList.setVisibility(View.GONE);
                                JSONArray userdetails = jobj.getJSONArray("response");
                                eventsArrayList = new ArrayList<Event>();
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
                                    events.setImage(jsonObject.getString("image"));
                                    events.setCreated_on(jsonObject.getString("created_on"));
                                    events.setEvent_price(jsonObject.getString("price"));

                                    eventsArrayList.add(events);
                                    Log.d("ArraySize", String.valueOf(eventsArrayList.size()));


                                }
                                eventsAdaptor = new EventsAdaptor(getActivity(), eventsArrayList, "1");
                                recyclerViewPostedList.setAdapter(eventsAdaptor);
                            } else {
                                recyclerViewPostedList.setVisibility(View.GONE);
                                textViewList.setVisibility(View.VISIBLE);
                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("No Events Available")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                recyclerViewPostedList.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void getTODAYEvents() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_TODAYEVENTS,
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
                                recyclerViewPostedList.setVisibility(View.VISIBLE);
                                textViewList.setVisibility(View.GONE);
                                JSONArray userdetails = jobj.getJSONArray("response");
                                eventsArrayList = new ArrayList<Event>();
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
                                    events.setImage(jsonObject.getString("image"));
                                    events.setCreated_on(jsonObject.getString("created_on"));
                                    events.setEvent_price(jsonObject.getString("price"));

                                    eventsArrayList.add(events);
                                    Log.d("ArraySize", String.valueOf(eventsArrayList.size()));


                                }
                                eventsAdaptor = new EventsAdaptor(getActivity(), eventsArrayList, "1");
                                recyclerViewPostedList.setAdapter(eventsAdaptor);
                            } else {
                                recyclerViewPostedList.setVisibility(View.GONE);
                                textViewList.setVisibility(View.VISIBLE);
                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("No Events Available")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                recyclerViewPostedList.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("VolleyError" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void getTommorrowEvents() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_TOMOOROWEVENTS,
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
                                recyclerViewPostedList.setVisibility(View.VISIBLE);
                                textViewList.setVisibility(View.GONE);
                                JSONArray userdetails = jobj.getJSONArray("response");
                                eventsArrayList = new ArrayList<Event>();
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
                                    events.setImage(jsonObject.getString("image"));
                                    events.setCreated_on(jsonObject.getString("created_on"));
                                    events.setEvent_price(jsonObject.getString("price"));

                                    eventsArrayList.add(events);
                                    Log.d("ArraySize", String.valueOf(eventsArrayList.size()));


                                }
                                eventsAdaptor = new EventsAdaptor(getActivity(), eventsArrayList, "1");
                                recyclerViewPostedList.setAdapter(eventsAdaptor);
                            } else {
                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("No Events Available")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                textViewList.setVisibility(View.VISIBLE);
                                recyclerViewPostedList.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    private void getWeekEndEvents() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_WEEKENDEVENTS,
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
                                recyclerViewPostedList.setVisibility(View.VISIBLE);
                                textViewList.setVisibility(View.GONE);
                                JSONArray userdetails = jobj.getJSONArray("response");
                                eventsArrayList = new ArrayList<Event>();
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
                                    events.setImage(jsonObject.getString("image"));
                                    events.setCreated_on(jsonObject.getString("created_on"));
                                    events.setEvent_price(jsonObject.getString("price"));

                                    eventsArrayList.add(events);
                                    Log.d("ArraySize", String.valueOf(eventsArrayList.size()));


                                }
                                eventsAdaptor = new EventsAdaptor(getActivity(), eventsArrayList, "1");
                                recyclerViewPostedList.setAdapter(eventsAdaptor);
                            } else {
                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("No Events Available")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                recyclerViewPostedList.setVisibility(View.GONE);
                                textViewList.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    public void dialogFilter() {

        TextView tv_cancle, tv_apply;
        final AlertDialog.Builder dialogBuilder1 = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater1 = getLayoutInflater();
        final View dialogView1 = inflater1.inflate(R.layout.dialog_filter, null);

        radioButton_weekend = dialogView1.findViewById(R.id.radioButton_weekend);
        radioButton_All = dialogView1.findViewById(R.id.radioButton_All);
        radioButton_today = dialogView1.findViewById(R.id.radioButton_today);
        radioButton_Tommorrow = dialogView1.findViewById(R.id.radioButton_Tommorrow);
        tv_cancle = dialogView1.findViewById(R.id.tv_cancle);
        tv_apply = dialogView1.findViewById(R.id.tv_apply);
        dialogBuilder1.setView(dialogView1);

    /*getOccassions(user_id);

    getChildList(user_id);*/

        tv_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (str_apply.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Please select the Filter", Toast.LENGTH_SHORT).show();
                } else if (str_apply.equalsIgnoreCase("All")) {
                    getEvents();
                    b.dismiss();
                } else if (str_apply.equalsIgnoreCase("Today")) {
                    getTODAYEvents();
                    b.dismiss();

                } else if (str_apply.equalsIgnoreCase("Tomorrow")) {
                    getTommorrowEvents();
                    b.dismiss();

                } else if (str_apply.equalsIgnoreCase("WeekEnd")) {
                    getWeekEndEvents();
                    b.dismiss();

                }
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
                getEvents();
            }
        });
        radioButton_All.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_apply = "All";
            }
        });
        radioButton_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_apply = "Today";
            }
        });
        radioButton_Tommorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_apply = "Tomorrow";
            }
        });
        radioButton_weekend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_apply = "WeekEnd";
            }
        });
        b = dialogBuilder1.create();
        b.show();
    }

    private void getDatePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        String list_of_count = String.format("%02d", (monthOfYear + 1));

                        String date = dayOfMonth + "-" + list_of_count + "-" + year;
                        String date2 = year + "-" + list_of_count + "-" + dayOfMonth;
                        Log.e("DateFormatChange", date2);

                      /*  DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                        Date date_format = null;
                        try {
                            date_format = inputFormat.parse(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateFormat = outputFormat.format(date_format);
                        Log.d("DateFormatClass", outputDateFormat);

*/
                        fiterByDate(date2);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

    }

    public void fiterByDate(final String outputDateFormat) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_EVENTSDATEBY,
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
                                recyclerViewPostedList.setVisibility(View.VISIBLE);
                                textViewList.setVisibility(View.GONE);

                                JSONArray userdetails = jobj.getJSONArray("response");
                                eventsArrayList = new ArrayList<Event>();
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
                                    events.setImage(jsonObject.getString("image"));
                                    events.setCreated_on(jsonObject.getString("created_on"));
                                    events.setEvent_price(jsonObject.getString("price"));

                                    eventsArrayList.add(events);
                                    Log.d("ArraySize", String.valueOf(eventsArrayList.size()));


                                }
                                eventsAdaptor = new EventsAdaptor(getActivity(), eventsArrayList, "1");
                                recyclerViewPostedList.setAdapter(eventsAdaptor);
                            } else {
                                recyclerViewPostedList.setVisibility(View.GONE);
                                textViewList.setVisibility(View.VISIBLE);
                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("selected_date", outputDateFormat);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}


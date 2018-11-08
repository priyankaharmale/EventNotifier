package com.hnweb.eventnotifier.fragment;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hnweb.eventnotifier.R;
import com.hnweb.eventnotifier.adaptor.EventsAdaptor;
import com.hnweb.eventnotifier.adaptor.PurchaseHistoryAdaptor;
import com.hnweb.eventnotifier.bo.Event;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.utils.AlertUtility;
import com.hnweb.eventnotifier.utils.AppUtils;
import com.hnweb.eventnotifier.utils.ConnectionDetector;
import com.hnweb.eventnotifier.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PurchaseHistoryFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerViewPostedList;
    SharedPreferences prefs;
    String user_id;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    TextView textViewList;
    int position;
    AlertDialog b;

    RadioButton radioButton_All, radioButton_today, radioButton_Tommorrow, radioButton_weekend;
    ImageView imageViewFilter, imageViewSearch;
    String value_date_filter = "";
    String replaceArrayListCategory = "";
    String category_id = "";
    LinearLayout linearLayout;
    SearchView searchView;
    String str_apply = "";
    ImageView mCloseButton;
    private ArrayList<Event> eventsArrayList = null;
    PurchaseHistoryAdaptor eventsAdaptor;
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
        prefs = getActivity().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);
        initViewById(view);
        return view;
    }

    private void initViewById(View view) {
        recyclerViewPostedList = view.findViewById(R.id.recylerview_posted_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewPostedList.setLayoutManager(layoutManager);

        textViewList = view.findViewById(R.id.textView_empty_list);
        ll_filter = view.findViewById(R.id.ll_filter);
        imageViewFilter = view.findViewById(R.id.imageView_filter_booked);
        imageViewFilter.setOnClickListener(this);
        imageViewSearch = view.findViewById(R.id.imageView_search);
        ll_datepicker = view.findViewById(R.id.ll_datepicker);
        radioButton_weekend = view.findViewById(R.id.radioButton_weekend);
        radioButton_All = view.findViewById(R.id.radioButton_All);
        radioButton_today = view.findViewById(R.id.radioButton_today);
        radioButton_Tommorrow = view.findViewById(R.id.radioButton_Tommorrow);

        imageViewSearch.setOnClickListener(this);
        ll_filter.setVisibility(View.GONE);
        ll_datepicker.setVisibility(View.GONE);
        linearLayout = view.findViewById(R.id.linearLayout_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = view.findViewById(R.id.searchView_my_task);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // Add Text Change Listener to EditText
        mCloseButton = searchView.findViewById(R.id.search_close_btn);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                eventsAdaptor.getFilter().filter(newText.toString());

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
                //dialogFilter();
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

            default:
                break;
        }
    }

    private void getEvents() {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_BOOKINGHISTORY,
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
                                    events.setNo_of_tickets(jsonObject.getString("no_of_tickets"));
                                    eventsArrayList.add(events);
                                    Log.d("ArraySize", String.valueOf(eventsArrayList.size()));


                                }
                                eventsAdaptor = new PurchaseHistoryAdaptor(getActivity(), eventsArrayList);
                                recyclerViewPostedList.setAdapter(eventsAdaptor);
                            } else {
                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("No data Available")
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
                    params.put("user_id", user_id);

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


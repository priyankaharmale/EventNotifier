package com.hnweb.eventnotifier.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hnweb.eventnotifier.NotificationUpdateModel;
import com.hnweb.eventnotifier.R;
import com.hnweb.eventnotifier.adaptor.NotificationListAdapter;
import com.hnweb.eventnotifier.bo.NotificationModel;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.utils.ConnectionDetector;
import com.hnweb.eventnotifier.utils.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class NotificationFragment extends Fragment {

    Toolbar toolbar;
    LoadingDialog loadingDialog;
    ConnectionDetector connectionDetector;
    SharedPreferences prefs;
    String user_id;
    ArrayList<NotificationModel> notificationModels = new ArrayList<NotificationModel>();
    NotificationListAdapter notificationListAdapter;
    RecyclerView recyclerViewListChat;
    TextView textViewEmpty;
    String messageFlag;

   /* @Override
    public void onResume() {
        super.onResume();
        getMessageList();
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message_notifi_list, container, false);
        //toolbar = ((MainActivityUser) getActivity()).toolbar;
        //toolbar.setTitle("MESSAGES");

        prefs = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);

        initViewById(view);

        connectionDetector = new ConnectionDetector(getActivity());
        loadingDialog = new LoadingDialog(getActivity());

        if (connectionDetector.isConnectingToInternet()) {
            getNotificationList();
            getNotificationMarkAsRead();
        } else {
          /*  Snackbar snackbar = Snackbar
                    .make(((MainActivityUser) getActivity()).coordinatorLayout, "No Internet Connection, Please try Again!!", Snackbar.LENGTH_LONG);

            snackbar.show();*/
            Toast.makeText(getActivity(), "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }


        return view;

    }

    private void getNotificationMarkAsRead() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_NOTIFICATIONMARKASREAD,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("MarkAsRead", response);
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int message_code = jsonObject.getInt("message_code");
                            String msg = jsonObject.getString("message");
                            Log.d("message_code:- ", String.valueOf(message_code));
                            if (message_code == 1) {
                                NotificationUpdateModel.getInstance().setmState(true);
                            } else {
                                NotificationUpdateModel.getInstance().setmState(true);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
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
                params.put("user_id", user_id);
                Log.e("MarkAsRead", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }

    private void initViewById(View view) {
        recyclerViewListChat = view.findViewById(R.id.recycler_view_message_chat_list);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerViewListChat.setLayoutManager(layoutManager);

        textViewEmpty = view.findViewById(R.id.textView_empty);
    }

    private void getNotificationList() {
        loadingDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, AppConstant.API_NOTIFICATIONLISTING,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("UpdateFacilityList", response);
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int message_code = jsonObject.getInt("message_code");
                            String msg = jsonObject.getString("message");
                            Log.d("message_code:- ", String.valueOf(message_code));
                            if (message_code == 1) {
                                recyclerViewListChat.setVisibility(View.VISIBLE);
                                JSONArray jsonArray = jsonObject.getJSONArray("details");
                                notificationModels.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObjectDetails = jsonArray.getJSONObject(i);

                                    NotificationModel notificationModel = new NotificationModel();
                                    notificationModel.setNotifi_id(jsonObjectDetails.getString("nid"));
                                    notificationModel.setNotifi_user_id(jsonObjectDetails.getString("u_id"));
                                    notificationModel.setNotifi_type(jsonObjectDetails.getString("type"));
                                    notificationModel.setNotifi_msg(jsonObjectDetails.getString("msg"));
                                    notificationModel.setNotifimy_task_id(jsonObjectDetails.getString("my_task_id"));
                                    notificationModel.setNotifi_from_id(jsonObjectDetails.getString("from_id"));
                                    notificationModel.setNotifi_created_date(jsonObjectDetails.getString("created_dt"));
                                    notificationModel.setNotifi_status(jsonObjectDetails.getString("status"));

                                    notificationModels.add(notificationModel);

                                }

                                notificationListAdapter = new NotificationListAdapter(getActivity(), notificationModels);
                                recyclerViewListChat.setAdapter(notificationListAdapter);
                                textViewEmpty.setVisibility(View.GONE);
                            } else {
                                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                //Utils.myToast(MessageChatActivity.this,"");
                                textViewEmpty.setVisibility(View.VISIBLE);
                                textViewEmpty.setText("No Data Found!!");
                                recyclerViewListChat.setVisibility(View.GONE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
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
                params.put("user_id", user_id);
                Log.e("NotificationParams", params.toString());
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        postRequest.setShouldCache(false);
        requestQueue.add(postRequest);
    }
}

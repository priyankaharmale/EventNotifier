package com.hnweb.eventnotifier.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by shree on 06-12-2016.
 */
public class RequestUtility {
    private final Context context;
    private OnDataCallbackListner listner;
    private RequestInfo request_info;

    public interface StateListner
    {
        public void  OnComplete(int request_id);
    }
    public RequestInfo getRequest() {
        return request_info;
    }

    public static void submitRequest(Context context, final RequestInfo request_info, final StateListner listner) {
        String url = request_info.getUrl();
        StringBuffer sb = new StringBuffer(url);
        if(request_info.getMethod() == RequestInfo.METHOD_GET) {
            Map<String, String> params = request_info.getParams();
            if (params != null) {
                if (params.size() > 0) {
                    sb.append("?");
                }
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sb.append(key + "=" + value + "&");
                }
                url = sb.toString();
            }
        }
        final StringRequest stringRequest = new StringRequest(request_info.getMethod(),url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
if(listner != null)
{
    listner.OnComplete(request_info.getID());
}

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if(request_info.getMethod() == RequestInfo.METHOD_POST)
                {
                    if (request_info.getParams() != null) {
                        return request_info.getParams();
                    }
                }
                return params;


            }

        };
// Add the request to the RequestQueue.
        String request_tag = "request"+ System.currentTimeMillis();
        MainApplication.getInstance().addToRequestQueue(stringRequest, request_tag);
    }


    public interface OnDataCallbackListner
    {
        public void OnDataReceived(String data);
        public void OnError(String message);
    }
    public void setCallbackListner(OnDataCallbackListner listner) {
        this.listner = listner;
    }
    public RequestUtility(Context context) {
        this.context = context;
        
    }




}

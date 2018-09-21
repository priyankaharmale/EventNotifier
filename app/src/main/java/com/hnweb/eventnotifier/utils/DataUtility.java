package com.hnweb.eventnotifier.utils;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Arshad on 06-12-2016.
 */
public class DataUtility {


    public static void submitRequest(LoadingDialog loadingDialog, Context context, final RequestInfo request_info, final boolean should_cache, final OnDataCallbackListner listner) {

        try {
            if (Utils.isNetworkAvailable(context) == true)
            {
                final CacheUtility cacheUtility = new CacheUtility(context);

                final String request_tag = request_info.getRequestTag()== null ? request_info.getUrl():request_info.getRequestTag();
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
                                    try {
                                        listner.OnDataReceived(response);
                                        if(should_cache)
                                        {
                                            cacheUtility.put(request_tag,response);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(isNetworkError(error))
                        {
                            if(should_cache) {
                                String cached = cacheUtility.get(request_tag);
                                if (cached != null) {
                                    if (listner != null) {
                                        listner.OnDataReceived(cached);
                                    }
                                } else {
                                    if (listner != null) {
                                        listner.OnError(error.getLocalizedMessage());
                                    }
                                }
                            }else
                            {
                                if (listner != null) {
                                    listner.OnError(error.getLocalizedMessage());
                                }
                            }
                        }else
                        {
                            if(listner != null)
                            {listner.OnError(error.getLocalizedMessage());

    //                        Log.i("Response message",error.getLocalizedMessage());
                            }
                        }
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

                MainApplication.getInstance().addToRequestQueue(stringRequest, request_tag);
            }
            else
            {
                Toast.makeText(context,"No Internet Connection", Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public interface OnDataCallbackListner
{
    public void OnDataReceived(String data);
    public void OnError(String message);
}

    private static boolean isNetworkError(VolleyError volleyError) {
        boolean have_error = false;

        if (volleyError instanceof NetworkError) {
            have_error = true;
        } else if (volleyError instanceof ServerError) {

        } else if (volleyError instanceof AuthFailureError) {

        } else if (volleyError instanceof ParseError) {

        } else if (volleyError instanceof NoConnectionError) {
            have_error = true;
        } else if (volleyError instanceof TimeoutError) {

        }
        return have_error;
    }

}

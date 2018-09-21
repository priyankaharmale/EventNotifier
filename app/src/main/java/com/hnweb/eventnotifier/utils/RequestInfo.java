package com.hnweb.eventnotifier.utils;

import com.android.volley.Request;

import java.util.Map;

/**
 * Created by shree on 06-12-2016.
 */
public class RequestInfo {
    public static final int METHOD_GET = Request.Method.GET;
    public static final int METHOD_POST = Request.Method.POST;
    private int method;
    private Map<String, String> params;
    private String url;
private String request_tag;
    private int request_id;

    public void setMethod(int method) {
        this.method = method;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public int getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String,String> getParams() {
        return params;
    }
    public void setRequestTag(String request_tag)
    {
        this.request_tag = request_tag;
    }
    public String getRequestTag() {
        return request_tag;
    }

    public void setRequestID(int request_id) {
        this.request_id = request_id;
    }

    public int getID() {
        return request_id;
    }
}

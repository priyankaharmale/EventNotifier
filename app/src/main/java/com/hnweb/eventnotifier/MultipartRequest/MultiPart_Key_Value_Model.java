package com.hnweb.eventnotifier.MultipartRequest;

import java.util.Map;

/**
 * Created by Hnweb on 6/21/2017.
 */

public class MultiPart_Key_Value_Model {
    public Map<String, String> Stringparams;
    public Map<String, String> Fileparams;
    public String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getStringparams() {
        return Stringparams;
    }

    public void setStringparams(Map<String, String> stringparams) {
        Stringparams = stringparams;
    }

    public Map<String, String> getFileparams() {
        return Fileparams;
    }

    public void setFileparams(Map<String, String> fileparams) {
        Fileparams = fileparams;
    }
}

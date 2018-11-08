package com.hnweb.eventnotifier.interfaces;

/**

 */

public interface OnCallBack {
    public void callback(String count);
    public void callbackYear(String count);
    public void callcountryList(String id, String name);
    public void callstateList(String id, String name);
    public void callcityList(String id, String name);
    public void callrefresh();

}

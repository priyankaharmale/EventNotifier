package com.hnweb.eventnotifier.contants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Priyanka H on 13/06/2018.
 */
public class AppConstant {


    /*============================================Register==================================================*/
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "u_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE = "phone_number";
    public static final String KEY_STREET = "u_street";
    public static final String KEY_CITY = "u_city";
    public static final String KEY_STATE = "u_state";
    public static final String KEY_COUNTRY = "u_country";
    public static final String KEY_ZIPCODE = "u_zipcode";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_IMAGE = "img";
    public static final String KEY_DEVICETYPE = "device_type";
    public static final String KEY_DEVICETOKEN = "device_token";

    public static final String BASE_URL = "http://viyra.com/viyra.com/johnman/event_notifier/index.php/api/Users_api/";

    /*******************************User***************************************************************************************************/
    /*=================================================Register User=========================================================*/
    public static final String API_REGISTER_USER = BASE_URL + "register";
    /*=================================================Login User=========================================================*/
    public static final String API_LOGIN_USER = BASE_URL + "login";
    /*=================================================Forgot Password User=========================================================*/
    public static final String API_FORGOTPWD_USER = BASE_URL + "forgotpassword";
    /*=================================================Login User Facebook=========================================================*/
    public static final String API_FACEBOOK_LOGIN_USER = BASE_URL + "facebook_login";
    /*=================================================Login User Twitter=========================================================*/
    public static final String API_TWITTER_LOGIN_USER = BASE_URL + "twitter_login";
    /*=================================================Login User Google=========================================================*/
    public static final String API_GOOGLE_LOGIN_USER = BASE_URL + "google_login";
    /*=================================================GET USER DETAILS=========================================================*/
    public static final String API_GET_USERDEATILS = BASE_URL + "profile";
    /*=================================================UPDATE USER DETAILS=========================================================*/
    public static final String API_UPDATE_USERDEATILS = BASE_URL + "updateprofile";
    /*=================================================ChANGE Password=========================================================*/
    public static final String API_CHANGE_PASSWORD = BASE_URL + "changepassword";
    /*=================================================UPCOming Events=========================================================*/
    public static final String API_GET_EVENTS = BASE_URL + "upcomming_event";
    /*=================================================Past Events=========================================================*/
    public static final String API_GET_PASTEVENTS = BASE_URL + "past_event";
    /*=================================================Today Events=========================================================*/
    public static final String API_GET_TODAYEVENTS = BASE_URL + "todaysevent";
    /*=================================================Tommorrow Events=========================================================*/
    public static final String API_GET_TOMOOROWEVENTS = BASE_URL + "tomarrowsevent";
    /*=================================================WeekEnd Events=========================================================*/
    public static final String API_GET_WEEKENDEVENTS = BASE_URL + "weekendsevent";
    /*=================================================Yesterday Events=========================================================*/
    public static final String API_GET_YEASTERDAYEVENTS = BASE_URL + "yesterdaysevent";
    /*=================================================LastWeekEnd Events=========================================================*/
    public static final String API_GET_LASTWEEKENDEVENTS = BASE_URL + "lastweekendsevent";
    /*=================================================Date By Events=========================================================*/
    public static final String API_GET_EVENTSDATEBY = BASE_URL + "selectedDatesevent";
    /*================================================= Events Details=========================================================*/
    public static final String API_GET_EVENTSDETAILS = BASE_URL + "get_event_details";
    /*================================================= Events Booking=========================================================*/
    public static final String API_GET_BOOKINGEVENT = BASE_URL + "event_booking";
    /*=================================================Booking History=========================================================*/
    public static final String API_GET_BOOKINGHISTORY= BASE_URL + "booking_history";

    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


}

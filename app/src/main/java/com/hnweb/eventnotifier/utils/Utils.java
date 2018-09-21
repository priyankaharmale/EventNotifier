package com.hnweb.eventnotifier.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.hnweb.eventnotifier.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * Created by Priyanka H on 13/06/2018.
 */
public class Utils {

    static String user_type;

    public static void intentCall(Context context, Class to) {

        Intent i = new Intent(context, to);
        context.startActivity(i);
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


    public static void myToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void myToast1(Context context) {
        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
    }


  /*  public static void myToast1(Context context)
    {
        Snackbar.make(context, "Network Error ", Snackbar.LENGTH_LONG)
                .setAction("ACTION",null).show();
    }
*/


    public static void logout(Context context) {
        final SharedPreferences settings = context.getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE); //1
        SharedPreferences.Editor editor = settings.edit();
        String isLogin = "";
        editor.putString("isLogin", isLogin);

        editor.putString("user_type", user_type);
        editor.commit();
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static String validateString(Context context, String st) {
        st.trim().replace(" ", "%20");
        return st;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static class DialogsUtils {
        public static ProgressDialog showProgressDialog(Context context, String text) {
            //ProgressDialog m_Dialog = new ProgressDialog(context);
            ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
            progressDialog.setMessage(text);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // m_Dialog.
            progressDialog.setCancelable(false);
            progressDialog.show();
            return progressDialog;
        }

    }

    public static String getDateMM_dd_yyyy(String msg_date, String tgifid) {
        String finalString = null;

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        try {
            date1 = (Date) formatter.parse(msg_date);

            Log.d("Message_date1", "original date1 of : " + tgifid + " is:" + date1);
            SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy");

            finalString = newFormat.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("Message_date1", "finalString of : " + tgifid + " is:" + finalString);

        return finalString;

    }


    public static String getDate_with_time(String msg_date) {
        long unixSeconds = Long.parseLong(msg_date);

        Date time = new Date((long) unixSeconds * 1000);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");

        String date = sdf.format(time);

        Log.d("Dateis", ": \t" + date);

        return date;

    }

    public static class AlertDialogsUtils {


        public static void showAlertDialog(final Context context, String message, final Class to) {


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);


            alertDialog.setMessage(message);


            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    intentCall(context, to);
                }
            });


//            alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//
//
//                }
//            });


            alertDialog.show();


        }

    }


    public static class AlertDialogsUtils1 {


        public static void showAlertDialog1(final Context context, String message, Drawable d) {


            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            alertDialog.setMessage(message);
            alertDialog.setIcon(d);

            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {


                }
            });


//            alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//
//
//                }
//            });


            alertDialog.show();


        }

    }

    public static class AlertDialogsUtils2 {


        public static void showAlertDialogfinish(final Context context, String message) {


            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);


            alertDialog.setMessage(message);


            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {


                }
            });


//            alertDialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//
//
//                }
//            });


            alertDialog.show();


        }

    }

    public static void AlertDialog(FragmentActivity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("UberCut")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.dismiss();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static boolean checkNull(String s) {
        return s != null;
    }

    public String dateFormats(String date, String input_format, String output_format){

        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = new SimpleDateFormat(input_format);
        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat(output_format);
        Date date_format = null;
        try {
            date_format = inputFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateFormat = outputFormat.format(date_format);
        Log.d("DateFormatClass", outputDateFormat);

        return outputDateFormat;

    }

    public String timeFormats(String time, String input_time_format, String output_time_format) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inFormat = new SimpleDateFormat(input_time_format);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outFormat = new SimpleDateFormat(output_time_format);
        String time_format = null;
        try {
            time_format = outFormat.format(inFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("TimeFormats", time_format);

        return time_format;
    }

}



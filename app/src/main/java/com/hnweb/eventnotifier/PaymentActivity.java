package com.hnweb.eventnotifier;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.eventnotifier.adaptor.MonthAdaptor;
import com.hnweb.eventnotifier.adaptor.YearAdaptor;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.interfaces.OnCallBack;
import com.hnweb.eventnotifier.utils.AlertUtility;
import com.hnweb.eventnotifier.utils.AppUtils;
import com.hnweb.eventnotifier.utils.ConnectionDetector;
import com.hnweb.eventnotifier.utils.LoadingDialog;
import com.hnweb.eventnotifier.utils.Utils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements OnCallBack {

    public static final String PUBLISHABLE_KEY = "pk_test_dC5cjS6xNiCLr68WRFjrV0HN";
    SharedPreferences prefs;
    String user_id, user_email_id;

    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    String total_price, event_price, noOfTicket, event_id;
    TextView textViewAmount;
    Button btnProcced, btn_clr;
    OnCallBack onCallBack;
    ImageView iv_backButton;
    String cvv_value = "", cardNumber = "", expiryMonth = "", expiryYear = "", expiryYearfourDigit = "";
    private EditText etCardNumber, etMonth, etExpiryDate, etCVV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();


        total_price = intent.getStringExtra("event_total_price");
        event_price = intent.getStringExtra("event_price");
        noOfTicket = intent.getStringExtra("no_of_tickets");
        event_id = intent.getStringExtra("event_id");

        onCallBack = this;
        prefs = getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        user_id = prefs.getString(AppConstant.KEY_ID, null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        iv_backButton = (ImageView) toolbar.findViewById(R.id.iv_backButton);
        iv_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        initViewById();
    }

    @SuppressLint("SetTextI18n")
    private void initViewById() {

        textViewAmount = findViewById(R.id.textView_total_price);

        textViewAmount.setText("$" + total_price);

        etCardNumber = (EditText) findViewById(R.id.et_cardNo);
        etMonth = (EditText) findViewById(R.id.et_mm);
        etExpiryDate = (EditText) findViewById(R.id.et_yyyy);
        etCVV = (EditText) findViewById(R.id.et_cvv);

        etCardNumber.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            boolean isDelete = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0)
                    isDelete = false;
                else
                    isDelete = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

                String source = editable.toString();
                int length = source.length();

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(source);

                if (length > 0 && length % 5 == 0) {
                    if (isDelete)
                        stringBuilder.deleteCharAt(length - 1);
                    else
                        stringBuilder.insert(length - 1, " ");

                    etCardNumber.setText(stringBuilder);
                    etCardNumber.setSelection(etCardNumber.getText().length());

                }
            }
        });

        etMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMonth();
            }
        });
        etExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogYear();
            }
        });
        btnProcced = findViewById(R.id.btn_proceed);
        btn_clr = findViewById(R.id.btn_clr);
        btnProcced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cvv_number = etCVV.getText().toString().trim();
                //  Log.d("CvvData", String.valueOf(get_cvv));
                /* *//* if (get_cvv == true) {
                    Log.d("CvvData", String.valueOf(cvv_value));
                    if (!cvv_number.matches("")) {
                        if (cvv_value.equals(cvv_number)) {
*//*
                            Log.e("cardDeatils", cardNumber + " : " + cvv_value + " :: " + expiryMonth + " :: " + expiryYearfourDigit);
                            getStripePaymentToken(cardNumber, expiryMonth, expiryYear, cvv_value, expiryYearfourDigit);
                        } else {
                            Toast.makeText(PaymentActivity.this, "Please Correct Card Details", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        etCVV.setError("Please Enter CVV Number!!");
                    }

                } else {*/
                //Toast.makeText(PaymentActivity.this, "Else False", Toast.LENGTH_SHORT).show();
                String strCardNumber = etCardNumber.getText().toString().trim();
                String strMonth = etMonth.getText().toString().trim();
                String strExpiryDate = etExpiryDate.getText().toString().trim();
                String strCVV = etCVV.getText().toString().trim();


                if (!strCardNumber.matches("")) {
                    if (!strMonth.matches("")) {
                        if (!strExpiryDate.matches("")) {
                            if (!strCVV.matches("")) {
                                String ste_year = strExpiryDate.substring(strExpiryDate.length() - 2);

                                getStripePaymentToken(strCardNumber, strMonth, ste_year, strCVV, strExpiryDate);
                            } else {
                                etCVV.setError("Please Enter CVV!!");
                            }
                        } else {
                            etExpiryDate.setError("Please Enter Expiry Date (MM/DD)!!");
                        }
                    } else {
                        etMonth.setError("Please Enter Month!!");
                    }
                } else {
                    etCardNumber.setError("Please Enter Card Number!!");
                }

            }
        });

        btn_clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etExpiryDate.setText("");
                etMonth.setText("");
                etCVV.setText("");
                etCardNumber.setText("");
            }
        });
        //textViewAmount.setText("$ " + serviceprice);

        connectionDetector = new ConnectionDetector(PaymentActivity.this);
        loadingDialog = new LoadingDialog(PaymentActivity.this);


    }


    private void getStripePaymentToken(final String cardNumber, final String expiryMonth, String expiryYear, final String cvv_value, final String expiryYearfourDigit) {
        loadingDialog.show();
        Log.e("PaymetDatas", cardNumber + " : " + expiryMonth + " :: " + expiryYear + " : " + cvv_value + " " + expiryYearfourDigit);
        Log.d("PaymetDatas", cardNumber + " " + cvv_value + " " + expiryYearfourDigit);

        final String strExpiryDate = expiryMonth + expiryYear;

        Card card = new Card(cardNumber,
                Integer.valueOf(expiryMonth),
                Integer.valueOf(expiryYear),
                cvv_value);

        Stripe stripe = new Stripe();

        stripe.createToken(card, PUBLISHABLE_KEY, new TokenCallback() {
            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                Log.d("StripeSuccessPayNow", token.getId());
                String token_id = token.getId();
                if (connectionDetector.isConnectingToInternet()) {
                    newStripePaymentAPIRequest(token_id);
                } else {
                    Toast.makeText(PaymentActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
                }

            }

            public void onError(Exception error) {

                Log.d("StripeFail", error.getLocalizedMessage());
                Toast.makeText(PaymentActivity.this, "Please Enter Correct Card Details!!", Toast.LENGTH_SHORT).show();
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
            }
        });
    }


    private void newStripePaymentAPIRequest(final String token_id) {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_BOOKINGEVENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("ResponsePayment", "Login= " + response);

                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");

                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);

                            if (message_code == 1) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                                builder.setTitle(msg)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                                dialog.dismiss();
                                                Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                Utils.AlertDialog(PaymentActivity.this, msg);
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(PaymentActivity.this, error);
                        AlertUtility.showAlert(PaymentActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("user_id", user_id);

                    params.put("stripeToken", token_id);
                    params.put("event_id", event_id);
                    params.put("event_price", event_price);
                    params.put("no_of_tickets", noOfTicket);
                    params.put("total_price", total_price);


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
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentActivity.this);
        requestQueue.add(stringRequest);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void dialogYear() {
        Dialog dialog = new Dialog(PaymentActivity.this);
        dialog.setContentView(R.layout.dialog_month);
        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Year");
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i <= 2060; i++) {
            years.add(Integer.toString(i));
        }
        YearAdaptor adapter = new YearAdaptor(PaymentActivity.this, years, onCallBack, dialog);
        lv.setAdapter(adapter);

        dialog.show();
    }

    public void dialogMonth() {
        Dialog dialog = new Dialog(PaymentActivity.this);
        dialog.setContentView(R.layout.dialog_month);
        ListView lv = (ListView) dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("Month");
        ArrayList<String> years = new ArrayList<String>();
        years.add("01");
        years.add("02");
        years.add("03");
        years.add("04");
        years.add("05");
        years.add("06");
        years.add("07");
        years.add("08");
        years.add("09");
        years.add("10");
        years.add("11");
        years.add("12");
        MonthAdaptor adapter = new MonthAdaptor(PaymentActivity.this, years, onCallBack, dialog);
        lv.setAdapter(adapter);
        dialog.show();
    }

    @Override
    public void callback(String count) {
        etMonth.setText(count);
    }

    @Override
    public void callbackYear(String count) {
        etExpiryDate.setText(count);

    }

    @Override
    public void callcountryList(String id, String name) {

    }

    @Override
    public void callstateList(String id, String name) {

    }

    @Override
    public void callcityList(String id, String name) {

    }

    @Override
    public void callrefresh() {

    }

}

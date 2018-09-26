package com.hnweb.eventnotifier;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.utils.AlertUtility;
import com.hnweb.eventnotifier.utils.AppUtils;
import com.hnweb.eventnotifier.utils.LoadingDialog;
import com.hnweb.eventnotifier.utils.Utils;
import com.hnweb.eventnotifier.utils.Validations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/* * Created by Priyanka H on 21/09/2018.
 */
public class RegistrationActivity extends AppCompatActivity {
    Button btn_register;
    TextView tv_login;
    EditText et_fullname, et_email, et_mobile, et_password, et_confrimpassword;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loadingDialog = new LoadingDialog(this);
        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);
        et_fullname = (EditText) findViewById(R.id.et_fullname);
        et_email = (EditText) findViewById(R.id.et_email);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_password = (EditText) findViewById(R.id.et_password);
        et_confrimpassword = (EditText) findViewById(R.id.et_confrimpassword);


        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidation()) {

                    if (Utils.isNetworkAvailable(RegistrationActivity.this)) {
                        String email = et_email.getText().toString();

                        if (!et_password.getText().toString().equals(et_confrimpassword.getText().toString())) {
                            Toast.makeText(RegistrationActivity.this, "Password Not matching ", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            register();
                        }
                    } else {
                        Utils.myToast1(RegistrationActivity.this);
                    }
                }

            }
        });
    }

    private boolean checkValidation() {

        boolean ret = true;
        if (!Validations.hasText(et_fullname, "Please Enter Name"))
            ret = false;
        if (!Validations.hasText(et_mobile, "Please Enter the Mobile No."))
            ret = false;
        if (!Validations.hasText(et_email, "Please Enter Email ID "))
            ret = false;
        if (!Validations.hasText(et_password, "Please Enter Password"))
            ret = false;
        if (!Validations.hasText(et_confrimpassword, "Please Enter Confirm Password"))
            ret = false;
        if (!Validations.isEmailAddress(et_email, true, "Please Enter Valid Email ID"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_password, "Password atleast 7 characters"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_confrimpassword, "Password atleast 7 characters"))
            ret = false;

        return ret;
    }

    private void register() {
        loadingDialog.show();
        final String password = et_password.getText().toString();
        final String email = et_email.getText().toString();
        final String phoneNo = et_mobile.getText().toString();
        final String name = et_fullname.getText().toString();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_REGISTER_USER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            final JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");
                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {


                                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(RegistrationActivity.this, error);
                        AlertUtility.showAlert(RegistrationActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("email_address", email);
                    params.put(AppConstant.KEY_NAME, name);
                    params.put(AppConstant.KEY_PHONE, phoneNo);
                    params.put(AppConstant.KEY_PASSWORD, password);

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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}

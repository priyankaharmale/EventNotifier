package com.hnweb.eventnotifier.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hnweb.eventnotifier.HomeActivity;
import com.hnweb.eventnotifier.R;
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

import static android.content.Context.MODE_PRIVATE;

public class ChangePasswordFragment extends Fragment  {

    EditText et_prepassword, et_newpassword, et_confirmpassword;
    Button btn_confirm;
    LoadingDialog loadingDialog;
    SharedPreferences sharedPreferences;
    String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_changepwd, container, false);

        //toolbar = ((MainActivityUser) getActivity()).toolbar;
        //toolbar.setTitle("MY TASKS");
        initViewById(view);

        return view;
    }

    private void initViewById(View view) {
        et_prepassword = (EditText) view.findViewById(R.id.et_prepassword);
        et_newpassword = (EditText) view.findViewById(R.id.et_newpassword);
        et_confirmpassword = (EditText) view.findViewById(R.id.et_confirmpassword);
        btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        userId = sharedPreferences.getString(AppConstant.KEY_ID, null);

        loadingDialog = new LoadingDialog(getContext());
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {

                    if (Utils.isNetworkAvailable(getActivity())) {

                        String oldpassword = et_prepassword.getText().toString();
                        String newPassword = et_newpassword.getText().toString();
                        String confirmNewPwd = et_confirmpassword.getText().toString();
                        if (newPassword.equals(confirmNewPwd)) {
                            submit(oldpassword, newPassword);

                        } else {
                            Toast.makeText(getActivity(), "Please Enter Confirm Password same as New Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Utils.myToast1(getActivity());
                    }
                }
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validations.hasText(et_prepassword, "Please Enter Old Password"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_prepassword, "Password atleast 7 characters"))
            ret = false;
        if (!Validations.hasText(et_newpassword, "Please Enter New Password"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_newpassword, "Password atleast 7 characters"))
            ret = false;
        if (!Validations.hasText(et_confirmpassword, "Please Enter Confirm New Password"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_confirmpassword, "Password atleast 7 characters"))
            ret = false;
        return ret;
    }
    private void submit(final String oldPassword, final String newPassword) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_CHANGE_PASSWORD,
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

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("old_password", oldPassword);
                    params.put("password", newPassword);
                    params.put("user_id", userId);

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

}

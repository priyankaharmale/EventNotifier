package com.hnweb.eventnotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hnweb.eventnotifier.utils.Utils;
import com.hnweb.eventnotifier.utils.Validations;

public class RegistrationActivity extends AppCompatActivity {
    Button btn_register;
    TextView tv_login;
    EditText et_fullname, et_email, et_mobile, et_password, et_confrimpassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
                        }
                        else
                        {
                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);


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
}

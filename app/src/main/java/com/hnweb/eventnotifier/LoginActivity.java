package com.hnweb.eventnotifier;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hnweb.eventnotifier.utils.Utils;
import com.hnweb.eventnotifier.utils.Validations;

public class LoginActivity extends AppCompatActivity {
    TextView tv_register;
    Button btn_login;
    EditText et_password, et_email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv_register = findViewById(R.id.tv_register);
        btn_login = findViewById(R.id.btn_login);
        et_password = (EditText) findViewById(R.id.et_password);
        et_email = (EditText) findViewById(R.id.et_email);

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation1()) {

                    if (Utils.isNetworkAvailable(LoginActivity.this)) {

                        String password = et_password.getText().toString();
                        String email = et_email.getText().toString();

                        // login(email, password);

                        Toast.makeText(LoginActivity.this, "Login ", Toast.LENGTH_SHORT).show();

                    } else {
                        Utils.myToast1(LoginActivity.this);
                    }
                }
            }
        });
    }

    private boolean checkValidation1() {
        boolean ret = true;
        if (!Validations.hasText(et_email, "Please Enter Email ID "))
            ret = false;
        if (!Validations.isEmailAddress(et_email, true, "Please Enter Valid Email ID"))
            ret = false;
        if (!Validations.hasText(et_password, "Please Enter RE-Enter Password"))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_password, "Password atleast 7 characters"))
            ret = false;
        return ret;
    }


}

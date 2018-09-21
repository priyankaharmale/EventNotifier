package com.hnweb.eventnotifier.utils;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Priyanka H on 13/06/2018.
 */

public class Validations {

    // Regular Expression
    // you can change the expression based on your need

    static String regex = "^[0-9]{5}(?:-[0-9]{4})?$";

    Context context;


/*^\d{5}([\-]?\d{4})?$*/


    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{3}-\\d{7}";

    // Error Messages
    private static final String REQUIRED_MSG = "required";
    private static final String EMAIL_MSG = "invalid email";
    private static final String PHONE_MSG = "###-#######";

    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required, String msg) {
        return isValidEmail(editText, EMAIL_REGEX, msg, required);
    }

    // call this method when you need to check email validation
    public static boolean isEmailAddress_input_layout(EditText editText, boolean required, String msg, TextInputLayout tt) {
        return isValidEmail_input_layout(editText, EMAIL_REGEX, msg, required, tt);
    }

    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText, boolean required, String msg) {


        return isValid(editText, PHONE_REGEX, msg, required);
    }

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText, "Please enter valid Telephone number")) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }
        ;

        return true;
    }


    public static boolean isValidEmail(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText, "Please enter valid Email id")) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);

            return false;
        }
        ;

        return true;
    }


    public static boolean isValidEmail_input_layout(EditText editText, String regex, String errMsg, boolean required, TextInputLayout tt) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);
        tt.setError(null);
        // text required and editText is blank, so return false
        if (required && !hasText_input_layout(editText, "Please enter Email id", tt)) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            // editText.setError(errMsg);
            tt.setError(errMsg);
            return false;
        }
        ;

        return true;
    }


    public static boolean isZipCode(EditText editText, String errMsg) {

        String zip = editText.getText().toString();

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(zip);

        System.out.println(matcher.matches());

        boolean res = matcher.matches();

        if (res == false) {

            editText.setError(errMsg);

            return false;
        }

        return true;
    }


    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText, String msg) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(msg);
            return false;
        }

        return true;
    }


    public static boolean hasText_input_layout(EditText editText, String msg, TextInputLayout tt) {

        String text = editText.getText().toString().trim();

        editText.setError(null);
        tt.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            //editText.setError(msg);
            tt.setError(msg);
            return false;
        }
        return true;
    }

    public static boolean hasText_textviewt(TextView editText, String msg) {

        String text = editText.getText().toString().trim();

        editText.setError(null);


        // length 0 means there is no text
        if (text.length() == 0) {
            //editText.setError(msg);
            editText.setError(msg);
            return false;
        }
        return true;
    }

    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError("Enter Keyword");
            return false;
        }

        return true;
    }


    public static boolean hasText_textview(TextView textview, String msg) {

        String text = textview.getText().toString().trim();
        //textview.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            // textview.setError(msg);


            return false;
        }

        return true;
    }


    public static boolean hasText_text_view(TextView textView, String msg) {

        String text = textView.getText().toString().trim();
        textView.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            textView.setError(msg);
            return false;
        }

        return true;
    }


    public static boolean check_text_length(EditText editText, TextInputLayout inputLayout) {

        boolean ret_value;
        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() < 4) {
            inputLayout.setError("Please Enter the Name atleast 4 Characters");
            ret_value = false;
            return ret_value;
        }

        ret_value = true;
        return ret_value;
    }


    public static boolean check_text_length_7_text_layout(EditText editText, String msg) {

        boolean ret_value;
        String text = editText.getText().toString().trim();
        editText.setError(null);
        // length 0 means there is no text
        if (text.length() < 7) {
            //editText.setError(msg);
            editText.setError(msg);
            ret_value = false;
            return ret_value;
        }

        ret_value = true;
        return ret_value;
    }

    public static boolean check_text_length_10(EditText editText, String msg, TextInputLayout tt) {

        String text = editText.getText().toString().trim();
        editText.setError(null);
        tt.setError(null);
        // length 0 means there is no text
        if (text.length() < 9) {
            //  editText.setError(msg);
            tt.setError(msg);
            return false;

        }

        return true;
    }


    public static boolean checkPassword(TextInputLayout et_new_password, TextInputLayout et_confirm_password, EditText et_new_psw, EditText et_confirm) {

        String password = et_new_psw.getText().toString();
        String confirm_password = et_confirm.getText().toString();

        if (!password.equals(confirm_password)) {
            et_confirm_password.setError("Password and Confirm Password must be same");
            return false;
        }


        return true;
    }

    public static boolean checkPasswordNew(EditText et_password, EditText et_confirm) {

        String password = et_password.getText().toString();
        String confirm_password = et_confirm.getText().toString();

        if (!password.equals(confirm_password)) {
            et_confirm.setError("Password and Confirm Password must be same");
            return false;
        }


        return true;
    }

    public static boolean checkPasswordNew_text_input_layout(EditText et_password, EditText et_confirm, TextInputLayout tt) {

        String password = et_password.getText().toString();
        String confirm_password = et_confirm.getText().toString();
        tt.setError(null);

        if (!password.equals(confirm_password)) {
            //  et_confirm.setError("Password and Confirm Password must be same");
            tt.setError("Password and Confirm Password must be same");

            return false;
        }


        return true;
    }
}

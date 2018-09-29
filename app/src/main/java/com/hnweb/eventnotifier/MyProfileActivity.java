package com.hnweb.eventnotifier;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hnweb.eventnotifier.contants.AppConstant;
import com.hnweb.eventnotifier.utils.AlertUtility;
import com.hnweb.eventnotifier.utils.AppUtils;
import com.hnweb.eventnotifier.utils.ConnectionDetector;
import com.hnweb.eventnotifier.utils.LoadingDialog;
import com.hnweb.eventnotifier.utils.Validations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String userId;
    ConnectionDetector connectionDetector;
    LoadingDialog loadingDialog;
    EditText et_firstName, et_emailName, et_MobileNo, et_password;
    String userfirstName, userLastName, userEmailId, userMobileNo, userPhoto;
    ImageView iv_profilePic, iv_user_camerabutton;
    ProgressBar progressBar;
    private int GALLERY = 1, CAMERA = 2;
    public static final int REQUEST_CAMERA = 5;
    public static File destination;
    protected static final int REQUEST_STORAGE_ACCESS_PERMISSION = 102;
    String camImage, imagePath12;
    ImageView iv_editprofile;
    String isclick = "1";
    Button button_update_profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        sharedPreferences = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
        userId = sharedPreferences.getString(AppConstant.KEY_ID, null);

        et_firstName = (EditText) findViewById(R.id.et_name);
        et_emailName = (EditText) findViewById(R.id.et_email_id);
        et_MobileNo = (EditText) findViewById(R.id.et_phone_no);
        iv_profilePic = (ImageView) findViewById(R.id.profile_image_edit);
        iv_user_camerabutton = (ImageView) findViewById(R.id.profile_image_edit_upload);

        button_update_profile = findViewById(R.id.button_update_profile);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_nav);


        connectionDetector = new ConnectionDetector(MyProfileActivity.this);
        loadingDialog = new LoadingDialog(MyProfileActivity.this);
        if (connectionDetector.isConnectingToInternet()) {
            getUserDetails();
        } else {

            Toast.makeText(MyProfileActivity.this, "No Internet Connection, Please try Again!!", Toast.LENGTH_SHORT).show();
        }

        iv_user_camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        button_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidation()) {

                    if (connectionDetector.isConnectingToInternet()) {
                        //    updateUserData(camImage);
                    } else {
                        Toast.makeText(MyProfileActivity.this, "Please Connect to internet", Toast.LENGTH_LONG).show();
                        // }
                    }
                }


            }


        });
    }

    private void getUserDetails() {
        loadingDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_GET_USERDEATILS,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("MessagesResponse" + response);
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.i("Response", "MessagesResponse= " + response);
                        try {
                            JSONObject jobj = new JSONObject(response);
                            int message_code = jobj.getInt("message_code");
                            String msg = jobj.getString("message");
                            Log.e("FLag", message_code + " :: " + msg);
                            if (message_code == 1) {
                                JSONObject jsonObject = jobj.getJSONObject("response");
                                jsonObject.getString("user_id");
                                userEmailId = jsonObject.getString("email_address");
                                userfirstName = jsonObject.getString("name");
                                userMobileNo = jsonObject.getString("phone_number");
                                userPhoto = jsonObject.getString("profile_photo");

                                et_emailName.setText(userEmailId);
                                et_firstName.setText(userfirstName);
                                et_MobileNo.setText(userMobileNo);


                                if (userPhoto.equals("")) {
                                    Glide.with(MyProfileActivity.this).load(R.drawable.img_navigation).into(iv_profilePic);
                                } else {
                                    Glide.with(MyProfileActivity.this).load(userPhoto).into(iv_profilePic);
                                }


                                SharedPreferences prefUser = getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
                                SharedPreferences.Editor editorUser = prefUser.edit();
                                //  editorUser.putString(AppConstant.KEY_PHOTO, userPhoto);
                                editorUser.commit();


                            } else {
                                msg = jobj.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                                builder.setMessage(msg)
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
                        String reason = AppUtils.getVolleyError(MyProfileActivity.this, error);
                        AlertUtility.showAlert(MyProfileActivity.this, reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
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
        RequestQueue requestQueue = Volley.newRequestQueue(MyProfileActivity.this);
        requestQueue.add(stringRequest);

    }

/*
    public void updateUserData(String camImage) {
        loadingDialog.show();

        MultiPart_Key_Value_Model OneObject = new MultiPart_Key_Value_Model();
        Map<String, String> fileParams = new HashMap<>();
        if (camImage == null) {
            //  fileParams.put("profile_pic", "");

        } else {
            fileParams.put("rphoto", camImage);

        }

        System.out.println("priya Op" + camImage);

        Map<String, String> stringparam = new HashMap<>();

        stringparam.put(AppConstant.KEY_ID, userId);
        stringparam.put("rfname", et_firstName.getText().toString());
        stringparam.put("rlname", et_lastName.getText().toString());
        stringparam.put("rphone", et_MobileNo.getText().toString());
        stringparam.put("remail", et_emailName.getText().toString());

        OneObject.setUrl(AppConstant.API_USERUPDATE);
        OneObject.setFileparams(fileParams);
        System.out.println("file" + fileParams);
        System.out.println("UTL" + OneObject.toString());
        OneObject.setStringparams(stringparam);
        System.out.println("string" + stringparam);

        MultipartFileUploaderAsync someTask = new MultipartFileUploaderAsync(this, OneObject, new OnEventListener<String>() {
            @Override
            public void onSuccess(String object) {
                loadingDialog.dismiss();
                System.out.println("Result" + object);
                //    Toast.makeText(getActivity(), "ress" + object, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObject1response = new JSONObject(object);
                    int flag = jsonObject1response.getInt("message_code");

                    if (flag == 1) {


                        String message = jsonObject1response.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();


                                et_firstName.setEnabled(false);
                                et_lastName.setEnabled(false);
                                et_MobileNo.setEnabled(false);
                                iv_editprofile.setImageResource(R.drawable.edit_my_profile);
                                isclick = "1";
                                getUserDetails();

                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        String message = jsonObject1response.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("JSONException" + e);
                }
            }


            @Override
            public void onFailure(Exception e) {
                System.out.println("onFailure" + e);

            }
        });
        someTask.execute();
        return;
    }
*/

    private void showPictureDialog() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(MyProfileActivity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                isPermissionGrantedImageGallery();
                                break;
                            case 1:
                                isPermissionGrantedImage();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void isPermissionGrantedImageGallery() {

        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(MyProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            choosePhotoFromGallary();
        }

    }

    public void isPermissionGrantedImage() {
        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(MyProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            camerImage();
        }

    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MyProfileActivity.this, permission)) {
            new android.app.AlertDialog.Builder(MyProfileActivity.this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MyProfileActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(MyProfileActivity.this, new String[]{permission}, requestCode);
        }
    }

    public void camerImage() {
        System.out.println("Click Image11");
        String name = AppConstant.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".png");


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(MyProfileActivity.this, getApplicationContext().getPackageName() + ".my.package.name.provider", destination));
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    // Log.i("Path", imagePath12);
                    FileOutputStream fo;
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    imagePath12 = destination.getAbsolutePath();
                    camImage = imagePath12;

                    try {
                        Glide.with(MyProfileActivity.this)
                                .load(camImage)
                                .error(R.drawable.img_navigation)
                                .centerCrop()
                                .crossFade()
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(iv_profilePic);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MyProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == REQUEST_CAMERA) {
            System.out.println("REQUEST_CAMERA");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            String imagePath = destination.getAbsolutePath();
            Log.i("Path", imagePath);
            camImage = imagePath;
            Toast.makeText(MyProfileActivity.this, camImage, Toast.LENGTH_SHORT).show();
            try {
                Glide.with(MyProfileActivity.this)
                        .load(camImage)
                        .error(R.drawable.img_navigation)
                        .centerCrop()
                        .crossFade()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(iv_profilePic);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validations.hasText(et_firstName, "Please Enter Name"))
            ret = false;
        if (!Validations.hasText(et_MobileNo, "Please Enter Contact Number"))
            ret = false;
        return ret;
    }

}

package com.hnweb.eventnotifier.MultipartRequest;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This program demonstrates a usage of the MultipartUtility class.
 *
 * @author www.codejava.net
 */
public class MultipartFileUploaderAsync extends AsyncTask<Void, Void, String> {

    private OnEventListener<String> mCallBack;
    private Context mContext;
    public Exception mException;
    public MultiPart_Key_Value_Model mu;


    public MultipartFileUploaderAsync(Context applicationContext, MultiPart_Key_Value_Model oneObject, OnEventListener<String> onEventListener) {
        mCallBack = onEventListener;
        mContext = applicationContext;
        mu = oneObject;
    }


    @Override
    protected String doInBackground(Void... params) {

        String res = null;
        String charset = "UTF-8";
        try {
            // todo try to do something dangerous


            try {
                MultipartUtility multipart = new MultipartUtility(mu.getUrl(), charset);

                multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");


                Map<String, String> string = mu.getStringparams();
                if (string != null) {
                    if (string.size() > 0) {

                    }
                    for (Map.Entry<String, String> entry : string.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        multipart.addFormField(key, value);

                        System.out.println("string key value" + key + value);
//                            sb.append(key + "=" + value + "&");
                    }

                }
                Map<String, String> file = mu.getFileparams();
                if (file != null) {
                    if (file.size() > 0) {

                    }
                    for (Map.Entry<String, String> entry : file.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        File f = new File(value);
                        multipart.addFilePart(key, f);
                        System.out.println("file key value" + key + value);
//                            sb.append(key + "=" + value + "&");
                    }

                }


                List<String> response = multipart.finish();

                System.out.println("SERVER REPLIED:");

                for (String line : response) {
                    System.out.println("SERVER REPLIED:sdff" + line);
                    res = line;
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }

            return res;

        } catch (Exception e) {
            mException = e;
        }

        return res;
    }

    @Override
    protected void onPostExecute(String result) {

        System.out.println("sfsafsdfsagdsg" + result);
        if (mCallBack != null) {

            if (mException == null) {
                mCallBack.onSuccess(result);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }


}

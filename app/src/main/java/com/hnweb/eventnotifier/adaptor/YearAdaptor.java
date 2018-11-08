package com.hnweb.eventnotifier.adaptor;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.hnweb.eventnotifier.R;
import com.hnweb.eventnotifier.interfaces.OnCallBack;

import java.util.ArrayList;


public class YearAdaptor extends BaseAdapter {
    Context context;
    ArrayList<String> logos;
    LayoutInflater inflter;
    TextView tv_cost;
    OnCallBack onCallBack;
    Dialog dialog;


    public YearAdaptor(Context applicationContext, ArrayList<String> logos, OnCallBack onCallBack, Dialog dialog) {
        this.context = applicationContext;
        this.logos = logos;
        inflter = (LayoutInflater.from(applicationContext));
        this.onCallBack = onCallBack;
        this.dialog = dialog;
    }

    @Override
    public int getCount() {
        return logos.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.adaptor_yearlist, null); // inflate the layout
        TextView textView = (TextView) view.findViewById(R.id.tv_year);
        textView.setText(logos.get(i)); // set logo images
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, logos.get(i),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                fetchcount(logos.get(i));

            }
        });
        return view;
    }

    private void fetchcount(String count) {
        onCallBack.callbackYear(count);
    }
}

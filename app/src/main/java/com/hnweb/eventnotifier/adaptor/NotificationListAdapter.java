package com.hnweb.eventnotifier.adaptor;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hnweb.eventnotifier.R;
import com.hnweb.eventnotifier.bo.NotificationModel;
import com.hnweb.eventnotifier.utils.Utils;


import java.util.ArrayList;
import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private Context context;
    private List<NotificationModel> notificationModelList;
    private LayoutInflater inflater;

    public NotificationListAdapter(FragmentActivity activity, ArrayList<NotificationModel> notificationModelList) {

        this.context = activity;
        this.notificationModelList = notificationModelList;
        this.inflater = LayoutInflater.from(context);


    }

    @Override
    public NotificationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_notification_list, parent, false);
        NotificationListAdapter.ViewHolder vh = new NotificationListAdapter.ViewHolder(rowView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final NotificationListAdapter.ViewHolder holder, final int i) {
        final NotificationModel chatModelList = notificationModelList.get(i);

        holder.tvNotificationMsg.setText(chatModelList.getNotifi_msg());
        String chat_date_time = chatModelList.getNotifi_created_date();
        Log.e("DateFormatBook", chat_date_time);

        Utils util = new Utils();
        String input_date_format = "yyyy-MM-dd HH:mm:ss";
        String output_date_format = "HH:mm aa, dd MMM yyyy";
        String outputDateTimeFormat = util.dateFormats(chat_date_time, input_date_format, output_date_format);
        Log.d("DateFormatBook", outputDateTimeFormat);

        holder.tvChatDateTime.setText(outputDateTimeFormat);



        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u_id = chatModelList.getChat_u_id();
                String message_flag = chatModelList.getMessage_flag();
                Intent intent = new Intent(context, MessageChatActivity.class);
                intent.putExtra("Beautician_id", u_id);
                intent.putExtra("MessageFlag",message_flag);
                context.startActivity(intent);

            }
        });*/
    }


    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNotificationMsg, tvChatDateTime;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNotificationMsg = itemView.findViewById(R.id.textView_notification);
            tvChatDateTime = itemView.findViewById(R.id.textView_msg_date_time);

        }
    }
}



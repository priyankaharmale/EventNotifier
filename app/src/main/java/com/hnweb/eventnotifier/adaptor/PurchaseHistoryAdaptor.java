package com.hnweb.eventnotifier.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.hnweb.eventnotifier.EventDetailsActivity;
import com.hnweb.eventnotifier.R;
import com.hnweb.eventnotifier.bo.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PurchaseHistoryAdaptor extends RecyclerView.Adapter<PurchaseHistoryAdaptor.ViewHolder> implements Filterable {
    private Context context;
    private List<Event> eventsList;
    private LayoutInflater inflater;
    Drawable drawable;
    private List<Event> mFilteredList;
    public MyFilter mFilter;
    String str_startDate, eventStartTime;

    public PurchaseHistoryAdaptor(Context context, List<Event> data) {
        this.context = context;
        this.eventsList = data;
        this.mFilteredList = new ArrayList<Event>();

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public PurchaseHistoryAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = inflater.inflate(R.layout.adaptor_puchasehdemo, parent, false);
        PurchaseHistoryAdaptor.ViewHolder vh = new PurchaseHistoryAdaptor.ViewHolder(rowView);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final PurchaseHistoryAdaptor.ViewHolder holder, final int i) {
        final Event details = eventsList.get(i);

        Log.e("Data", details.toString());
        String messageId = eventsList.get(i).getId();
        String eventTitle = eventsList.get(i).getEvent_name();
        String eventDateTime = eventsList.get(i).getCreated_on();
        String eventDate = eventsList.get(i).getEvent_date();
        String eventStartTime = eventsList.get(i).getEvent_starttime();
        String eventEndTime = eventsList.get(i).getEvent_endtime();
        String evetPhoto = eventsList.get(i).getImage();
        String eventLocation = eventsList.get(i).getEvent_place();
        String eventPrice = eventsList.get(i).getTotal_price();
        String noOfTickets = eventsList.get(i).getNo_of_tickets();
        String transaction_date = eventsList.get(i).getTransaction_date();

        drawable = ContextCompat.getDrawable(context, R.drawable.default_img_event);

        if (eventTitle.equals("")) {
            holder.textViewEventTitle.setText("No Name");
        } else {
            holder.textViewEventTitle.setText(eventsList.get(i).getEvent_name());
        }

        if (noOfTickets.equals("")) {
            holder.tv_NoOfTickets.setText("-");

        } else {
            holder.tv_NoOfTickets.setText(eventsList.get(i).getNo_of_tickets() + " Tickets");

        }

     /*   if (eventDate.equals("")) {
            holder.textViewDate.setText(" - ");
        } else {

            String pattern = "yyyy-MM-dd";

            SimpleDateFormat format1 = new SimpleDateFormat(pattern);
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");


            Date date = null;
            try {
                date = format1.parse(eventDate);
                str_startDate = formatter.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            holder.textViewDate.setText(str_startDate);
        }
        if (eventStartTime.equals("")) {
            holder.textViewTime.setText(" - ");
        } else {

            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                Date time = sdf1.parse(eventStartTime);
                String endTime = sdf2.format(time);

                holder.textViewTime.setText(endTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
*/

        if (transaction_date.equals("")) {
            holder.textViewDate.setText(" - ");
        } else {

            String pattern = "yyyy-MM-dd";

            SimpleDateFormat format1 = new SimpleDateFormat(pattern);
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");


            Date date = null;
            try {
                date = format1.parse(transaction_date);
                str_startDate = formatter.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }


            holder.textViewDate.setText(str_startDate);
        }
        if (transaction_date.equals("")) {
            holder.textViewTime.setText(" - ");
        } else {

            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                Date time = sdf1.parse(transaction_date);
                String endTime = sdf2.format(time);

                holder.textViewTime.setText(endTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        if (evetPhoto.equals("")) {
            Glide.with(context).load(R.drawable.event_img_one).into(holder.iv_eventimage);

        } else {

            try {
                Glide.with(context)
                        .load(evetPhoto)
                        .error(drawable)
                        .centerCrop()
                        .crossFade()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                holder.progress_item.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                holder.progress_item.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.iv_eventimage);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }

        if (eventPrice.equalsIgnoreCase("")) {
            holder.tv_eventPrice.setText("-");
        } else {
            holder.tv_eventPrice.setText("$" + eventPrice);

        }

        if (eventLocation.equals("")) {
            holder.tv_eventAddress.setText(" - ");
        } else {
            holder.tv_eventAddress.setText(eventLocation);
        }
    /*    holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                SharedPreferences prefUser = context.getApplicationContext().getSharedPreferences("AOP_PREFS", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefUser.edit();
                Gson gson = new Gson();
                String json = gson.toJson(details);
                prefsEditor.putString("MyObject", json);
                prefsEditor.commit();
                context.startActivity(intent);
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewEventTitle, textViewDate, textViewTime, tv_eventendDt, tv_eventAddress, tv_eventPrice, tv_NoOfTickets;
        ImageView iv_eventimage;
        ProgressBar progress_item;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewEventTitle = itemView.findViewById(R.id.tv_eventName);
            textViewDate = itemView.findViewById(R.id.tv_eventDate);
            textViewTime = itemView.findViewById(R.id.tv_eventTime);
            progress_item = (ProgressBar) itemView.findViewById(R.id.progress_item);
            tv_eventAddress = itemView.findViewById(R.id.tv_eventAddress);
            tv_eventPrice = itemView.findViewById(R.id.tv_eventPrice);
            tv_NoOfTickets = itemView.findViewById(R.id.tv_NoOfTickets);
            iv_eventimage = itemView.findViewById(R.id.iv_eventImage);
        }
    }

    @Override
    public Filter getFilter() {

        if (mFilter == null) {
            mFilteredList.clear();
            mFilteredList.addAll(this.eventsList);
            mFilter = new MyFilter(this, mFilteredList);
        }
        return mFilter;

    }

    private static class MyFilter extends Filter {

        private final PurchaseHistoryAdaptor myAdapter;
        private final List<Event> originalList;
        private final List<Event> filteredList;

        private MyFilter(PurchaseHistoryAdaptor myAdapter, List<Event> originalList) {
            this.myAdapter = myAdapter;
            this.originalList = originalList;
            this.filteredList = new ArrayList<Event>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Event leadsModel : originalList) {
                    if (leadsModel.getEvent_name().toLowerCase().contains(filterPattern) || leadsModel.getEvent_place().toLowerCase().contains(filterPattern)) {
                        filteredList.add(leadsModel);
                        Log.d("FilterData", filteredList.toString());
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            myAdapter.eventsList.clear();
            myAdapter.eventsList.addAll((ArrayList<Event>) filterResults.values);
            myAdapter.notifyDataSetChanged();

        }
    }
}

package com.htp.chat.adapter;

/*
 * Created by faozi on 01/02/18.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htp.chat.R;
import com.htp.chat.StaticVars;
import com.htp.chat.modal.modChat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter {

    ArrayList<modChat> items;
    Activity act;
    public modChat mc;

    private final int VIEW_ITEM = 1;
    private int lastPosition = -1;
    SharedPreferences spUser;

    public ChatAdapter(Activity acts, ArrayList<modChat> data) {
        act = acts;
        items = data;
        spUser = act.getSharedPreferences(StaticVars.SP_USER, Context.MODE_PRIVATE);
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public static class BrandViewHolder extends RecyclerView.ViewHolder {

        TextView tPostDate, tName, tChatting;
        RelativeLayout rlMainChat;
        LinearLayout llChat;

        BrandViewHolder(View v) {
            super(v);

            tPostDate = v.findViewById(R.id.tPostDate);
            tName = v.findViewById(R.id.tName);
            tChatting = v.findViewById(R.id.tChatting);
            rlMainChat = v.findViewById(R.id.rlMainChat);
            llChat = v.findViewById(R.id.llChat);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_chat, parent, false);

            vh = new ChatAdapter.BrandViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof ChatAdapter.BrandViewHolder) {

            mc = items.get(position);

            String userId = spUser.getString(StaticVars.SP_USER_ID,"");

            SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
            String date = df.format(mc.getPostDate());
            ((BrandViewHolder) holder).tPostDate.setText(getFormattedDate(mc.getPostDate().getTime()));
            ((BrandViewHolder) holder).tName.setText(mc.getName());
            ((BrandViewHolder) holder).tChatting.setText(mc.getDescriptionChat());
            if (mc.getUserId().equals(userId)) {
                ((BrandViewHolder) holder).rlMainChat.setGravity(Gravity.END);
                ((BrandViewHolder) holder).llChat.setBackgroundColor(act.getResources().getColor(R.color.colorPrimary));
            } else {
                ((BrandViewHolder) holder).rlMainChat.setGravity(Gravity.START);
                ((BrandViewHolder) holder).llChat.setBackgroundColor(act.getResources().getColor(R.color.colorAccent));
            }
//            ((BrandViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    act.startActivity(new Intent(act, DetailLaporan.class));
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm"; // Format 24 Hours
//        final String timeFormatString = "hh:mm aa"; // Format AM and PM
        final String dateTimeFormatString = "EEEE, MMMM d, HH:mm";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Hari ini " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Kemarin " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMMM yyyy, HH:mm", smsTime).toString();
        }
    }

}

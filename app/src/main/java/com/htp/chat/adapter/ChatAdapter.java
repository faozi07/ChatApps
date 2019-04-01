package com.htp.chat.adapter;

/*
 * Created by faozi on 01/02/18.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htp.chat.R;
import com.htp.chat.StaticVars;
import com.htp.chat.modal.modChat;

import java.util.ArrayList;
import java.util.Calendar;


public class ChatAdapter extends RecyclerView.Adapter {

    private ArrayList<modChat> items;
    private Activity act;
    private modChat mc;

    private final int VIEW_ITEM = 1;
    private SharedPreferences spUser;

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

        TextView tPostDate, tName, tChatting, tTheDate;
        LinearLayout llChat, llMainChat;

        BrandViewHolder(View v) {
            super(v);

            tPostDate = v.findViewById(R.id.tPostDate);
            tName = v.findViewById(R.id.tName);
            tChatting = v.findViewById(R.id.tChatting);
            tTheDate = v.findViewById(R.id.tTheDate);
            llMainChat = v.findViewById(R.id.rlMainChat);
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

            ((BrandViewHolder) holder).tPostDate.setText(setFormatTime(mc.getPostDate().getTime()));
            ((BrandViewHolder) holder).tName.setText(mc.getName());
            ((BrandViewHolder) holder).tChatting.setText(mc.getDescriptionChat());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            if (mc.getUserId().equals(userId)) {
                ((BrandViewHolder) holder).llMainChat.setGravity(Gravity.END);
                ((BrandViewHolder) holder).llChat.setBackground(act.getResources().getDrawable(R.drawable.bg_rounded_chat_blue));
                params.setMargins(50, 0, 0, 0);
                ((BrandViewHolder) holder).llChat.setLayoutParams(params);
            } else {
                ((BrandViewHolder) holder).llMainChat.setGravity(Gravity.START);
                ((BrandViewHolder) holder).llChat.setBackground(act.getResources().getDrawable(R.drawable.bg_rounded_chat_red));
                params.setMargins(0, 0, 50, 0);
                ((BrandViewHolder) holder).llChat.setLayoutParams(params);
            }

            if (position < 1) {
                ((BrandViewHolder) holder).tTheDate.setText(setFormatDate(mc.getPostDate().getTime()));
                ((BrandViewHolder) holder).tTheDate.setVisibility(View.VISIBLE);
            } else if (!setFormatDate(mc.getPostDate().getTime()).equals(setFormatDate(items.get(position-1).getPostDate().getTime()))) {
                ((BrandViewHolder) holder).tTheDate.setText(setFormatDate(mc.getPostDate().getTime()));
                ((BrandViewHolder) holder).tTheDate.setVisibility(View.VISIBLE);
            } else {
                ((BrandViewHolder) holder).tTheDate.setVisibility(View.GONE);
            }
            ((BrandViewHolder) holder).llChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mc = items.get(position);
                    dialogDetailChat(mc);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String setFormatTime(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm"; // Format 24 Hours
//        final String timeFormatString = "hh:mm aa"; // Format AM and PM
//        final String dateTimeFormatString = "EEEE, dd MMMM, HH:mm";
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return DateFormat.format(timeFormatString, smsTime).toString();
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return DateFormat.format(timeFormatString, smsTime).toString();
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(timeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("HH:mm", smsTime).toString();
        }
    }

    private String setFormatDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String dateTimeFormatString = "dd MMMM yyyy";
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Hari ini ";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Kemarin ";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        }
    }

    @SuppressLint("InflateParams")
    private void dialogDetailChat(modChat modChat) {
        LayoutInflater inflater;
        View dialog_layout;
        inflater = LayoutInflater.from(act);
        dialog_layout = inflater.inflate(R.layout.dialog_detail_chat, null);
        Button btnOk = dialog_layout.findViewById(R.id.btnOk);
        TextView tPostDate = dialog_layout.findViewById(R.id.tPostDate);
        TextView tName = dialog_layout.findViewById(R.id.tName);
        TextView tChatting = dialog_layout.findViewById(R.id.tChatting);

        String dateTime = setFormatDate(modChat.getPostDate().getTime())+", "+setFormatTime(modChat.getPostDate().getTime());
        tPostDate.setText(dateTime);
        tName.setText(modChat.getName());
        tChatting.setText(modChat.getDescriptionChat());

        final AlertDialog dialogDetail;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(act);

        dialogBuilder.setView(dialog_layout);
        dialogBuilder.setCancelable(true);
        dialogDetail = dialogBuilder.create();
        dialogDetail.show();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetail.dismiss();
            }
        });
    }

}

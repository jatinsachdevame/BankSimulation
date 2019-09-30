package com.example.banksimulation;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class MyListAdapter extends ArrayAdapter<String> {

    final Activity context;
    final Integer[] image;
    final String[] amount;
    final String[] closing_balance;
    final String[] date;
    final String[] time;
    final Boolean[] type;

    public MyListAdapter(Activity context, Boolean[] type, Integer[] image, String[] amount, String[] closing_balance, String[] date, String[] time) {
        super(context, R.layout.listview_xml, amount);
        this.context = context;
        this.image = image;
        this.amount = amount;
        this.closing_balance = closing_balance;
        this.date = date;
        this.time = time;
        this.type = type;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview_xml, null,true);

        ImageView typeImageView = rowView.findViewById(R.id.type);
        TextView dateTextView =  rowView.findViewById(R.id.date);
        TextView amountTextView = rowView.findViewById(R.id.amount);
        TextView closing_balanceTextView = rowView.findViewById(R.id.closing_balance);
        TextView timeTextView = rowView.findViewById(R.id.time);


        amountTextView.setText(amount[position]);
        closing_balanceTextView.setText("Closing Balance : "+closing_balance[position]);
        timeTextView.setText(time[position]);
        typeImageView.setImageResource(image[position]);

        String date2 = reverseDate(date[position]);

        if (type[position]) {
            amountTextView.setText(amount[position]);
            amountTextView.setTextColor(Color.rgb(21,224,157));
            dateTextView.setText("Deposited on "+date2);


        }
        else {
            dateTextView.setText("Withdraw on "+date2);
        }


        return rowView;

    };

    public String reverseDate(String str) {

        String str2= "";

        str2 = str2 + str.substring(8,10);
        str2 = str2 + "/";
        str2 = str2 + str.substring(5,7);
        str2 = str2 + "/";
        str2 = str2 + str.substring(0,4);

        return str2;
    }

}

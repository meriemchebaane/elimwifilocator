package com.example.android.wifilocator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.android.wifilocator.MainActivity.listwifi;

public class WifiArrayAdapter extends ArrayAdapter<Wifi> {

    public WifiArrayAdapter(Context context) {

        super(context, R.layout.item_view, listwifi);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        View itemView = convertView;
        if (itemView == null) {
//                itemView=getActivity().getLayoutInflater().inflate(R.layout.item_view,parent,false);
            itemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_view, parent, false);
        }
        // Find the wifi to work with.

        Wifi wifi = listwifi.get(position);

        // Fill the view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_imageView);

        if (wifi.getSecurity().contains("WPA") || wifi.getSecurity().contains("WPA2")) {
            if ((int)  (wifi.getLevel()) < -90) {
                imageView.setImageResource(R.drawable.secured_low_signal);
            } else if ((int)  (wifi.getLevel()) > -50) {
                imageView.setImageResource(R.drawable.secured_high_signal);
            } else {
                imageView.setImageResource(R.drawable.secured_medium_signal);
            }
        } else {
            Log.d("Level " + listwifi.size(), String.valueOf((int)  (wifi.getLevel())));
            if ((int)  (wifi.getLevel()) < -90) {
                imageView.setImageResource(R.drawable.free_low_signal);
            } else if ((int)  (wifi.getLevel()) > -50) {
                imageView.setImageResource(R.drawable.free_high_signal);
            } else {
                imageView.setImageResource(R.drawable.free_medium_signal);
            }
        }

        TextView SSID = (TextView) itemView.findViewById(R.id.item_SSID);
        SSID.setText(wifi.getSSID());
        final RatingBar rate = (RatingBar) itemView.findViewById(R.id.ratingBar);
        rate.setRating(2f);

        rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rate.setRating(rating);
                rate.invalidate();
                Log.d("Rating", String.valueOf(rating));
            }
        });
        return itemView;
//            return super.getView(position,convertView,parent);
    }

}
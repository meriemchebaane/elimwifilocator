package com.example.android.wifilocator;

//AsyncTask to scan and detect all wifis available and put them in a ListView in the Main UI thread
// If the result of scan is null, display a textView instead of the listView and set the text to "No Wifis Available"
// as its emptyState

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import static com.example.android.wifilocator.MainActivity.listwifi;
import static com.example.android.wifilocator.MainActivity.wifiList;

public class WifisScanAsyncTask extends AsyncTask<Void, Void, Void> {

    public Activity activity;

    public WifisScanAsyncTask(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void ... voids) {

        MainActivity.wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        MainActivity.wifiManager.startScan();
        wifiList = MainActivity.wifiManager.getScanResults();


        Log.d("TAG", wifiList.toString());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (ScanResult scanResult : wifiList) {
            boolean exist = false;
            Wifi Oldwifi = null;
            int i = 0;
            boolean superieur = false;
            while (!exist) {
                for (Wifi wifi : listwifi) {
                    if ((wifi.getSecurity().equals(scanResult.capabilities)) && (wifi.getSSID().equals(scanResult.SSID))) {
                        exist = true;
                        if ((int)(wifi.getLevel()) < scanResult.level) {
                            Oldwifi = wifi;
                            superieur = true;
                            break;
                        }
                    }
                }
                if (superieur) {
                    listwifi.remove(Oldwifi);
                    listwifi.add(new Wifi(scanResult.SSID, scanResult.capabilities, scanResult.level, scanResult.BSSID));
                    Log.d("SSID " + listwifi.size(), scanResult.SSID);
                    Log.d("Capabilities " + listwifi.size(), scanResult.capabilities);
                    Log.d("Level " + listwifi.size(), String.valueOf(scanResult.level));
                } else if (!exist) {
                    listwifi.add(new Wifi(scanResult.SSID, scanResult.capabilities, scanResult.level, scanResult.BSSID));
                    Log.d("SSID " + listwifi.size(), scanResult.SSID);
                    Log.d("Capabilities " + listwifi.size(), scanResult.capabilities);
                    Log.d("Level " + listwifi.size(), String.valueOf(scanResult.level));
                    //   Log.d("Capabilities " + listwifi.size(), scanResult.capabilities);
                    //     Log.d("Level " + listwifi.size(), String.valueOf(scanResult.level));

                }
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        ArrayAdapter<Wifi> adapter = new WifiArrayAdapter(activity);
        NonScrollListView list = (NonScrollListView) activity.findViewById(R.id.list_Wifi);
        list.setAdapter(adapter);
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = activity.findViewById(R.id.loading_indicator2);
        loadingIndicator.setVisibility(View.GONE);
        // TextView that is displayed when the list of wifis is empty
        TextView mEmptyStateTextView = (TextView) activity.findViewById(R.id.empty_view);
        list.setEmptyView(mEmptyStateTextView);
        mEmptyStateTextView.setText(R.string.no_wifis);

    }
}
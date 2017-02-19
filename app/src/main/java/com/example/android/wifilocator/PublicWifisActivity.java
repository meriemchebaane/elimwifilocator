package com.example.android.wifilocator;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.android.wifilocator.models.AccessPoint;
import com.example.android.wifilocator.models.Cluster;
import com.example.android.wifilocator.models.Point;
import com.example.android.wifilocator.models.Region;
import com.example.android.wifilocator.models.SSID;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.android.wifilocator.MainActivity.key;
import static com.example.android.wifilocator.MainActivity.mFirebaseDatabase;

/**
 * Created by Sherif Meimari on 1/25/2017.
 */

public class
PublicWifisActivity extends AppCompatActivity {

    //Context
    public Context context;
    final List<AccessPoint> points = new ArrayList<AccessPoint>();
    public static DatabaseReference mBSSIDSDatabaseReference;
    //Expandable List
    ExpandableListView mExpandableListView;
    ExpandableListAdapter mExpandableListAdapter;
    List<String> regionNames;
    HashMap<String,List<String>> regionsMap;
    //Firebase
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_wifis);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Expandable ListView Components
        mExpandableListView = (ExpandableListView) findViewById(R.id.exp_list);
        regionsMap = new HashMap<>();
        regionNames = new ArrayList<String>(regionsMap.keySet());
        mExpandableListAdapter = new WifiExpandableListAdapter(this, regionNames, regionsMap);

        //Key
//        public final static String Key  = "com.example.android.wifilocator";


        // ******************selected child***************************
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                final String Region = regionNames.get(groupPosition);
                final String ssidSlected= regionsMap.get(regionNames.get(groupPosition)).get(childPosition);
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                mBSSIDSDatabaseReference = mFirebaseDatabase.getReference();
                final Region[] region = new Region[1];
                mBSSIDSDatabaseReference.child(Region).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        region[0] = dataSnapshot.getValue(Region.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String regionName = region[0].getRegion();
//                        boolean foundSSID = false;
                        if (regionName.equals(Region)){
                            for(SSID ssid : region[0].getListSSID()){
//                                String ssidName = ssid.getSSID();

//                                    foundSSID = true;
                                        for(AccessPoint accessPoint:ssid.getAccessPoints()){
                                            AccessPoint point = new AccessPoint(accessPoint.getLevel(),accessPoint.getLat(),accessPoint.getLng());
                                            points.add(point);

                                }

                            }
                            try {
                                uploadToServer();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();

                return false;
            }
        });
    }

    private void uploadToServer() throws IOException, JSONException {
        String query = "http://wifilocator-fe294.appspot.com/server";
        String json = new Gson().toJson(points);
        URL url = new URL(query);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes("UTF-8"));
        os.close();

        String response = conn.getResponseMessage();
        // read the response
        Reader reader = new InputStreamReader(conn.getInputStream(), "UTF-8");
//        List<AccessPoint> bestWifiPoints = new Gson().fromJson(reader, new TypeToken<List<AccessPoint>>(){}.getType());
        List<Cluster> bestWifiPoints = new Gson().fromJson(reader, new TypeToken<List<Cluster>>(){}.getType());
        Log.d("Clusters", bestWifiPoints.toString());


//        for(AccessPoint bestPoint : bestWifiPoints){
//            Log.d("Test", bestPoint.toString());
//        }
//            Log.d("Test", bestWifiPoints);

        reader.close();
        conn.disconnect();

        ArrayList<AccessPoint> accessPoints = new ArrayList<>();
        for(Cluster cluster : bestWifiPoints){
            if(cluster.getCentroid().getX() > -80){
                for(Point point : cluster.getPoints()){
                    accessPoints.add(new AccessPoint(point.getX(), point.getY(), point.getZ()));
                }
            }
        }
        Intent intent = new Intent(PublicWifisActivity.this,MainActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("arrayList",(Serializable)((ArrayList<AccessPoint>)accessPoints));
        intent.putExtra("bundle",args);
        startActivity(intent);

//        Intent intent = new Intent(PublicWifisActivity.this,MainActivity.class);
//        Bundle args = new Bundle();
//        args.putSerializable("arrayList",(Serializable)((ArrayList<AccessPoint>)bestWifiPoints));
//        intent.putExtra("bundle",args);
//        startActivity(intent);

//        Bundle mBundle = new Bundle();
//        mBundle.putParcelableArrayList(key,(ArrayList<? extends Parcelable>) bestWifiPoints);
//        Intent mIntent = new Intent(this, MainActivity.class);
//        mIntent.putExtra("best", mBundle);
//        startActivity(mIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.settings:
                Intent myIntent = new Intent(PublicWifisActivity.this,
                        SettingsActivity.class);
                startActivity(myIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void attachDatabaseReadListener() {

        //Listener to listen to any item added, changed, removed, moved or cancelled in the Database
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Region region = dataSnapshot.getValue(Region.class);
                    String regionName = region.getRegion();
                    List<String> ssidNames = new ArrayList<String>();
                    for(SSID ssid : region.getListSSID()){
                        String ssidName = ssid.getSSID();
                        ssidNames.add(ssidName);
                    }
                    regionNames.add(regionName);
                    regionsMap.put(regionName, ssidNames);
                    mExpandableListView.setAdapter(mExpandableListAdapter);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            MainActivity.mWifisDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseListener() {
        if (mChildEventListener != null) {
            MainActivity.mWifisDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}

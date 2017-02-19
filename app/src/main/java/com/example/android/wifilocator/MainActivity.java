package com.example.android.wifilocator;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.android.wifilocator.models.AccessPoint;
import com.example.android.wifilocator.models.Region;
import com.example.android.wifilocator.models.SSID;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import static com.example.android.wifilocator.R.id.map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    //Key
    public static final String key = "key";
    //Wifi List
    public static List<Wifi> listwifi = new ArrayList<Wifi>();
    public static WifiManager wifiManager;
    public static List<ScanResult> wifiList;

    //Google Map
    public static GoogleMap mGoogleMap;

    //ScrollView
    public static ScrollView mainScrollView;
    //Transparent ImageView
    ImageView transparentImageView;

    //Float Action Button
    private FloatingActionButton mFloatingActionButton;
    private FloatingActionButton mFloatingActionButton2;

    //Firebase
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mWifisDatabaseReference;
    private static DatabaseReference rootReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mChildEventListener;
    //Authentication
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;

    //List of Locations of all the wifis in the Database
    public static List<LatLng> results = new ArrayList<>();
    //Intent
    Intent intent;
    //Results
    ArrayList<AccessPoint> bestWifiPoints;

    //The main thread of the MainActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        mainScrollView = (ScrollView) findViewById(R.id.main_scrollview);
        transparentImageView = (ImageView) findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        //AsyncTask to scan and detect all wifis available and put them in a ListView in the Main UI thread
        // If the result of scan is null, display a textView instead of the listView and set the text to "No Wifis Available"
        // as its emptyState

        WifisScanAsyncTask scanWifisTask = new WifisScanAsyncTask(this);
        scanWifisTask.execute();

        //Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mWifisDatabaseReference = mFirebaseDatabase.getReference();

        //Results
        bestWifiPoints = new ArrayList<>();

        //FloatingActionButton sends list of wifis
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Send wifis with corresponding SSID on Click
                final List<SSID> ssids = new ArrayList<SSID>();
                final Region[] region = new Region[1];
                mWifisDatabaseReference.child(GoogleLocationAsyncTask.region).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        region[0] = dataSnapshot.getValue(Region.class);
  Toast.makeText(MainActivity.this, "New Wifis have been added!", Toast.LENGTH_SHORT).show();
                }

                @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(region[0] == null){
                            for(Wifi wifi : listwifi) {
                                //Add only public wifis with no security
                                if(!wifi.getSecurity().contains("WPA") && !wifi.getSecurity().contains("WPA2"))
                                    ssids.add(createSSID(wifi));
                            }
                            region[0] = new Region(GoogleLocationAsyncTask.region, ssids);
                            mWifisDatabaseReference.child(region[0].getRegion()).setValue(region[0]);
                            Log.d("TAG", "New Region: " + region[0]);
                        }else{
                            for(Wifi wifi : listwifi){
                                String ssidName = wifi.getSSID();
                                boolean foundSSID = false;
                                SSID ssid1=null;
                                for(SSID ssid : region[0].getListSSID()){
                                    foundSSID = false;
                                    ssid1 = null;
                                    if(ssid.getSSID().equals(ssidName)){
                                        foundSSID = true;
                                        ssid1 = ssid;
                                        break;

                                    }
                                }
                                if(foundSSID){
                                    ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
                                    ssid1.getAccessPoints().add(new AccessPoint(wifi.getLevel(), GoogleLocationAsyncTask.latLng.latitude, GoogleLocationAsyncTask.latLng.longitude));
                                }
                                else {
//                                    ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
//                                    accessPoints.add(new AccessPoint(wifi.getLevel(), GoogleLocationAsyncTask.latLng.latitude, GoogleLocationAsyncTask.latLng.longitude));
//                                    SSID ssid2 = new SSID(wifi.getSSID());
//                                    ssid2.setAccessPoints(accessPoints);
//                                    region[0].getListSSID().add(ssid2);
                                    region[0].getListSSID().add(createSSID(wifi));
                                }
                            }
                            mWifisDatabaseReference.child(region[0].getRegion()).setValue(region[0]);
                        }
                    }
                });
                thread.start();
//                for(SSID ssid : ssids){
//
//                    //Exclude the wifis with security restriction
//                    if(!ssid.getSecurity().contains("WPA") && !ssid.getSecurity().contains("WPA2")) {
//                        //if you only want a specific wifi set its ssid
////                      if(Pattern.compile(Pattern.quote("Mediterranee"), Pattern.CASE_INSENSITIVE).matcher(wifi.getSSID()).find())
////                        mWifisDatabaseReference.child(ssid.getSSID() + ": " + ssid.getRegion()).setValue(ssid);
//
//
//                        mWifisDatabaseReference.child(region[0].getRegion()).setValue(region[0]);
//
//                    }
//                }
            }
        });

        //FloatingActionButton to show publicWifisActivity
        mFloatingActionButton2 = (FloatingActionButton) findViewById(R.id.fab2);
        mFloatingActionButton2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(MainActivity.this,
                        PublicWifisActivity.class);
                startActivity(myIntent);
            }
        });


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //user is signed in
                }else{
                    //user is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    //Method to create new SSID instance used in Firebase
    public SSID createSSID(Wifi wifi){

        AccessPoint accessPoint = new AccessPoint(wifi.getLevel() ,GoogleLocationAsyncTask.latLng.latitude, GoogleLocationAsyncTask.latLng.longitude);
        List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
        accessPoints.add(accessPoint);
        SSID ssid = new SSID(wifi.getSSID(), wifi.getSecurity(), accessPoints, GoogleLocationAsyncTask.region);
        return ssid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //Method that displays the map with the current position marker when the map is loaded
    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mGoogleMap.setMyLocationEnabled(true);

        //AsyncTask that creates a GoogleApiClient to get current location of the user and display
        // the updated location on the GoogleMap
        GoogleLocationAsyncTask currentLocationTask = new GoogleLocationAsyncTask(this);
        currentLocationTask.execute();
    Intent intent= getIntent();
        if(intent.getBundleExtra("bundle")!= null) {
    Bundle args =intent.getBundleExtra("bundle");
           ArrayList <AccessPoint> bestWifiPoints = (ArrayList<AccessPoint>) args.getSerializable("arrayList");
            for (AccessPoint point : bestWifiPoints) {
                LatLng result = new LatLng(point.getLat(), point.getLng());
                results.add(result);
            }
            //Adding the heatMap with all wifis locations to the map
            addHeatMap(results);
        }

    }

    //Inflating the AppBar to specify a java object for the xml View of the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Method to determine the action of each item in the AppBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.rescan) {
            //Rescan Wifis
            //AsyncTask that creates a GoogleApiClient to get current location of the user and display
            // the updated location on the GoogleMap
            GoogleLocationAsyncTask currentLocationTask = new GoogleLocationAsyncTask(this);
            currentLocationTask.execute();
            return true;
        }

        if (id == R.id.sign_out) {
            //Signed Out
            AuthUI.getInstance().signOut(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addHeatMap(List<LatLng> list) {
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

    }

    @Override
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        if (mChildEventListener != null) {
            mWifisDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

}


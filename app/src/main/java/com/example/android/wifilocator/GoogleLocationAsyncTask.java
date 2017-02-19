package com.example.android.wifilocator;

/**
 * Created by Sherif Meimari on 1/25/2017.
 */

//AsyncTask that creates a GoogleApiClient to
// 1- get current location of the user and display the updated location on the GoogleMap
//2- get address of the current location to define the region

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.android.wifilocator.MainActivity.mGoogleMap;

public class GoogleLocationAsyncTask extends AsyncTask<Void, Void, Void> implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public Activity activity;

    public GoogleLocationAsyncTask(Activity activity)
    {
        this.activity = activity;
    }

    //Location
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    //Google Map
    public static LatLng latLng;
    Marker currLocationMarker;

    //Geocoder for addresses
    Geocoder geocoder;
    List<Address> addresses;
    public static String address;
    public static String city;
    public static String country;
    public static String region;

    //Building the GoogleApiClient to get the Location from Google Services
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Getting current Location Overriden Methods and updating it in the map
    @Override
    public void onConnected(Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();

            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            //        zoom to current position:
            CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
//            markerOptions.title("Current Position");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(activity, "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(activity, "onConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

//        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        currLocationMarker = mGoogleMap.addMarker(markerOptions);


//        zoom to current position:
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(latLng).zoom(14).build();
//
//        mGoogleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));

        //I only need one location, unregister the listener
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//        Toast.makeText(activity, "latitude: " + location.getLatitude() + " longitude: " + location.getLongitude(),Toast.LENGTH_SHORT);
//        Log.d("Location", String.valueOf(location.getLatitude()));

        //Location updated
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //getting name of the place of this location
        geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            addresses= geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0)
            {
                address = addresses.get(0).getAddressLine(0);
                //Delete street number from the address
                for(int i=0; i<=9; i++) {
                    if (address.contains(String.valueOf(i))) {
                        address = address.replace(String.valueOf(i), "");
                    }
                }
                if(address.contains("-")){
                    address = address.replace("-", "");
                }

                city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                region = address + ", " + city + ", " + country;

//                Toast.makeText(activity, "Address : " + address, Toast.LENGTH_SHORT).show();
//                Toast.makeText(activity, "City : " + city, Toast.LENGTH_SHORT).show();
//                Toast.makeText(activity, "Country : " + country, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //setting the location to wifis
        for(Wifi wifi : MainActivity.listwifi) {
            wifi.setLocation(latLng);
        }

    }

    @Override
    protected Void doInBackground(Void ... voids) {

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
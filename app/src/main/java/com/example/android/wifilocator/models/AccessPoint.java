package com.example.android.wifilocator.models;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Sherif Meimari on 1/25/2017.
 */

public class AccessPoint implements Serializable{

    private double  level=0;
    private double lat=0;
    private double lng=0;

    //Constructors
    //Constructor 1
    public AccessPoint(){

    }

    //Constructor 2
    public AccessPoint(double level){

        this.level = level;
    }

    //Constructor 3
    public AccessPoint(double level, double lat, double lng){
        this.level = level;
        this.lat = lat;
        this.lng = lng;
    }

    //Getters
    public double getLevel() {
        return level;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

//Setters

    public void setLevel(double level) {
        this.level = level;
    }


    @Override
    public String toString() {
        return "AccessPoint{" +
                "level='" + level + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}

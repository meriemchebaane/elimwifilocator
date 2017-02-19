package com.example.chebaane.myapplication.backend;

/**
 * Created by Sherif Meimari on 1/25/2017.
 */

import java.util.List;

//Class of the objects that will be stored to and retrieved from the Firebase
public class SSID {

    private String ssid;
    private String security;
    private List<AccessPoint> accessPoints;
    private String region;

    //Constructors
    //Constructor 1
    public SSID(){

    }

    //Constructor 2
    public SSID(String ssid){
        this.ssid = ssid;
    }

    //Constructor 3
    public SSID(String ssid, String security){

        this.ssid = ssid;
        this.security = security;
    }

    //Constructor 4
    public SSID(String ssid, String security, List<AccessPoint> accessPoints){
        this.ssid = ssid;
        this.security = security;
        this.accessPoints = accessPoints;
    }

    //Constructor 5
    public SSID(String ssid, String security, List<AccessPoint> accessPoints, String region){
        this.ssid = ssid;
        this.security = security;
        this.accessPoints = accessPoints;
        this.region = region;
    }

    //Getters
    public String getSSID() {
        return ssid;
    }

    public List<AccessPoint> getAccessPoints() {
        return accessPoints;
    }

    public String getSecurity() {
        return security;
    }

    public String getRegion(){ return region; }

    //Setters
    public void setSSID(String ssid) {
        this.ssid = ssid;
    }

    public void setAccessPoints(List<AccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
    }
    public void setRegion(String region){ this.region = region; }
}

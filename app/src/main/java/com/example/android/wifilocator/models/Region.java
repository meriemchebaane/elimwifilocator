package com.example.android.wifilocator.models;

import com.example.android.wifilocator.models.SSID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sherif Meimari on 1/28/2017.
 */

public class Region {

    String region;
    List<SSID> listSSID = new ArrayList<>();

    //Constructor 1
    public Region(){}

    //Constructor 2
    public Region(String region, List<SSID> listSSID){
        this.region = region;
        this.listSSID = listSSID;
    }

    //Getters

    public String getRegion() {
        return region;
    }

    public List<SSID> getListSSID() {
        return listSSID;
    }

    //Setters

    public void setRegion(String region) {
        this.region = region;
    }

    public void setListSSID(List<SSID> listSSID) {
        this.listSSID = listSSID;
    }
}

package com.example.jamesn.safetravels;

import org.json.JSONObject;

/**
 * Created by James on 3/5/2015.
 */
public class Waypoint {
    double longitude;
    double latitude;
    int minimumETA;
    JSONObject[] weatherData=new JSONObject[49];

    public Waypoint(Double latitude1, Double longitude1,int ETA){
        longitude=longitude1;
        latitude=latitude1;
        minimumETA=ETA;
    }

    public String toString(){
        return "Latitude "+ latitude + "Longitude: " + longitude+ "Minimum time to this destination:"+ minimumETA;
    }
}

package com.example.jamesn.safetravels;

import org.joda.time.DateTimeZone;
import org.json.JSONObject;

/**
 * Created by James on 3/5/2015.
 */
public class Waypoint implements java.io.Serializable {

    double longitude;
    double latitude;
    int minimumETA;
    int stoppageTime; //Time in minutes to be stopped at this particular waypoint.

    //TODO set these variables in the Main Activity
    int timeZoneOffset;
    String localTimeZoneID;
    String location;
    String timezone;
    DateTimeZone timeZoneObject;



    JSONObject[] weatherData=new JSONObject[48]; // may want to change this to say, 48-(n) where n is the hours of a given trip

    public static Waypoint[] transfer;

    public Waypoint(Double latitude1, Double longitude1,int ETA){
        longitude=longitude1;
        latitude=latitude1;
        minimumETA=ETA;
    }

    public String toString(){
        return "Latitude "+ latitude + "Longitude: " + longitude+ "Minimum time to this destination:"+ minimumETA;
    }
}

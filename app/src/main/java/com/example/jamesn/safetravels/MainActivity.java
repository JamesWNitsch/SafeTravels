package com.example.jamesn.safetravels;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity {
    //URL JSON request Variables
    private final String directionAPIKey = "AIzaSyDxwp7uZEZ4OkHX_uBBzeGJ_dBlmi2gVYM";
    private final String weatherAPIKey= "060abcb16ab36eee9315150eb9d7a4bd";
    private String origin = "";
    private String destination = "";
    private String[] userWaypoints = new String[28];
    private Waypoint[] waypointArray= new Waypoint[10];
    private DateTime weatherQueryStartTime;

    //TextViews/EditTexts
    private EditText originEditText;
    private EditText destinationEditText;
    private EditText firstWaypointEditText;
    private EditText secondWaypointEditText;
    private EditText thirdWaypointEditText;
    private TextView results;

    public String internetQueryResults;
    public Object lock= new Object();

    //Json Arrays and Objects for weather and directions
    public JSONArray legsArray;
    private JSONObject waypointWeatherFull;

    //Variables from the Json data for weather

    //Variables from the Json data for directions
    private int totalTravelTime;


    //https://maps.googleapis.com/maps/api/directions/json?origin=Brooklyn&destination=Queens&mode=transit&key=API_KEY
    //https://maps.googleapis.com/maps/api/directions/json?origin=Boston,MA&destination=Concord,MA&waypoints=Charlestown,MA|Lexington,MA&key=API_KEY
    //https://api.forecast.io/forecast/060abcb16ab36eee9315150eb9d7a4bd/37.8267,-122.423
    //https://api.forecast.io/forecast/060abcb16ab36eee9315150eb9d7a4bd/40.71265260000001,-74.0065973,1425730500?&exclude=[daily,hourly,minutely]
    //https://maps.googleapis.com/maps/api/geocode/json?latlng=40.7126526,-74.006597&key=AIzaSyAAWUbXDk2a_I_VnHkGK18Us3PDGkTcci8

    public String createWeatherURLTime(double latitude, double longitude,DateTime date) throws UnsupportedEncodingException {

        return "https://api.forecast.io/forecast/" + weatherAPIKey + "/"
                + Double.toString(latitude) + "," + Double.toString(longitude)
                +","+ String.valueOf(date.getMillis() / 1000)
                + "?&exclude="+URLEncoder.encode("[","UTF-8")+",daily,minutely,flags,currently,"+URLEncoder.encode("]", "UTF-8");
    }

    public String createReverseGeocodingURL(double latitude, double longitude) throws UnsupportedEncodingException {
        //https://maps.googleapis.com/maps/api/geocode/json?latlng=40.2149925,-74.586042&result_type=administrative_area_level_2&key=AIzaSyAAWUbXDk2a_I_VnHkGK18Us3PDGkTcci8
        System.out.println("https://maps.googleapis.com/maps/api/geocode/json?latlng="+
                latitude +","+longitude+"&result_type=administrative_area_level_2"+URLEncoder.encode("|","UTF-8")+
                "administrative_area_level_1" +"&key="+directionAPIKey);

        return "https://maps.googleapis.com/maps/api/geocode/json?latlng="+
                latitude +","+longitude+"&result_type=administrative_area_level_2"+URLEncoder.encode("|","UTF-8")+
                "administrative_area_level_1" +"&key="+directionAPIKey;


    }

    public String createWeatherURL(double latitude, double longitude) throws UnsupportedEncodingException {

        return "https://api.forecast.io/forecast/" + weatherAPIKey + "/"
                + Double.toString(latitude) + "," + Double.toString(longitude)
                + "?&exclude="+URLEncoder.encode("[","UTF-8")+",daily,minutely,flags,currently,"+URLEncoder.encode("]", "UTF-8");
    }

    public String createDirectionsURL() {
        System.out.println("https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin +
                "&destination=" + destination +
                "&waypoints=" + returnWaypoints() +
                "&key=" + directionAPIKey);

            return "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + origin +
                    "&destination=" + destination +
                    "&waypoints=" + returnWaypoints()+
                    "&key=" + directionAPIKey;
    }

    public String returnWaypoints() {
        String waypointURLSegment = "";
        Boolean firstWaypoint=true;

        for (String st : userWaypoints) {
            if (st != null && st.matches(".*[a-zA-Z]+.*")) {
                if (!firstWaypoint){
                    waypointURLSegment+="|";
                }
                waypointURLSegment +=st;
                firstWaypoint=false;
            }
        }
        try {
            return URLEncoder.encode(waypointURLSegment,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Something went wrong";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize the views
        originEditText = (EditText) findViewById(R.id.originEditTextView);
        destinationEditText = (EditText) findViewById(R.id.destinationEditTextView);
        firstWaypointEditText = (EditText) findViewById(R.id.firstWaypointEditText);
        secondWaypointEditText = (EditText) findViewById(R.id.secondWaypointEditText);
        thirdWaypointEditText = (EditText) findViewById(R.id.thirdWaypointEditText);
        results = (TextView) findViewById(R.id.results);

        //Just for testing
        originEditText.setText("Princeton,NJ");
        destinationEditText.setText("Lawrenceville, NJ");
        //firstWaypointEditText.setText("Toronto,Canada");
        //secondWaypointEditText.setText("Princeton,NJ");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void getLocationsOfWaypoints() throws JSONException, UnsupportedEncodingException {
        JSONObject waypointLocation;
        JSONArray waypointLocationResults;
        JSONObject conversionObject;
        for (Waypoint waypoint:waypointArray){
            if (waypoint!= null){
                if (waypoint.location==null){
            new getInternetData().execute(createReverseGeocodingURL(waypoint.latitude, waypoint.longitude));
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            waypointLocation = new JSONObject(internetQueryResults);
            System.out.println("Waypoint Location raw results: "+waypointLocation.toString());
            waypointLocationResults= waypointLocation.getJSONArray("results");
            System.out.println("Waypoint Location result object: "+waypointLocationResults.toString());
            conversionObject=waypointLocationResults.getJSONObject(0);
            String regex = "\\s*\\b, USA\\b\\s*";
            waypoint.location=conversionObject.getString("formatted_address").replaceAll(regex, "");


            System.out.println("Location String: " + waypoint.location);
        }}}
    }

    public Waypoint averageWaypoint(double latitudeStart, double longitudeStart,
                                    double latitudeEnd, double longitudeEnd,
                                    int fullDuration, int timeTraveled, int timeCounter){
        double scale = ((double)fullDuration)/((double)timeTraveled);
        return new Waypoint((((latitudeEnd-latitudeStart)/scale)+latitudeStart),(((longitudeEnd-longitudeStart)/scale)+longitudeStart),
                (timeCounter+timeTraveled));
    }

    public void commitAndSend(View view) {
        origin = String.valueOf(originEditText.getText());
        destination = String.valueOf(destinationEditText.getText());
        origin = String.valueOf(originEditText.getText());

        userWaypoints[0]= String.valueOf(firstWaypointEditText.getText());
        userWaypoints[1]= String.valueOf(secondWaypointEditText.getText());
        userWaypoints[2]= String.valueOf(thirdWaypointEditText.getText());

        sendAndReceiveDirections();
        try {
            try {
                sendAndReceiveWeather();
                getLocationsOfWaypoints();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Waypoint.transfer =waypointArray;
        Intent intent=new Intent(this,ResultsScreen.class);
        intent.putExtra("timeOfRequest",weatherQueryStartTime); //in order to correctly display the times.
        startActivity(intent);
    }

    public int stepDurationCalculation(JSONObject step) throws JSONException {
        JSONObject durationObject = step.getJSONObject("duration");
        String durationString = durationObject.getString("text");
        int stepTimeMins=0;
        String[] durationSplit;
        durationSplit=durationString.split(" ");

        if (durationString.contains("days")||durationString.contains("day")){
            stepTimeMins+= (Integer.valueOf(durationSplit[0])*1440);
            stepTimeMins+= (Integer.valueOf(durationSplit[2])*60);}
        else if (durationString.contains("hours")||durationString.contains("hour")){
            stepTimeMins+= (Integer.valueOf(durationSplit[0])*60);
            stepTimeMins+= (Integer.valueOf(durationSplit[2])); }
        else if (durationString.contains("mins")||durationString.contains("min")){
            stepTimeMins+= (Integer.valueOf(durationSplit[0])); }
        return stepTimeMins;
    }

    //Set waypoints for the entire trip
    public void setWaypoints() throws JSONException {
        waypointArray= new Waypoint[10];
        int stepDurationTimer;
        int timeCounter=0;
        int test;
        int index=1; //Set to 1 because index 0 is automatically set as the starting location
        JSONObject stepEndLocation;
        JSONObject stepStartLocation;
        //Extracting the start and endpoints of the journey for the first and last waypoint
        JSONObject temp=legsArray.getJSONObject(0);
        JSONObject startLocation=temp.getJSONObject("start_location");
        JSONObject endLocation=temp.getJSONObject("end_location");
        waypointArray[0]=new Waypoint(startLocation.getDouble("lat"),startLocation.getDouble("lng"),0);
        waypointArray[0].location=String.valueOf(originEditText.getText());
        //if trip is less that 8 hours
        if (totalTravelTime<=480){
            for (int i=0; i<legsArray.length(); i++) {
                //assign steps to that leg's index
                JSONObject leg = legsArray.getJSONObject(i);
                JSONArray steps = leg.getJSONArray("steps");
                for (int j=0; j<steps.length(); j++){
                    int splitDistanceLoopCount=1;
                    JSONObject currentStep=steps.getJSONObject(j);
                    stepDurationTimer= stepDurationCalculation(currentStep);
                    while (stepDurationTimer>=60) {
                        stepEndLocation = currentStep.getJSONObject("end_location");
                        stepStartLocation = currentStep.getJSONObject("start_location");
                        //Just for testing
                        System.out.println(stepStartLocation.getDouble("lat")+" "+stepStartLocation.getDouble("lng")+ " " +
                                stepEndLocation.getDouble("lat")+ " "+ stepEndLocation.getDouble("lng")+ " "+
                                stepDurationCalculation(currentStep)+ " "+ ((60 * splitDistanceLoopCount) - (timeCounter % 60)));
                        System.out.println("While loop");

                        waypointArray[index]=averageWaypoint(stepStartLocation.getDouble("lat"), stepStartLocation.getDouble("lng"),
                                stepEndLocation.getDouble("lat"), stepEndLocation.getDouble("lng"),
                                stepDurationCalculation(currentStep), ((60 * splitDistanceLoopCount) - (timeCounter % 60)), timeCounter);
                        stepDurationTimer -= 60;
                        timeCounter += 60;
                        index++;
                    }
                    test=((timeCounter%60)+stepDurationTimer);
                    if (test>=60){
                        stepEndLocation = currentStep.getJSONObject("end_location");
                        stepStartLocation = currentStep.getJSONObject("start_location");
                        //Just for testing
                        System.out.println(stepStartLocation.getDouble("lat")+" "+stepStartLocation.getDouble("lng")+ " " +
                                stepEndLocation.getDouble("lat")+ " "+ stepEndLocation.getDouble("lng")+ " "+
                                stepDurationCalculation(currentStep)+ " "+ ((60 * splitDistanceLoopCount) - (timeCounter % 60)));
                        System.out.println("if>=60");

                        waypointArray[index]=averageWaypoint(stepStartLocation.getDouble("lat"), stepStartLocation.getDouble("lng"),
                                stepEndLocation.getDouble("lat"), stepEndLocation.getDouble("lng"),
                                stepDurationCalculation(currentStep), (60-(timeCounter%60)), timeCounter);
                        timeCounter+=stepDurationTimer;
                        index++;
                    }
                    if (test<60){
                        timeCounter+=stepDurationTimer;
                    }
                }
            }
            System.out.println("well.... everything worked!");
        }
        else {
            System.out.println("Haven't accounted for trips above 8 hours yet!");
        }
        //there should be a way to calculate an even 8-way split between the total travel time, and
        //then replace all instances of '60' above with the new variable, providing the same even
        //split. Do this after.

        //sets the last waypoint to the destination lat/lng
        waypointArray[index]=new Waypoint(endLocation.getDouble("lat"),endLocation.getDouble("lng"), totalTravelTime);
        waypointArray[index].location=String.valueOf(destinationEditText.getText());
    }




    //Accepts in a JSONArray of the Legs of the Journey, and passes back the total travel
    //time in minutes.
    public int calculateTotalTravelTime() throws JSONException {
        totalTravelTime=0;
        String temp;
        String[] durationSplit;
        for (int i=0; i<legsArray.length(); i++){
            JSONObject legTimeLoop =legsArray.getJSONObject(i);
            temp=(legTimeLoop.getJSONObject("duration").getString("text"));
            System.out.println(temp);
            durationSplit=temp.split(" ");
            if (Arrays.asList(durationSplit).contains("days")||Arrays.asList(durationSplit).contains("day")){
                System.out.println("it worked!--Days/Hours");
                totalTravelTime+= (Integer.valueOf(durationSplit[0])*1440);
                totalTravelTime+= (Integer.valueOf(durationSplit[2])*60);
            }
            else if (Arrays.asList(durationSplit).contains("hours")||Arrays.asList(durationSplit).contains("hour")){
                System.out.println("it worked!--Hours/Mins");
                totalTravelTime+= (Integer.valueOf(durationSplit[0])*60);
                totalTravelTime+= (Integer.valueOf(durationSplit[2]));
            }
            else if (Arrays.asList(durationSplit).contains("mins")||Arrays.asList(durationSplit).contains("min")){
                System.out.println("it worked!-- Mins");
                totalTravelTime+= (Integer.valueOf(durationSplit[0]));
            }

        }
        System.out.println(totalTravelTime);
        return totalTravelTime;
    }

    public void sendAndReceiveWeather() throws JSONException, UnsupportedEncodingException {
        waypointWeatherFull = new JSONObject();
        weatherQueryStartTime = new DateTime();
        for (Waypoint waypoint: waypointArray){
            if (waypoint!=null) {
                new getInternetData().execute(createWeatherURL(waypoint.latitude, waypoint.longitude));
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //System.out.println(tempResults);
                waypointWeatherFull = new JSONObject(internetQueryResults);
                //Assign Waypoint's Constant Variables:
                //TODO timezone assignment (possibly through JodaTime) Get time from offset
                waypoint.localTimeZoneID=waypointWeatherFull.getString("timezone");
                waypoint.timeZoneOffset=waypointWeatherFull.getInt("offset");
                DateTimeZone timeZone=DateTimeZone.forID(waypoint.localTimeZoneID);
                waypoint.timezone= timeZone.getShortName(DateTimeUtils.currentTimeMillis());
                waypoint.timeZoneObject=timeZone;




                JSONArray tempHourly= waypointWeatherFull.getJSONObject("hourly").getJSONArray("data");
                DateTime tempTimeStamp=weatherQueryStartTime.plusMinutes(waypoint.minimumETA); //Local variable to hold the minimum ETA of the waypoint being processed
                int startHourIndex=0;
                //this function finds the nearest hour in the hourly data array. The two if statements are responsible for rounding up or down.
                int k=1;
                while (k<tempHourly.length()){
                    if(tempHourly.getJSONObject(k).getLong("time")>=(tempTimeStamp.getMillis()/1000)){
                        if (tempTimeStamp.getMinuteOfHour()>30) {
                            startHourIndex = k;
                            System.out.println(k);
                        }
                        else if (tempTimeStamp.getMinuteOfHour()<=30){
                            startHourIndex = k-1;
                            System.out.println(k);
                        }
                        break;
                    }
                    k++;
                }
                //just for testing
                System.out.println(waypoint.minimumETA);

                int u=startHourIndex;
                for (int l=0; l<waypoint.weatherData.length;l++){//while waypoint.WeatherData is not full
                    int additionalCalls=1;
                    if (u<tempHourly.length()) {
                        waypoint.weatherData[l] = tempHourly.getJSONObject(u);
                        System.out.println(waypoint.weatherData[l]);
                        u++;
                    }
                    else{
                        System.out.println("you went over 48 hours of data. Calling for additional data");
                        new getInternetData().execute(createWeatherURLTime(waypoint.latitude, waypoint.longitude, tempTimeStamp.plusDays(additionalCalls)));
                        synchronized (lock) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        waypointWeatherFull = new JSONObject(internetQueryResults);
                        tempHourly= waypointWeatherFull.getJSONObject("hourly").getJSONArray("data");
                        u=0;
                    }

                }

            }
        }
    }

    public void sendAndReceiveDirections(){


        new getInternetData().execute(createDirectionsURL());

        synchronized (lock) {
        try {
            lock.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
        JSONObject directionsJSONData = null;
        try {
            directionsJSONData = new JSONObject(internetQueryResults);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        results.setText(directionsJSONData.toString());

        String duration="Something went wrong in SendAndReceive()";
        //Now Parse Json Data into the Global variables
        try {
            // routesArray contains ALL routes
            JSONArray routesArray = directionsJSONData.getJSONArray("routes");
            // Grab the first route
            JSONObject route = routesArray.getJSONObject(0);
            // Take all legs from the route
            legsArray = route.getJSONArray("legs");
            // Grab first leg
            JSONObject leg = legsArray.getJSONObject(0);

            totalTravelTime=calculateTotalTravelTime();

            JSONArray steps = leg.getJSONArray("steps");
            setWaypoints();

            for (Waypoint way: waypointArray){
                if (way!=null) {
                    System.out.println("Jus' testing here");
                    System.out.println(way.toString());
                }
            }

            JSONObject step= steps.getJSONObject(0);

            JSONObject durationObject = leg.getJSONObject("duration");
            duration = durationObject.getString("text");

            results.setText(steps.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class getInternetData extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            String tempURL= params[0];
            tempURL=tempURL.replaceAll(" ","");
            Log.i("Passing URL",tempURL);
            HttpGet httpGet= new HttpGet(tempURL);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder= new StringBuilder();
            try{
                response=client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int byteData;
                while ((byteData= stream.read())!=-1){
                    stringBuilder.append((char)byteData);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Testing a second time");
            internetQueryResults =stringBuilder.toString();
            synchronized (lock) {
                lock.notify();
            }
            return stringBuilder.toString();
        }
    }




}

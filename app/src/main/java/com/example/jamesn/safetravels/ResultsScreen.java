package com.example.jamesn.safetravels;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.lucasr.twowayview.TwoWayView;

import java.text.DecimalFormat;


public class ResultsScreen extends ActionBarActivity {
    private TextView departureTime;
    private Waypoint[] waypointArrayCopy;
    private ListAdapter weatherPanelAdapter;
    private TwoWayView weatherPanels;
    static int hourDisplay;
    public static DateTime weatherQueryTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //Define all future UI objects in here in here.
        super.onCreate(savedInstanceState);
        SeekBar hourlySeekbar=(SeekBar)findViewById(R.id.hourlySeekbar);

        setContentView(R.layout.activity_results_screen);

       bugTest();

        //waypointArrayCopy=Waypoint.transfer;


        //hourlySeekbar.setMax(waypointArrayCopy[0].weatherData.length);
        Intent intent = getIntent();

        Long tempTimeStamp =intent.getLongExtra("timeOfRequest",0);
        weatherQueryTime= new DateTime(tempTimeStamp);


        weatherPanelAdapter = new WaypointAdapter(this,waypointArrayCopy);
        weatherPanels = (TwoWayView) findViewById(R.id.weatherPanels);
        weatherPanels.setAdapter(weatherPanelAdapter);
        setSeekbar();
        departureTime= (TextView) findViewById(R.id.leavingTime);

        /*System.out.println("Here's Some Test for ya!!");
        int wayCount=0;
        for (Waypoint way: waypointArrayCopy){
            System.out.println("STARTING WAYPOINT"+wayCount);
            wayCount++;
            for (int k=0; k<way.weatherData.length;k++){
                //System.out.println(way.weatherData[k]);
                if (way.weatherData[k]==null){
                    System.out.println("WARNING AT INDEX"+ k);
                }
            }
        }
        */
    }

    public void bugTest(){
        int waypointArrayLength=0;
        for (int i=0; i<Waypoint.transfer.length;i++){
            if (Waypoint.transfer[i]!=null){
                waypointArrayLength++;
            }
        }

        waypointArrayCopy=new Waypoint[waypointArrayLength];
        for (int j=0; j<waypointArrayLength;j++){
            if (Waypoint.transfer[j]!=null){
                waypointArrayCopy[j]=Waypoint.transfer[j];
            }
        }

        System.out.println("Bug Testing for the first waypoint in the array: ");
        for (int l=0; l<waypointArrayCopy[0].weatherData.length; l++){
            int totes = waypointArrayCopy[0].weatherData.length-1;
            System.out.println("Printing index"+ l+"/"+totes+":    "+waypointArrayCopy[0].weatherData[l]);
        }

        System.out.println("Printout of each waypoint attribute: ");
        int i=0;
        for (Waypoint way: waypointArrayCopy){

            System.out.println("Waypoint #: "+ i);
            System.out.println("Location: "+ way.location);
            System.out.println("Lat: "+ way.latitude);
            System.out.println("Lng: "+ way.longitude);
            System.out.println("ETA: "+ way.minimumETA);

            System.out.println("");
             i++;
        }

        String latLngPaste="";
        DecimalFormat latLng= new DecimalFormat("#.######");
        for (Waypoint way: waypointArrayCopy){
            latLngPaste=latLngPaste+latLng.format(way.latitude)+","+latLng.format(way.longitude)+" ";
        }
        System.out.println(latLngPaste);

    }

    public void setSeekbar(){
        TextView travelTime= (TextView) findViewById(R.id.travelTimeText);
        travelTime.setText("Total Trip Time: "+(waypointArrayCopy[waypointArrayCopy.length-1].minimumETA/60)+ " hours, "+(waypointArrayCopy[waypointArrayCopy.length-1].minimumETA%60)+" mins");
        SeekBar hourlySeekbar=(SeekBar)findViewById(R.id.hourlySeekbar);
        hourlySeekbar.setMax(waypointArrayCopy[0].weatherData.length-1);
        hourlySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                hourDisplay=seekBar.getProgress();
                departureTime.setText(hourDisplay+ " hours from now");
                weatherPanels.setAdapter(weatherPanelAdapter);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results_screen, menu);
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
}

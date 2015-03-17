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


public class ResultsScreen extends ActionBarActivity {
    private TextView testView;
    private TextView departureTime;
    private Waypoint[] waypointArrayCopy;
    private SeekBar hourlySeekbar;
    private ListAdapter weatherPanelAdapter;
    private TwoWayView weatherPanels;
    static int hourDisplay;
    public static DateTime weatherQueryTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //Define all future UI objects in here in here.
        super.onCreate(savedInstanceState);
        SeekBar hourlySeekbar=(SeekBar)findViewById(R.id.hourlySeekbar);




        setContentView(R.layout.activity_results_screen);
        waypointArrayCopy=Waypoint.transfer;


        //hourlySeekbar.setMax(waypointArrayCopy[0].weatherData.length);
        Intent intent = getIntent();

        Long tempTimeStamp =intent.getLongExtra("timeOfRequest",0);
        weatherQueryTime= new DateTime(tempTimeStamp);


        weatherPanelAdapter = new WaypointAdapter(this,waypointArrayCopy);
        weatherPanels = (TwoWayView) findViewById(R.id.weatherPanels);
        weatherPanels.setAdapter(weatherPanelAdapter);
        setSeekbar();
        departureTime= (TextView) findViewById(R.id.leavingTime);

    }

    public void setSeekbar(){
        SeekBar hourlySeekbar=(SeekBar)findViewById(R.id.hourlySeekbar);
        hourlySeekbar.setMax(waypointArrayCopy[0].weatherData.length-8); //TODO remove the -8 and figure out why it doesn't work without it

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

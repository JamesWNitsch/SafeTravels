package com.example.jamesn.safetravels;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;

import java.text.DecimalFormat;

public class WaypointAdapter extends ArrayAdapter<Waypoint> {
    public WaypointAdapter(Context context, Waypoint[] values) {
        super(context,R.layout.weather_panel,values);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater theInflator = LayoutInflater.from(getContext());
        View theView = theInflator.inflate(R.layout.weather_panel,
                parent,false);

        /*if (convertView == null)
        {
            LayoutInflater mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            theView = mInflater.inflate(R.layout.weather_panel, null);
        }*/

        if (getItem(position)!=null) {

            String summary = "Unable to load";
            String feelsLike="Unable to load";
            String temperature = "Unable to load";
            String precipitationChance="Unable to load";
            String precipitationIntensity= "Unable to load";
            String visibility="Unable to load";
            String dayOfWeek="Unable to load";
            String dayOfMonth="Unable to load";
            String month="Unable to load";
            String time="Unable to load";
            String timeZone="Unable to load";
            String location="Unable to load";
            String ETA = "Unable to load";

            try {
                summary = String.valueOf(getItem(position).weatherData[ResultsScreen.hourDisplay].get("summary"));
                feelsLike= String.valueOf(getItem(position).weatherData[ResultsScreen.hourDisplay].get("apparentTemperature"));
                temperature=  String.valueOf(getItem(position).weatherData[ResultsScreen.hourDisplay].get("temperature"));
                precipitationChance=  String.valueOf(Double.valueOf(String.valueOf(getItem(position).weatherData[ResultsScreen.hourDisplay].get("precipProbability")))*100);
                precipitationIntensity=  String.valueOf(getItem(position).weatherData[ResultsScreen.hourDisplay].get("precipIntensity"));

                String tempVisibility=String.valueOf(getItem(position).weatherData[ResultsScreen.hourDisplay].get("visibility"));
                DecimalFormat deci= new DecimalFormat("#.##");
                visibility=String.valueOf(deci.format(Double.valueOf(tempVisibility)*0.621371));
                //visibility= String.valueOf(getItem(position).weatherData[ResultsScreen.hourDisplay].get("visibility"));

                //Strings inherited directly from the Waypoint class
                location= getItem(position).location;
                timeZone= getItem(position).timezone;

                Long temp= ((getItem(position).weatherData[ResultsScreen.hourDisplay]).getLong("time"));
                DateTime hourShift =new DateTime(temp*1000).withZone(DateTimeZone.forTimeZone(getItem(position).timeZoneObject.toTimeZone()));
                DateTime waypointETA =ResultsScreen.weatherQueryTime.plusMinutes(getItem(position).minimumETA).plusHours(ResultsScreen.hourDisplay).withZone(DateTimeZone.forTimeZone(getItem(position).timeZoneObject.toTimeZone()));
                DateTimeFormatter roundedDisplayTime= DateTimeFormat.forPattern("hh:mma");

                dayOfWeek=hourShift.dayOfWeek().getAsText();
                dayOfMonth= hourShift.dayOfMonth().getAsText();
                month= hourShift.monthOfYear().getAsText();
                time=roundedDisplayTime.print(hourShift);

                ETA=roundedDisplayTime.print(waypointETA); //TODO ETA calculations don't take into account timezone!




            } catch (JSONException e) {
                e.printStackTrace();
            }


            TextView summaryTextView = (TextView) theView.findViewById(R.id.summaryText);
            TextView apparentTempTextView = (TextView) theView.findViewById(R.id.feelsLikeText);
            TextView temperatureTextView = (TextView) theView.findViewById(R.id.rawTempText);
            TextView precipitationChanceTextView = (TextView) theView.findViewById(R.id.precipChanceText);
            TextView precipIntensityTextView = (TextView) theView.findViewById(R.id.precipIntensityText);
            TextView visibilityTextView = (TextView) theView.findViewById(R.id.visibilityText);
            TextView dayOfWeekTextView = (TextView) theView.findViewById(R.id.dayOfWeekText);
            TextView dayOfMonthTextView = (TextView) theView.findViewById(R.id.dayOfMonthText);
            TextView monthTextView = (TextView) theView.findViewById(R.id.monthText);
            TextView timeTextView = (TextView) theView.findViewById(R.id.timeOfDayText);
            TextView timeZoneTextView = (TextView) theView.findViewById(R.id.timeZoneText);
            TextView locationTextView = (TextView) theView.findViewById(R.id.locationText);
            TextView etaTextView = (TextView) theView.findViewById(R.id.etaText);

            summaryTextView.setText(summary);
            apparentTempTextView.setText("Feels like: "+feelsLike+"℉");
            temperatureTextView.setText(temperature+"℉");
            precipitationChanceTextView.setText(precipitationChance+"%");
            precipIntensityTextView.setText(precipitationIntensity+" inches");
            visibilityTextView.setText(visibility+" miles");
            timeZoneTextView.setText(timeZone);
            locationTextView.setText(location);


            dayOfWeekTextView.setText(dayOfWeek+" ");
            dayOfMonthTextView.setText(dayOfMonth);
            monthTextView.setText(month+" ");
            timeTextView.setText(time);
            etaTextView.setText(ETA);


            return theView;

        }
        LayoutInflater mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        theView = mInflater.inflate(R.layout.weather_panel, null); //TODO
        return theView;
    }
}

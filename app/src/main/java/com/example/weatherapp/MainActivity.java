package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    Button szukaj;
    String cityName = "Gliwice";
    EditText cityText;

    String latitude = "50.2833";
    String longitude = "18.670002";
    String tempDay, tempNight, pressure, clouds, feels_like_day, feels_like_night, wind_speed, rain;
    private String date;

    private TextView dateTimeDisplay;
    private TextView town;
    private TextView temp;
    private TextView desc;
    private ImageView image;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;



    private static final int MY_PERMISSION_REQUEST_LOCATIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTimeDisplay = findViewById(R.id.dateTextView);

        //pobranie aktualnej daty i ustawienie jej
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());
        dateTimeDisplay.setText(date);
        image = findViewById(R.id.wheather_image);
        town = findViewById(R.id.town);
        temp = findViewById(R.id.temp);
        desc = findViewById(R.id.desc);
        cityText = findViewById(R.id.cityText);
        szukaj = findViewById(R.id.buttonpobierz);
        szukaj.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    getCity();
                    pobierzDane();
                    setForecast();
                } catch(IllegalArgumentException | IOException e) {
                    Toast.makeText(MainActivity.this, "bad argument" + e, Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getData() {
        try {
            URL PogodynkaEndPoint = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=6bda4862dcc638f53d7a4758b0c6efab&lang=pl");
            HttpsURLConnection PogodynkaConnection = (HttpsURLConnection) PogodynkaEndPoint.openConnection();
            PogodynkaConnection.setRequestProperty("Accept", "application/json");
            if (PogodynkaConnection.getResponseCode() == 200) {
               InputStreamReader is = new InputStreamReader(PogodynkaConnection.getInputStream());
               Gson gson = new Gson();
               Forecast forecast = gson.fromJson(is, Forecast.class);
               PogodynkaConnection.disconnect();
               System.out.println(forecast); //wyswietla

               tempDay = forecast.getCurrent().getTemp();
               tempNight = forecast.getDaily()[0].getTemp().getNight();
               pressure = forecast.getCurrent().getPressure();
               feels_like_day = forecast.getCurrent().getFeels_like();
               feels_like_night = forecast.getDaily()[0].getFeels_like().getNight();
               wind_speed = forecast.getCurrent().getWind_speed();
               rain = forecast.getDaily()[0].getRain();
               clouds = forecast.getCurrent().getClouds();

               if(rain == null){
                   rain = "0";
               }

               System.out.println("Todays temp: " + tempDay + " night temp: " + tempNight);
               System.out.println("rain: " + rain + " clouds: " + clouds);

               setImage(Double.parseDouble(rain), Double.parseDouble(clouds), Double.parseDouble(tempDay), Double.parseDouble(wind_speed));
            } else {
                    //error
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pobierzDane(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        });
    }

    public void getCity() throws IOException {
        cityName = (String) cityText.getText().toString();
        System.out.println(cityName); //jest git
        cityLocalization(cityName);
        town.setText(cityName);
    }

    public void cityLocalization(String city) throws IOException {
        Geocoder gc = new Geocoder(MainActivity.this);
        List<Address> ads = gc.getFromLocationName(city, 5);
        while (ads.size() == 0) {
            ads = gc.getFromLocationName(city, 5);
        }
        if (ads.size() > 0) {
            Address addr = ads.get(0);
            latitude = String.valueOf(addr.getLatitude());
            longitude = String.valueOf(addr.getLongitude());
            System.out.println(ads.toString());
            System.out.println("Szerokość: " + latitude);
            System.out.println("Długość: " + longitude);
        }
    }

    public void setForecast(){
        String temperature = tempDay + "/" + tempNight + "°C  " + feels_like_day + "/" + feels_like_night +"°C";
        System.out.println(temperature);
        String description = "Ciśnienie: " + pressure + "hPa  " + "Wiatr: " + wind_speed + "km/h" + "  Opady: " + rain + "%" + "  Zachmurzenie: " + clouds + "%";
        temp.setText(temperature);
        desc.setText(description);
    }

    public void setImage(double rain, double clouds, double temp, double wind){
        if(rain == 0 && clouds < 15){
            image.setImageResource(R.drawable.sun);
        } else if(rain == 0 && clouds < 50 ){
            image.setImageResource(R.drawable.sun_cloud);
        } else if( rain == 0 && clouds < 70){
            image.setImageResource(R.drawable.sun_2clouds);
        } else if( rain > 0 && rain < 60){
            image.setImageResource(R.drawable.clouds);
        } else if( rain > 60){
            image.setImageResource(R.drawable.rain);
        } else if(rain > 60 && clouds > 70 && wind > 50 ){
            image.setImageResource(R.drawable.rain_and_storm);
        } else if( temp < -2 && clouds > 20){
            image.setImageResource(R.drawable.another);
        } else {
            image.setImageResource(R.drawable.wheather);
        }
    }
}

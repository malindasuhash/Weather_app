package app.com.example.malindasuhash.weatherapptake1.services;

import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherRequest;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherResults;
import app.com.example.malindasuhash.weatherapptake1.utils.OpenWeatherCaller;

/**
 * Implementation of the Async version of the service to get weather data.
 */
public class OpenWeatherClientAsync extends WeatherRequest.Stub {

    private final String TAG = this.getClass().getSimpleName();

    private String mOpenWebEndpointPrefix = "http://api.openweathermap.org/data/2.5/weather?q=";

    @Override
    public void getCurrentWeather(String Weather, WeatherResults results) throws RemoteException {

        String endpoint = mOpenWebEndpointPrefix + Weather;

        Log.i(TAG, "Looking for endpoint " + endpoint);

        List<WeatherData> data = OpenWeatherCaller.getResults(endpoint);

        results.sendResults(data);
    }
}

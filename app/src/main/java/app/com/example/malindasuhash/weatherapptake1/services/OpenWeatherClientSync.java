package app.com.example.malindasuhash.weatherapptake1.services;

import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherCall;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.utils.OpenWeatherCaller;

/**
 * Class to call the open weather service.
 */
class OpenWeatherClientSync extends WeatherCall.Stub {

    private final String TAG = this.getClass().getSimpleName();

    private String mOpenWebEndpointPrefix = "http://api.openweathermap.org/data/2.5/weather?q=";

    @Override
    public synchronized List<WeatherData> getCurrentWeather(String Weather) throws RemoteException {

        String endpoint = mOpenWebEndpointPrefix + Weather;

        Log.i(TAG, "Looking for endpoint " + endpoint);

        List<WeatherData> data = OpenWeatherCaller.getResults(endpoint);

        //Log.i(TAG, "Results received count is " + data != null ? Integer.toString(data.size()) : "0");

        return data;
    }
}

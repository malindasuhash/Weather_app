package app.com.example.malindasuhash.weatherapptake1.services;

import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherCall;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.utils.OpenWeatherCaller;

/**
 * Responsible for calling the Open weather endpoint.
 * This inherits the IPC stub that is defined in AIDL.
 */
class OpenWeatherClientSync extends WeatherCall.Stub {

    private final String TAG = this.getClass().getSimpleName();

    private String mOpenWebEndpointPrefix = "http://api.openweathermap.org/data/2.5/weather?q=";

    @Override
    public List<WeatherData> getCurrentWeather(String Weather) throws RemoteException {

        // Does the endpoint needs to URL encoded?
        String endpoint = mOpenWebEndpointPrefix + Weather;

        Log.i(TAG, "Looking for endpoint " + endpoint);

        List<WeatherData> data = OpenWeatherCaller.getResults(endpoint);

        return data;
    }
}

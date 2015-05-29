package app.com.example.malindasuhash.weatherapptake1.services.clients;

import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherRequest;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherResults;
import app.com.example.malindasuhash.weatherapptake1.utils.EndpointBuilder;
import app.com.example.malindasuhash.weatherapptake1.utils.OpenWeatherCaller;

/**
 * Implementation of the Async version of the service to get weather data.
 */
public class OpenWeatherClientAsync extends WeatherRequest.Stub {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void getCurrentWeather(String Weather, WeatherResults results) throws RemoteException {

        String endpoint = EndpointBuilder.build(Weather);

        Log.i(TAG, "Looking for endpoint " + endpoint);

        List<WeatherData> data = OpenWeatherCaller.getResults(endpoint);

        results.sendResults(data);
    }
}

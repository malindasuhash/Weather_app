package app.com.example.malindasuhash.weatherapptake1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service that calls the Open weather service
 * using one-way AIDL methods.
 */
public class WeatherServiceAsync extends Service {

    private final OpenWeatherClientAsync mClientAsync;

    public WeatherServiceAsync() {
        mClientAsync = new OpenWeatherClientAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mClientAsync;
    }
}

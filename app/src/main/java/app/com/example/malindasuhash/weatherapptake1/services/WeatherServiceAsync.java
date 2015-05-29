package app.com.example.malindasuhash.weatherapptake1.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import app.com.example.malindasuhash.weatherapptake1.services.clients.OpenWeatherClientAsync;

/**
 * The service that calls the Open weather service
 * using one-way AIDL methods.
 */
public class WeatherServiceAsync extends Service {

    private final OpenWeatherClientAsync mClientAsync;

    public static Intent makeIntent(Context context)
    {
        Intent intent = new Intent(context, WeatherServiceAsync.class);
        return intent;
    }

    public WeatherServiceAsync() {
        mClientAsync = new OpenWeatherClientAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mClientAsync;
    }
}

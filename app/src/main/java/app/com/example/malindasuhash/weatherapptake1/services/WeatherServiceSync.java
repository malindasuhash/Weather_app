package app.com.example.malindasuhash.weatherapptake1.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Sync Weather service calling the external web service to get data.
 */
public class WeatherServiceSync extends Service {

    private final String TAG = this.getClass().getSimpleName();

    private OpenWeatherClientSync mOpenWeatherClient;

    public WeatherServiceSync() {
        mOpenWeatherClient = new OpenWeatherClientSync();
    }

    public static Intent makeIntent(Context context)
    {
        Log.i("WeatherServiceSync", "Creating the intent");
        Intent intent = new Intent(context, WeatherServiceSync.class);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "OnBind called.");
        return mOpenWeatherClient;
    }
}


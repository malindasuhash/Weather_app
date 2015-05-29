package app.com.example.malindasuhash.weatherapptake1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.activities.WeatherActivity;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherRequest;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherResults;
import app.com.example.malindasuhash.weatherapptake1.services.WeatherServiceAsync;

/**
 * The operational handler for the WeatherServiceAsync bound service.
 */
public class WeatherOpsAsync extends WeatherOpsBase {

    private final String TAG = this.getClass().getName();

    private WeatherRequest mWeatherRequest;
    private List<WeatherData> mData;

    // Service connection for the bound service.
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mWeatherRequest = WeatherRequest.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mWeatherRequest = null;
        }
    };

    private WeatherResultCallback mCallback;

    public WeatherOpsAsync(WeatherActivity weatherActivity) {
        super(weatherActivity);
        mCallback = new WeatherResultCallback(mData);
    }

    @Override
    protected void DoWork() {
        Log.i(TAG, "Calling the async bound service.");

        try {
            mWeatherRequest.getCurrentWeather(getLocation(), mCallback);
        } catch (RemoteException e) {
             e.printStackTrace();
        }
    }

    @Override
    public void start() {
        super.start();
        Log.i(TAG, "Binding to Asyc service. Creating intent");
        Intent intent = WeatherServiceAsync.makeIntent(mWeatherActivity.get());

        Log.i(TAG, "Binding to service asyc");
        mWeatherActivity.get().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stop() {
        super.stop();
        Log.i(TAG, "Unbinding from Async service.");
        mWeatherActivity.get().unbindService(serviceConnection);
    }

    /**
     * Receives data from async bound service and binds to the UI.
     * @param weatherData
     */
    private void bindDataToUI(List<WeatherData> weatherData)
    {
        mWeatherActivity.get().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Handling callback in the UI");
            }
        });
    }

    /**
     * Handles the callback from the bound async service.
     */
    class WeatherResultCallback extends WeatherResults.Stub
    {
        private final String TAG = this.getClass().getName();

        public WeatherResultCallback(List<WeatherData> weatherData)
        {
        }

        @Override
        public void sendResults(List<WeatherData> results) throws RemoteException {
            Log.i(TAG, "Callback received from bound async service.");
            bindDataToUI(results);
        }
    }
}

package app.com.example.malindasuhash.weatherapptake1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.activities.WeatherActivity;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherCall;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.services.WeatherServiceSync;
import app.com.example.malindasuhash.weatherapptake1.utils.Formatter;

/**
 * The operational handler for the WeatherServiceSync bound service.
 */
public class WeatherOpsSync extends WeatherOpsBase {

    private final String TAG = this.getClass().getSimpleName();

    private WeakReference<EditText> mLocation;
    private WeakReference<ProgressBar> mProgressBar;

    private WeakReference<TextView> mWeatherName;
    private WeakReference<TextView> mWeatherSpeed;
    private WeakReference<TextView> mWeatherDeg;
    private WeakReference<TextView> mWeatherTemp;
    private WeakReference<TextView> mWeatherHumidity;
    private WeakReference<TextView> mWeatherSunrise;
    private WeakReference<TextView> mWeatherSunset;

    // Very simple map to store the weather information
    private HashMap<String,CacheEntry> mCache = new HashMap<>();

    private WeatherData mSyncWeatherData;
    private WeatherCall mWeatherCall;

    private volatile boolean mAsyncTaskStillExecuting;

    private WeatherGetterAsyncTask.TaskExecutionState state = new WeatherGetterAsyncTask.TaskExecutionState() {

        @Override
        public void Finished(final List<WeatherData> data) {
            mAsyncTaskStillExecuting = false;

            // The callback is currently is being received
            // in the UI thread. However just to be sure queueing
            // in UI thread.
            mWeatherActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data != null) {
                        addToCache(data.get(0));
                        bindToUi(data.get(0));
                        mSyncWeatherData = data.get(0);
                    } else {
                        Toast.makeText(mWeatherActivity.get(), "No data", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    private ServiceConnection mSyncServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "Weather sync callback received.");
            mWeatherCall = WeatherCall.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "Weather sync disconnected.");
            mWeatherCall = null;
        }
    };

    public WeatherOpsSync(WeatherActivity weatherActivity)
    {
        super(weatherActivity);
        initialiseFields();
    }

    private String getLocation()
    {
        return mLocation.get().getText().toString().trim();
    }

    @Override
    protected void DoWork() {
        getDataAsyncTask();
    }

    @Override
    public void onConfigurationChange(WeatherActivity activity)
    {
        Log.i(TAG, "Handling configuration change.");
        mWeatherActivity = new WeakReference<>(activity);

        Log.i(TAG, "Initialising fields.");
        initialiseFields();
    }

    @Override
    public void stop() {
        super.stop();

        Log.i(TAG, "Stopping Sync service.");
        unbindFromSyncService();
    }

    @Override
    public void start() {
        super.start();

        Log.i(TAG, "Starting Sync service.");
        bindToSyncService();
    }

    private void initialiseFields()
    {
        Log.i(TAG, "Initialising fields as weak references.");
        mLocation = new WeakReference<>((EditText)mWeatherActivity.get().findViewById(R.id.location));
        mProgressBar = new WeakReference<>((ProgressBar)mWeatherActivity.get().findViewById(R.id.progress));

        mWeatherName = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_name));
        mWeatherSpeed = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_speed));
        mWeatherDeg = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_deg));
        mWeatherTemp = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_temp));
        mWeatherHumidity = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_humidity));
        mWeatherSunrise = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_sunrise));
        mWeatherSunset = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_sunset));

        mProgressBar.get().setVisibility(mAsyncTaskStillExecuting ? View.VISIBLE : View.INVISIBLE);

        bindResults();
    }

    private void bindResults()
    {
        if (mSyncWeatherData != null)
        {
           bindToUi(mSyncWeatherData);
        }
    }

    private void bindToUi(WeatherData data)
    {
        Log.i(TAG, "Binding weather data to UI " + data);
        mWeatherName.get().setText(data.getName());
        mWeatherSpeed.get().setText(Formatter.formatSpeed(data.getSpeed()));
        mWeatherDeg.get().setText(Double.toString(data.getDeg()));
        mWeatherTemp.get().setText(Double.toString(data.getTemp()));
        mWeatherHumidity.get().setText(Formatter.formatHumidity(data.getHumidity()));
        mWeatherSunrise.get().setText(Long.toString(data.getSunrise()));
        mWeatherSunset.get().setText(Long.toString(data.getSunset()));

        mProgressBar.get().setVisibility(View.INVISIBLE);
        mAsyncTaskStillExecuting = true;
    }

    private void getDataAsyncTask()
    {
        WeatherData cacheData = getFromCache(getLocation());

        if (cacheData == null)
        {
            mProgressBar.get().setVisibility(View.VISIBLE);
            Log.i(TAG, "Stating the async task.");
            WeatherGetterAsyncTask weatherGetterAsyncTask = new WeatherGetterAsyncTask(state, mWeatherCall);
            weatherGetterAsyncTask.execute(getLocation());
            mAsyncTaskStillExecuting = true;
        } else
        {
            bindToUi(cacheData);
        }
    }

    private void bindToSyncService()
    {
        Log.i(TAG, "Binding to the Sync weather service.");

        Intent intent = WeatherServiceSync.makeIntent(mWeatherActivity.get());

        mWeatherActivity.get().bindService(intent, mSyncServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindFromSyncService()
    {
        Log.i(TAG, "unbinding from the Sync weather service.");

        mWeatherActivity.get().unbindService(mSyncServiceConnection);
    }

    private synchronized void addToCache(WeatherData data)
    {
        CacheEntry entry = new CacheEntry();
        entry.CachedOn = new Date().getTime() + 10 * 1000;
        entry.Data = mSyncWeatherData;

        // Add to cache
        mCache.put(data.getName().toLowerCase(), entry);
    }

    public synchronized WeatherData getFromCache(String location)
    {
        Date date = new Date();
        CacheEntry data = mCache.get(location.toLowerCase());

        if (data != null && date.getTime() < data.CachedOn)
        {
            Log.i(TAG, "Bound from cache");
            return data.Data;
        }

        return null;
    }

    class CacheEntry
    {
        public long CachedOn;

        public WeatherData Data;
    }
}

package app.com.example.malindasuhash.weatherapptake1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.activities.WeatherActivity;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherCall;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherRequest;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherResults;
import app.com.example.malindasuhash.weatherapptake1.services.WeatherServiceAsync;
import app.com.example.malindasuhash.weatherapptake1.services.WeatherServiceSync;
import app.com.example.malindasuhash.weatherapptake1.utils.CacheManager;
import app.com.example.malindasuhash.weatherapptake1.utils.Formatter;

/**
 * The operational handler for the WeatherServiceSync bound service.
 */
public class WeatherOps extends WeatherOpsBase {

    private final String TAG = this.getClass().getSimpleName();

    private final int CACHE_DURATION_IN_SECONDS = 10;

    private CacheManager mCacheManager;
    private WeakReference<ProgressBar> mProgressBar;

    private WeakReference<TextView> mWeatherName;
    private WeakReference<TextView> mWeatherSpeed;
    private WeakReference<TextView> mWeatherDeg;
    private WeakReference<TextView> mWeatherTemp;
    private WeakReference<TextView> mWeatherHumidity;
    private WeakReference<TextView> mWeatherSunrise;
    private WeakReference<TextView> mWeatherSunset;

    private WeatherData mSyncWeatherData;
    private WeatherCall mWeatherCall;
    private WeatherRequest mWeatherRequest;
    private WeatherResultCallback mCallback;

    private volatile boolean mAsyncTaskStillExecuting;

    private WeatherGetterAsyncTask.TaskExecutionState state = new WeatherGetterAsyncTask.TaskExecutionState() {

        @Override
        public void Finished(final List<WeatherData> data) {
            mAsyncTaskStillExecuting = false;
            bindResults(data);
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

    // Service connection for the bound service.
    private ServiceConnection mASyncserviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mWeatherRequest = WeatherRequest.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mWeatherRequest = null;
        }
    };

    public WeatherOps(WeatherActivity weatherActivity)
    {
        super(weatherActivity);
        mCallback = new WeatherResultCallback();
        mCacheManager = new CacheManager(CACHE_DURATION_IN_SECONDS);
        initialiseFields();
    }

    @Override
    protected void DoWork() {
        getDataAsyncTask();
    }

    @Override
    protected void DoWorkAsync() {
        getDataFromAsyncService();
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

        Log.i(TAG, "Stopping services.");
        unbindFromSyncService();
        unbindFromAsyncService();
    }

    @Override
    public void start() {
        super.start();

        Log.i(TAG, "Starting services.");
        bindToSyncService();
        bindToASyncService();
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

        ArrayList<WeatherData> data = new ArrayList<>();

        if (mSyncWeatherData != null)
            data.add(mSyncWeatherData);

        bind(data);
    }

    private void bindResults(final List<WeatherData> data)
    {
        mWeatherActivity.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bind(data);
            }
        });
    }

    private void bind(List<WeatherData> data) {
        if (data != null && data.size() > 0) {
            mCacheManager.set(data.get(0).getName(), data.get(0));
            bindToUi(data.get(0));
            mSyncWeatherData = data.get(0);
        } else {
            Toast.makeText(mWeatherActivity.get(), "No data", Toast.LENGTH_LONG).show();
        }
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
        WeatherData cacheData = mCacheManager.get(getLocation());

        Log.i(TAG, "Found in cache " + getLocation() + " " + (cacheData != null));

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

    private void getDataFromAsyncService()
    {
        Log.i(TAG, "Calling the async bound service. inside " + Thread.currentThread().getId());

        try {
            mWeatherRequest.getCurrentWeather(getLocation(), mCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sync service.
     */
    private void bindToSyncService()
    {
        Log.i(TAG, "Binding to the Sync weather service.");
        Intent intent = WeatherServiceSync.makeIntent(mWeatherActivity.get());
        mWeatherActivity.get().bindService(intent, mSyncServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindToASyncService()
    {
        Log.i(TAG, "Binding to the Async weather service.");
        Intent intent = WeatherServiceAsync.makeIntent(mWeatherActivity.get());
        mWeatherActivity.get().bindService(intent, mASyncserviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbinds from Sync weather service if still connected.
     */
    private void unbindFromSyncService()
    {
        if (mSyncServiceConnection != null) // To ensure we are still connected.
        {
            Log.i(TAG, "unbinding from the Sync weather service.");
            mWeatherActivity.get().unbindService(mSyncServiceConnection);
        }

    }

    /**
     * Unbinds from Async weather service if still connected.
     */
    private void unbindFromAsyncService()
    {
        if (mASyncserviceConnection != null)
        {
            Log.i(TAG, "unbinding from the Sync weather service.");
            mWeatherActivity.get().unbindService(mASyncserviceConnection);
        }
    }

    /**
     * Handles the callback from the bound async service.
     */
    class WeatherResultCallback extends WeatherResults.Stub
    {
        private final String TAG = this.getClass().getName();

        @Override
        public void sendResults(List<WeatherData> results) throws RemoteException {
            Log.i(TAG, "Callback received from bound async service.");
            bindResults(results);
        }
    }
}

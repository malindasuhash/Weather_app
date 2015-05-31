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

    // Callback for the AsyncTask to update the UI.
    // This allows to update the UI through the WeatherOps
    // rather than updating the UI directly in the AsyncTask
    // itself.
    private WeatherGetterAsyncTask.TaskExecutionState state = new WeatherGetterAsyncTask.TaskExecutionState() {
        @Override
        public void Finished(final List<WeatherData> data) {
            mAsyncTaskStillExecuting = false;
            bindResults(data);
        }
    };

    // Service connection for Sync weather service.
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

    // Service connection for the async weather service.
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

    /**
        Calls the Sync Weather service to get data.
     */
    @Override
    protected void DoWork() {
        WeatherData cacheData = mCacheManager.get(getLocation());
        if (cacheData == null)
        {
            getDataFromService(true);
        }
        else {
            bindToUi(cacheData, false);
            Toast.makeText(mWeatherActivity.get(),
                    mWeatherActivity.get().getString(R.string.from_cache), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     Calls the Async Weather service to get data.
     */
    @Override
    protected void DoWorkAsync() {
        WeatherData cacheData = mCacheManager.get(getLocation());
        if (cacheData == null)
        {
            getDataFromService(false);
        }
        else {
            bindToUi(cacheData, false);
            Toast.makeText(mWeatherActivity.get(),
                    mWeatherActivity.get().getString(R.string.from_cache), Toast.LENGTH_SHORT).show();
        }
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

        bindToUi(mSyncWeatherData, true);
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
            bindToUi(data.get(0), false);
            mSyncWeatherData = data.get(0);
        } else {
            mSyncWeatherData = null;
            bindToUi(mSyncWeatherData, false);
            reset();
            Toast.makeText(mWeatherActivity.get(),
                    mWeatherActivity.get().getString(R.string.sorry_no_data), Toast.LENGTH_LONG).show();
        }
    }

    private void bindToUi(WeatherData data, boolean onloading)
    {
        if (data != null) {
            Log.i(TAG, "Binding weather data to UI " + data);
            mWeatherName.get().setText(data.getName());
            mWeatherSpeed.get().setText(Formatter.formatSpeed(data.getSpeed()));
            mWeatherDeg.get().setText(Double.toString(data.getDeg()));
            mWeatherTemp.get().setText(Formatter.formatTemp(data.getTemp()));
            mWeatherHumidity.get().setText(Formatter.formatHumidity(data.getHumidity()));
            mWeatherSunrise.get().setText(Formatter.formatDate(data.getSunrise()));
            mWeatherSunset.get().setText(Formatter.formatDate(data.getSunset()));
        }

        if (onloading && mAsyncTaskStillExecuting) {
            mProgressBar.get().setVisibility(View.VISIBLE);
        } else
        {
            mProgressBar.get().setVisibility(View.INVISIBLE);
            mAsyncTaskStillExecuting = false;
        }
    }

    private void reset()
    {
        mWeatherName.get().setText("");
        mWeatherSpeed.get().setText("");
        mWeatherDeg.get().setText("");
        mWeatherTemp.get().setText("");
        mWeatherHumidity.get().setText("");
        mWeatherSunrise.get().setText("");
        mWeatherSunset.get().setText("");
    }

    /**
     * Executes the Sync or Async strategy based on the provided
     * value.
     */
    private void getDataFromService(boolean useSyncService)
    {
        if (useSyncService)
        {
            Log.i(TAG, "Stating the async task.");
            WeatherGetterAsyncTask weatherGetterAsyncTask = new WeatherGetterAsyncTask(state, mWeatherCall);
            weatherGetterAsyncTask.execute(getLocation());
        }
        else
        {
            Log.i(TAG, "Calling the async bound service. inside " + Thread.currentThread().getId());

            try {
                mWeatherRequest.getCurrentWeather(getLocation(), mCallback);
                Log.i(TAG, "Request sent to service");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        mProgressBar.get().setVisibility(View.VISIBLE);
        mAsyncTaskStillExecuting = true;
    }

    /**
     * Sync service.
     */
    private void bindToSyncService()
    {
        Log.i(TAG, "Binding to the Sync weather service.");
        Intent intent = WeatherServiceSync.makeIntent(mWeatherActivity.get().getApplicationContext());
        mWeatherActivity.get().bindService(intent, mSyncServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindToASyncService()
    {
        Log.i(TAG, "Binding to the Async weather service.");
        Intent intent = WeatherServiceAsync.makeIntent(mWeatherActivity.get().getApplicationContext());
        mWeatherActivity.get().bindService(intent, mASyncserviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbinds from Sync weather service if still connected.
     */
    private void unbindFromSyncService()
    {
        if (mSyncServiceConnection != null && !mWeatherActivity.get().isChangingConfigurations()) // To ensure we are still connected.
        {
            Log.i(TAG, "unbinding from the Sync weather service.");
            mWeatherActivity.get().getApplicationContext().unbindService(mSyncServiceConnection);
        }
    }

    /**
     * Unbinds from Async weather service if still connected.
     */
    private void unbindFromAsyncService()
    {
        if (mASyncserviceConnection != null && !mWeatherActivity.get().isChangingConfigurations())
        {
            Log.i(TAG, "unbinding from the Sync weather service.");
            mWeatherActivity.get().getApplicationContext().unbindService(mASyncserviceConnection);
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
            Log.i(TAG, "Callback received from bound async service. Attempt to bound to UI.");
            bindResults(results);
        }
    }
}

package app.com.example.malindasuhash.weatherapptake1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.activities.WeatherActivity;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherCall;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.services.WeatherServiceSync;

/**
 * This class contains the operations of the weather application.
 * The goal is to remove keep these operations out of the
 * activity and retained using the RetainedFragmentManager.
 */
public class WeatherOps extends WeatherOpsBase {

    private final String TAG = this.getClass().getSimpleName();

    private WeakReference<EditText> mLocation;
    private WeakReference<ProgressBar> mProgressBar;

    private WeakReference<Button> mGetWeatherSync;
    private WeakReference<Button> mGetWeatherAsync;

    private WeakReference<TextView> mWeatherName;
    private WeakReference<View> mSummary;
    private WeakReference<TextView> mWeatherSpeed;
    private WeakReference<TextView> mWeatherDeg;
    private WeakReference<TextView> mWeatherTemp;
    private WeakReference<TextView> mWeatherHumidity;
    private WeakReference<TextView> mWeatherSunrise;
    private WeakReference<TextView> mWeatherSunset;

    private WeatherData mSyncWeatherData;
    private WeatherCall mWeatherCall;

    private volatile boolean mAsyncTaskStillExecuting;
    private volatile boolean mReceiveComplete;

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
                    mProgressBar.get().setVisibility(View.INVISIBLE);
                    mGetWeatherSync.get().setEnabled(true);
                    mGetWeatherAsync.get().setEnabled(true);

                    if (data != null) {
                        mReceiveComplete = true;
                        mSyncWeatherData = data.get(0);
                        bindResults();
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

    public WeatherOps(WeatherActivity weatherActivity)
    {
        super(weatherActivity);

        initialiseFields();
    }

    @Override
    protected void DoWork() {
        runInitTask();
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
        mGetWeatherAsync = new WeakReference<>((Button)mWeatherActivity.get().findViewById(R.id.weatherAsyc));
        mGetWeatherSync = new WeakReference<>((Button)mWeatherActivity.get().findViewById(R.id.weatherSync));

        mWeatherName = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_name));
        mWeatherSpeed = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_speed));
        mWeatherDeg = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_deg));
        mWeatherTemp = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_temp));
        mWeatherHumidity = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_humidity));
        mWeatherSunrise = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_sunrise));
        mWeatherSunset = new WeakReference<>((TextView)mWeatherActivity.get().findViewById(R.id.weather_sunset));
        mSummary = new WeakReference<>((View)mWeatherActivity.get().findViewById(R.id.desc));

        mProgressBar.get().setVisibility(mAsyncTaskStillExecuting ? View.VISIBLE : View.INVISIBLE);
        mSummary.get().setVisibility(View.VISIBLE);
        bindResults();
    }

    private void bindResults()
    {
        if (mReceiveComplete && mSyncWeatherData != null)
        {
            Log.i(TAG, "Binding weather data to UI " + mSyncWeatherData);
            mWeatherName.get().setText(mSyncWeatherData.getName());
            mWeatherSpeed.get().setText(Double.toString(mSyncWeatherData.getSpeed()));
            mWeatherDeg.get().setText(Double.toString(mSyncWeatherData.getDeg()));
            mWeatherTemp.get().setText(Double.toString(mSyncWeatherData.getTemp()));
            mWeatherHumidity.get().setText(Double.toString(mSyncWeatherData.getHumidity()));
            mWeatherSunrise.get().setText(Long.toString(mSyncWeatherData.getSunrise()));
            mWeatherSunset.get().setText(Long.toString(mSyncWeatherData.getSunset()));
        }
    }

    private void runInitTask()
    {
        Log.i(TAG, "Executing sync task get whether data.");

        mProgressBar.get().setVisibility(View.VISIBLE);
        mGetWeatherAsync.get().setEnabled(false);
        mGetWeatherSync.get().setEnabled(false);
        Log.i(TAG, "Disabled both buttons");

        Log.i(TAG, "Stating the async task.");
        WeatherGetterAsyncTask weatherGetterAsyncTask = new WeatherGetterAsyncTask(state, mWeatherCall);
        weatherGetterAsyncTask.execute(mLocation.get().getText().toString());

        mAsyncTaskStillExecuting = true;
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
}

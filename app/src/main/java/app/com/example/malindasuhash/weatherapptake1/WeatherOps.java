package app.com.example.malindasuhash.weatherapptake1;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * This class contains the operations of the weather application.
 * The goal is to remove keep these operations out of the
 * activity and retained using the RetainedFragmentManager.
 */
public class WeatherOps {

    private final String TAG = this.getClass().getSimpleName();

    private WeakReference<WeatherActivity> mWeatherActivity;

    private WeakReference<EditText> mLocation;
    private WeakReference<ProgressBar> mProgressBar;

    private WeakReference<Button> mGetWeatherSync;
    private WeakReference<Button> mGetWeatherAsync;

    private volatile boolean mAsyncTaskStillExecuting;

    private WeatherGetterAsyncTask.TaskExecutionState state = new WeatherGetterAsyncTask.TaskExecutionState() {

        @Override
        public void Finished() {
            mAsyncTaskStillExecuting = false;

            // The finish method is called from onPostExecute in AsyncTask.
            // Therefore the following line is executed in UI thread.
            // No need to schedule the call using runOnUI...
            mProgressBar.get().setVisibility(View.INVISIBLE);
        }
    };

    public WeatherOps(WeatherActivity weatherActivity)
    {
        this.mWeatherActivity = new WeakReference<>(weatherActivity);

        initialiseFields();
    }

    public void getCurrentWeatherSync()
    {
        if (validate())
        {
            runTask();
        }
    }

    public void getCurrentWeatherAsync()
    {
        if (validate())
        {
            // Do work
        }
    }

    private boolean validate()
    {
        String location = mLocation.get().getText().toString();

        Log.i(TAG, "Validating " + location);

        if (location.trim().length() == 0)
        {
            Toast.makeText(mWeatherActivity.get(), R.string.location_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void onConfigurationChange(WeatherActivity activity)
    {
        Log.i(TAG, "Handling configuration change.");

        mWeatherActivity = new WeakReference<>(activity);

        Log.i(TAG, "Initialising fields.");

        initialiseFields();
    }

    private void initialiseFields()
    {
        mLocation = new WeakReference<>((EditText)mWeatherActivity.get().findViewById(R.id.location));
        mProgressBar = new WeakReference<>((ProgressBar)mWeatherActivity.get().findViewById(R.id.progress));
        mProgressBar.get().setVisibility(mAsyncTaskStillExecuting ? View.VISIBLE : View.INVISIBLE);
    }

    private void runTask()
    {
        Log.i(TAG, "Executing sync task get whether data.");

        mProgressBar.get().setVisibility(View.VISIBLE);
        WeatherGetterAsyncTask weatherGetterAsyncTask = new WeatherGetterAsyncTask(state);
        weatherGetterAsyncTask.execute("hello");

        mAsyncTaskStillExecuting = true;
    }
}

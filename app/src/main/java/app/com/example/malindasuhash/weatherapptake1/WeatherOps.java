package app.com.example.malindasuhash.weatherapptake1;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
    private WeakReference<Button> mGetWeatherSync;
    private WeakReference<Button> mGetWeatherAsync;

    public WeatherOps(WeatherActivity weatherActivity)
    {
        this.mWeatherActivity = new WeakReference<>(weatherActivity);

        initialiseFields();
    }

    public void getCurrentWeatherSync()
    {
        if (validate())
        {
            // DO work.
        }
    }

    public void getCurrentWeatherAsync()
    {

    }

    private boolean validate()
    {
        String location = mLocation.get().getText().toString();

        Log.i(TAG, "Validating " + location);

        if (location.trim().length() == 0)
        {
            Toast.makeText(mWeatherActivity.get(), R.string.location_cannot_be_empty, Toast.LENGTH_SHORT).show();
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
    }
}

package app.com.example.malindasuhash.weatherapptake1;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import app.com.example.malindasuhash.weatherapptake1.activities.WeatherActivity;

/**
 * Simple base class to encapsulate weather handling logic.
 */
public abstract class WeatherOpsImpl {

    private final String TAG = this.getClass().getSimpleName();

    protected WeakReference<WeatherActivity> mWeatherActivity;

    protected WeakReference<EditText> mLocation;

    public WeatherOpsImpl(WeatherActivity weatherActivity)
    {
        this.mWeatherActivity = new WeakReference<>(weatherActivity);
        this.mLocation = new WeakReference<>((EditText) mWeatherActivity.get().findViewById(R.id.location));
    }

    public void getCurrentWeather()
    {
        if (validate())
        {
            DoWork();
        }
    }

    protected abstract void DoWork();

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
}

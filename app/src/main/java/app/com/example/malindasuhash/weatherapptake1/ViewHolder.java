package app.com.example.malindasuhash.weatherapptake1;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import app.com.example.malindasuhash.weatherapptake1.activities.WeatherActivity;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.utils.Formatter;

/**
 * Simple class to hold the components in the view and useful functions.
 */
public class ViewHolder {

    protected WeakReference<EditText> mLocation;
    private WeakReference<ProgressBar> mProgressBar;
    private WeakReference<TextView> mWeatherName;
    private WeakReference<TextView> mWeatherSpeed;
    private WeakReference<TextView> mWeatherDeg;
    private WeakReference<TextView> mWeatherTemp;
    private WeakReference<TextView> mWeatherHumidity;
    private WeakReference<TextView> mWeatherSunrise;
    private WeakReference<TextView> mWeatherSunset;

    public void initialise(WeatherActivity activity)
    {
        mLocation = new WeakReference<>((EditText)activity.findViewById(R.id.location));
        mProgressBar = new WeakReference<>((ProgressBar)activity.findViewById(R.id.progress));

        mWeatherName = new WeakReference<>((TextView)activity.findViewById(R.id.weather_name));
        mWeatherSpeed = new WeakReference<>((TextView)activity.findViewById(R.id.weather_speed));
        mWeatherDeg = new WeakReference<>((TextView)activity.findViewById(R.id.weather_deg));
        mWeatherTemp = new WeakReference<>((TextView)activity.findViewById(R.id.weather_temp));
        mWeatherHumidity = new WeakReference<>((TextView)activity.findViewById(R.id.weather_humidity));
        mWeatherSunrise = new WeakReference<>((TextView)activity.findViewById(R.id.weather_sunrise));
        mWeatherSunset = new WeakReference<>((TextView)activity.findViewById(R.id.weather_sunset));
    }

    public void setData(WeatherData data)
    {
        if (data == null)
        {
            return;
        }

        mWeatherName.get().setText(data.getName());
        mWeatherSpeed.get().setText(Formatter.formatSpeed(data.getSpeed()));
        mWeatherDeg.get().setText(Formatter.formatDegrees(data.getDeg()));
        mWeatherTemp.get().setText(Formatter.formatTemp(data.getTemp()));
        mWeatherHumidity.get().setText(Formatter.formatHumidity(data.getHumidity()));
        mWeatherSunrise.get().setText(Formatter.formatDate(data.getSunrise()));
        mWeatherSunset.get().setText(Formatter.formatDate(data.getSunset()));
    }

    public void reset()
    {
        mWeatherName.get().setText("");
        mWeatherSpeed.get().setText("");
        mWeatherDeg.get().setText("");
        mWeatherTemp.get().setText("");
        mWeatherHumidity.get().setText("");
        mWeatherSunrise.get().setText("");
        mWeatherSunset.get().setText("");
    }

    public void showProgressBar()
    {
        mProgressBar.get().setVisibility(View.VISIBLE);
    }

    public void hideProgressBar()
    {
        mProgressBar.get().setVisibility(View.INVISIBLE);
    }

    public String getLocation()
    {
        return mLocation.get().getText().toString().trim();
    }
}

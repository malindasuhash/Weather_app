package app.com.example.malindasuhash.weatherapptake1.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;

/**
 * The adapter for the list view that contains weather information.
 */
public class WeatherListDataAdapter extends ArrayAdapter<WeatherData> {

    public WeatherListDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return super.getView(position, convertView, parent);
    }
}

package app.com.example.malindasuhash.weatherapptake1.services;

import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherCall;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;

/**
 * Class to call the open weather service.
 */
class OpenWeatherClientSync extends WeatherCall.Stub {

    private final String TAG = this.getClass().getSimpleName();

    private String mOpenWebEndpointPrefix = "http://api.openweathermap.org/data/2.5/weather?q=";

    @Override
    public synchronized List<WeatherData> getCurrentWeather(String Weather) throws RemoteException {
        ArrayList<WeatherData> data = new ArrayList<>();
        data.add(new WeatherData("name", 10, 20d, 30d, 2l, 5l, 6l));
        return data;
    }
}

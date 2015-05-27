package app.com.example.malindasuhash.weatherapptake1.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.json.WeatherJsonDataParser;

/**
 * Responsible for calling the Open weather service and parsing the result
 * using WeatherJsonDataParser.
 */
public class OpenWeatherCaller {

    public static List<WeatherData> getResults(String endpoint)
    {
        List<WeatherData> weatherDataList = null;

        try {
            final URL url = new URL(endpoint);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try (InputStream in = new BufferedInputStream(urlConnection.getInputStream())) {

                final WeatherJsonDataParser parser = new WeatherJsonDataParser();

                weatherDataList = parser.parseJsonStream(in);
                in.close();
            } finally {
                urlConnection.disconnect();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherDataList;
    }
}

package app.com.example.malindasuhash.weatherapptake1.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;
import app.com.example.malindasuhash.weatherapptake1.json.WeatherJsonDataParser;

/**
 * Responsible for calling the Open weather service and parsing the results.
 */
public class OpenWeatherCaller {

    public static List<WeatherData> getResults(String endpoint)
    {
        final List<WeatherData> returnList = new ArrayList<WeatherData>();

        List<WeatherData> weatherDataList = null;

        try {
            final URL url = new URL(endpoint);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try (InputStream in = new BufferedInputStream(urlConnection.getInputStream())) {

                final WeatherJsonDataParser parser = new WeatherJsonDataParser();

                // Parse the Json results and create JsonAcronym data
                // objects.
                weatherDataList = parser.parseJsonStream(in);
                in.close();
            } finally {
                urlConnection.disconnect();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (weatherDataList != null && weatherDataList.size() > 0) {
            // Convert the JsonAcronym data objects to our AcronymData
            // object, which can be passed between processes.
            /*for (WeatherData weatherData : weatherDataList)
                returnList.add(new AcronymData(jsonAcronym.getLongForm(),
                        jsonAcronym.getFreq(),
                        jsonAcronym.getSince()));
            */// Return the List of AcronymData.
            return returnList;
        }  else
            return null;
    }
}

package app.com.example.malindasuhash.weatherapptake1.json;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;

/**
 * Responsible for parsing the Open weather data.
 * Code adapted from:
 * https://github.com/douglascraigschmidt/POSA-15/blob/master/ex/AcronymApplication/src/vandy/mooc/jsonacronym/AcronymJSONParser.java
 */
public class WeatherJsonDataParser {

    final String TAG = this.getClass().getName();

    public List<WeatherData> parseJsonStream(InputStream inputStream)
            throws IOException {

        // Create a JsonReader for the inputStream.
        try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"))) {
            Log.i(TAG, "Parsing the results.");

            List<WeatherData> data = parseWeatherServiceResults(reader);

            return data;
        }
    }

    public List<WeatherData> parseWeatherServiceResults(JsonReader reader)
            throws IOException {

        reader.beginObject();
        try {
            if (reader.peek() == JsonToken.END_OBJECT)
                return null;

            List<WeatherData> data = parseWeatherMessage(reader);
            return data;
        } finally {
            if (reader.peek() == JsonToken.END_OBJECT)
            {
                reader.endObject();
            }
        }
    }

    public List<WeatherData> parseWeatherMessage(JsonReader reader)
            throws IOException {

        List<WeatherData> data = new ArrayList<>();

        String weatherName = null;
        Tuple<Long, Long> sysInfo = null;
        Tuple<Double, Long> tempAndHumidity = null;
        Tuple<Double, Double> speedAndDeg = null;

        try {
            outerloop:
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "coord":
                    case "weather":
                        reader.skipValue(); // We do not worry about these.
                        break;

                    case "sys":
                        sysInfo = parseSysObject(reader);
                        break;

                    case "name":
                        weatherName = reader.nextString();
                        Log.i(TAG, "reading name " + weatherName);
                        break;

                    case "main":
                        tempAndHumidity = getTempAndHumidity(reader);
                        break;

                    case "wind":
                        speedAndDeg = getSpeedAndDeg(reader);
                        break;

                    default:
                        Log.i(TAG, "weird problem with " + name + " field");
                        reader.skipValue(); // Again, we simply interested in key fields.
                        break;
                }

                Log.i(TAG, "Read " + name);
            }
        } finally {
            Log.i(TAG, "Reading complete.");
        }

        if (weatherName == null)
        {
            Log.i(TAG, "Looks like the city was not found.");
            return null;
        }

        WeatherData weatherData = new WeatherData(weatherName, speedAndDeg.Item1, speedAndDeg.Item2,
                tempAndHumidity.Item1, tempAndHumidity.Item2,
                sysInfo.Item2, sysInfo.Item1);

        data.add(weatherData);

        return data;
    }

    private Tuple<Long, Long> parseSysObject(JsonReader reader) throws IOException {

        reader.beginObject();

        long sunrise = 0l;
        long sunset = 0l;

        while (reader.hasNext())
        {
            switch (reader.nextName())
            {
                case "sunrise":
                    sunrise = reader.nextLong();
                    Log.i(TAG, "Sunrise " + sunrise);
                    break;
                case "sunset":
                    sunset = reader.nextLong();
                    Log.i(TAG, "Sunrise " + sunset);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }

        reader.endObject();

        Tuple<Long, Long> info = new Tuple<>();
        info.Item1 = sunset;
        info.Item2 = sunrise;

        return info;
    }

    public Tuple<Double, Long> getTempAndHumidity(JsonReader reader) throws IOException
    {
        reader.beginObject();

        double temp = 0l;
        long humidity = 0l;

        while (reader.hasNext())
        {
            switch (reader.nextName())
            {
                case "temp":
                    temp = reader.nextDouble();
                    Log.i(TAG, "temp " + temp);
                    break;
                case "humidity":
                    humidity = reader.nextLong();
                    Log.i(TAG, "humidity " + humidity);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }

        reader.endObject();

        Tuple<Double, Long> info = new Tuple<>();
        info.Item1 = temp;
        info.Item2 = humidity;

        return info;
    }

    public Tuple<Double, Double> getSpeedAndDeg(JsonReader reader) throws IOException
    {
        reader.beginObject();

        double speed = 0l;
        double deg = 0l;

        while (reader.hasNext())
        {
            switch (reader.nextName())
            {
                case "speed":
                    speed = reader.nextDouble();
                    Log.i(TAG, "speed " + speed);
                    break;
                case "deg":
                    deg = reader.nextDouble();
                    Log.i(TAG, "deg " + deg);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }

        reader.endObject();

        Tuple<Double, Double> info = new Tuple<>();
        info.Item1 = speed;
        info.Item2 = deg;

        return info;
    }

    /**
     * Very simple Tuple implementation.
     */
    private class Tuple<T, U>
    {
        public T Item1;
        public U Item2;
    }
}

package app.com.example.malindasuhash.weatherapptake1.json;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;

/**
 * Parse the response of the web service call.
 */
public class WeatherJsonDataParser {

    final String TAG = this.getClass().getName();

    private final String coord = "coord";
    private final String wname = "name";
    private final String wind = "wind";
    private final String sys = "sys";

    public List<WeatherData> parseJsonStream(InputStream inputStream)
            throws IOException {

        // Create a JsonReader for the inputStream.
        try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"))) {
            Log.i(TAG, "Parsing the results returned as an array");

            return parseWeatherServiceResults(reader);
        }
    }

    public List<WeatherData> parseWeatherServiceResults(JsonReader reader)
            throws IOException {

        reader.beginObject();
        try {
            if (reader.peek() == JsonToken.END_OBJECT)
                return null;

            return parseWeatherMessage(reader);
        } finally {
            if (reader.peek() == JsonToken.END_OBJECT)
            {
                reader.endObject();
            }
        }
    }


    /**
     * Constructor
     *
     * @param name
     * @param speed
     * @param deg
     * @param temp
     * @param humidity
     * @param sunrise - OK
     * @param sunset - OK
     */
    public List<WeatherData> parseWeatherMessage(JsonReader reader)
            throws IOException {

        List<WeatherData> data = null;

        String weatherName;
        SysInfo sysInfo = null;

        try {
            outerloop:
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "coord":
                    case "weather":
                        reader.skipValue();
                        break;

                    case "sys":
                        sysInfo = parseSysObject(reader);
                        break;

                    case "name":
                        weatherName = reader.nextString();
                        Log.i(TAG, "reading name " + weatherName);
                        break;

                    case "main":
                    /*case JsonAcronym.lfs_JSON:
                        Log.d(TAG, "reading lfs field");
                        if (reader.peek() == JsonToken.BEGIN_ARRAY)
                            acronyms = parseAcronymLongFormArray(reader);
                        break outerloop;*/
                    default:
                        Log.i(TAG, "weird problem with " + name + " field");
                        reader.skipValue();
                        break;
                }


                Log.i(TAG, "Read " + name);
            }
        } finally {
            Log.i(TAG, "Reading complete.");
        }

        return data;
    }

    private SysInfo parseSysObject(JsonReader reader) throws IOException {

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

        SysInfo info = new SysInfo();
        info.subset = sunset;
        info.sunrise = sunrise;

        return info;
    }

    public void getTemp(JsonReader reader) throws IOException
    {

    }

    private class SysInfo
    {
        public long sunrise;
        public long subset;
    }
}

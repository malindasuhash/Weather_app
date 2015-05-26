package app.com.example.malindasuhash.weatherapptake1.utils;

/**
 * Contains a collection of weather data elements.
 */
public class Formatter {

    public static String formatHumidity(double humidity)
    {
        return  Double.toString(humidity) + "%";
    }

    public static String formatSpeed(double speed)
    {
        return Double.toString(speed) + "mps";
    }
}

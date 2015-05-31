package app.com.example.malindasuhash.weatherapptake1.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

/**
 * Contains a collection of weather data elements.
 * Formatting based on: http://openweathermap.org/weather-data#current
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

    public static String formatTemp(double temp)
    {
        double t = temp - 273.15;
        NumberFormat formatter = new DecimalFormat("#0.00");

        return formatter.format(t) + "C";
    }

    public static String formatDate(long milliseconds)
    {
        Date date = new Date();
        date.setTime(milliseconds * 1000);

        return date.toString();
    }
}

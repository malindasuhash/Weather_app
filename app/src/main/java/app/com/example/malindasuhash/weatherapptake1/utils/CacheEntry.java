package app.com.example.malindasuhash.weatherapptake1.utils;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;

/**
 * Simple POJO to store an entry of the cache..
 */
public class CacheEntry {

    // Marks the expiry time in milliseconds.
    public long CachedUntil;

    // Weather data entry
    public WeatherData Data;
}

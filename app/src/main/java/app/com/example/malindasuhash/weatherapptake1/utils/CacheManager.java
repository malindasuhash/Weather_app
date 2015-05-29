package app.com.example.malindasuhash.weatherapptake1.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;

/**
 * The component that is responsible for managing the
 * cache.
 */
public class CacheManager {

    private HashMap<String, CacheEntry> mBackingStore = new HashMap<>();
    private int cacheDurationInSeconds;

    public CacheManager(int cacheDurationInSeconds)
    {
        this.cacheDurationInSeconds = cacheDurationInSeconds;
    }

    /**
     * Attempts to find an entry in the cache for the given key.
     */
    public WeatherData get(String key)
    {
        updateCache();

        Date date = new Date();
        CacheEntry cached = mBackingStore.get(key.toLowerCase());

        if (cached != null && date.getTime() < cached.CachedUntil)
        {
            return cached.Data;
        }

        return null;
    }

    /**
     * Adds an Weather data entry to the cache.
     */
    public synchronized void set(String key, WeatherData weatherData)
    {
        Date date = new Date();
        long expiry = date.getTime() + (cacheDurationInSeconds * 1000); // in milliseconds

        CacheEntry entry = new CacheEntry();
        entry.Data = weatherData;
        entry.CachedUntil = expiry;

        mBackingStore.put(key, entry);
    }

    private synchronized void updateCache()
    {
        HashMap<String, CacheEntry> map = new HashMap<>();
        Date date = new Date();

        for (Map.Entry<String, CacheEntry> cacheEntrySet : mBackingStore.entrySet())
        {
            if (cacheEntrySet.getValue().CachedUntil > date.getTime())
            {
                map.put(cacheEntrySet.getKey().toLowerCase(), cacheEntrySet.getValue());
            }
        }

        mBackingStore = map; // update the new list.
    }
}

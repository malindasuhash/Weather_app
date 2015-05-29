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

    private Date mDate = new Date();
    private HashMap<String, CacheEntry> mBackingStore = new HashMap<>();
    private int cacheDurationInSeconds;

    public CacheManager(int cacheDurationInSeconds)
    {
        this.cacheDurationInSeconds = cacheDurationInSeconds;
    }

    /**
     * Attempts to find an entry in the cache for the given key.
     */
    public synchronized WeatherData get(String key)
    {
        updateCache();

        CacheEntry cached = mBackingStore.get(key);

        if (cached != null && mDate.getTime() < cached.CachedUntil)
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
        long expiry = mDate.getTime() + cacheDurationInSeconds * 1000; // in milliseconds

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
                map.put(cacheEntrySet.getKey(), cacheEntrySet.getValue());
            }
        }

        mBackingStore = map; // update the new list.
    }
}

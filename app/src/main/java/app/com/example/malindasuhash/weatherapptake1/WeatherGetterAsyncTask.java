package app.com.example.malindasuhash.weatherapptake1;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherCall;
import app.com.example.malindasuhash.weatherapptake1.aidl.WeatherData;

/**
 * Async task for calling the WeatherServiceSync so that the call
 * does not block the UI.
 */
public class WeatherGetterAsyncTask extends AsyncTask<String, List<WeatherData>, List<WeatherData>> {

    private final String TAG = this.getClass().getSimpleName();

    private TaskExecutionState mTaskExecutionState;
    private WeatherCall mWeatherCall;

    public WeatherGetterAsyncTask(TaskExecutionState taskExecutionState, WeatherCall weatherCall)
    {
        super();
        this.mTaskExecutionState = taskExecutionState;
        this.mWeatherCall = weatherCall;
    }

    protected void onPreExecute() {
        super.onPreExecute();

        Log.i(TAG, "In OnPreExecute showing the progress window.");
    }

    @Override
    protected List<WeatherData> doInBackground(String... strings) {
        try {
            List<WeatherData> data = mWeatherCall.getCurrentWeather(strings[0]);
            Log.i(TAG, "Data " + data);
            return data;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<WeatherData> weatherDatas) {
        super.onPostExecute(weatherDatas);
        Log.i(TAG, "Done work in Sync " + weatherDatas.size());
        mTaskExecutionState.Finished();
    }

    interface TaskExecutionState
    {
        void Finished();
    }
}

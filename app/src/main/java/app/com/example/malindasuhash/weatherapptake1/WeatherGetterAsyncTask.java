package app.com.example.malindasuhash.weatherapptake1;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Async task for calling the WeatherServiceSync so that the call
 * does not block the UI.
 */
public class WeatherGetterAsyncTask extends AsyncTask<String, String, String> {

    private final String TAG = this.getClass().getSimpleName();

    private TaskExecutionState mTaskExecutionState;

    public WeatherGetterAsyncTask(TaskExecutionState taskExecutionState)
    {
        super();
        this.mTaskExecutionState = taskExecutionState;
    }

    protected void onPreExecute() {
        super.onPreExecute();

        Log.i(TAG, "In OnPreExecute showing the progress window.");
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Thread.sleep(8000l);
            Log.i(TAG, "Finished working");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        mTaskExecutionState.Finished();
    }

    interface TaskExecutionState
    {
        public void Finished();
    }
}

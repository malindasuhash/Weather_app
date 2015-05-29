package app.com.example.malindasuhash.weatherapptake1.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import app.com.example.malindasuhash.weatherapptake1.R;
import app.com.example.malindasuhash.weatherapptake1.WeatherOpsSync;
import app.com.example.malindasuhash.weatherapptake1.WeatherOpsBase;
import app.com.example.malindasuhash.weatherapptake1.utils.RetainedFragmentManager;


public class WeatherActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    private WeatherOpsBase mWeatherOps;

    protected final RetainedFragmentManager mRetainedFragmentManager =
            new RetainedFragmentManager(this.getFragmentManager(),
                    TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Creating the activity in OnCreate.");

        setContentView(R.layout.activity_weather);
        Log.i(TAG, "UI is bound in OnCreate.");

        handleConfigurationChanges();
        Log.i(TAG, "Configuration change handling is invoked");
    }

    public void getWeatherSync(View view) {
        Log.i(TAG, "Getting weather sync");
        mWeatherOps.getCurrentWeather();
    }

    public void getWeatherAsync(View view)
    {
        Log.i(TAG, "Getting weather async");
        mWeatherOps.getCurrentWeather();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mWeatherOps.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mWeatherOps.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleConfigurationChanges() {

        final String weatherFragmentTagName = "WeatherOps";

        // If this method returns true then this is the first time the
        // Activity has been created.
        if (mRetainedFragmentManager.firstTimeIn()) {
            Log.d(TAG, "First time onCreate() call");

            mWeatherOps = new WeatherOpsSync(this);

            mRetainedFragmentManager.put(weatherFragmentTagName, mWeatherOps);

        } else {
            // The RetainedFragmentManager was previously initialized,
            // which means that a runtime configuration change
            // occured.

            Log.d(TAG, "Second or subsequent onCreate() call");

            mWeatherOps = mRetainedFragmentManager.get(weatherFragmentTagName);

            // This check shouldn't be necessary under normal
            // circumstances, but it's better to lose state than to
            // crash!
            if (mWeatherOps == null) {
                mWeatherOps = new WeatherOpsSync(this);

                mRetainedFragmentManager.put(weatherFragmentTagName, mWeatherOps);
            }
            else {
                mWeatherOps.onConfigurationChange(this);
            }
        }
    }
}

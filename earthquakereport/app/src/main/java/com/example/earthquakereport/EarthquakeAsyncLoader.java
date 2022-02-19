package com.example.earthquakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class EarthquakeAsyncLoader extends AsyncTaskLoader<ArrayList<Info>> {

    public static final String LOG_TAG = EarthquakeAsyncLoader.class.getName();
    /**
     * @param context
     * @deprecated
     */
    String mLoader = null;

    public EarthquakeAsyncLoader(Context context, String url) {
        super(context);
        Log.i(LOG_TAG,"Constructor of EarthquakeAsyncLoader is invoked ");

        mLoader = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG,"OnStartLoading is invoked");
        forceLoad();
    }

    @Override
    public ArrayList<Info> loadInBackground() {

        Log.i(LOG_TAG,"loadInBackground is invoked");
        if (mLoader == null)
            return null;


        ArrayList<Info> earthquakes = QueryUtils.extractEarthquake(mLoader);
        return earthquakes;
    }

}

package com.example.earthquakereport;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Info>> {

    public static String MAIN_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    private static InfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView earthquakeListView = (ListView) findViewById(R.id.quake);
        mAdapter = new InfoAdapter(this,new ArrayList<Info>());

        earthquakeListView.setEmptyView(findViewById(R.id.empty_view));
        earthquakeListView.setAdapter(mAdapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Info I = mAdapter.getItem(i);
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(I.url));
                startActivity(intent);
            }
        });
        ConnectivityManager C = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo N = C.getActiveNetworkInfo();

        if(N != null && N.isConnected()) {
            LoaderManager L = getLoaderManager();
            Log.i(LOG_TAG, "init method started its work");
            L.initLoader(1, null, this);
        }
        else
        {
            View P = findViewById(R.id.progressBar);
            P.setVisibility(View.GONE);
            TextView t = findViewById(R.id.empty_view);
            t.setText("NO INTERNET CONNECTION");
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<Info>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.i(LOG_TAG,"onCreateLoader method is invoked ");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        Uri baseUri = Uri.parse(MAIN_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "30");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeAsyncLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Info>> loader, ArrayList<Info> infos) {

        View P = findViewById(R.id.progressBar);
        P.setVisibility(View.GONE);
        Log.i(LOG_TAG,"onLoadFinished method is invoked ");

        mAdapter.clear();
        if(infos != null || !infos.isEmpty()) {
            mAdapter.addAll(infos);
        }
        TextView t = findViewById(R.id.empty_view);
        t.setText("NO EARTHQUAKE IN THE LAST 24 HOURS");

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Info>> loader) {
        Log.i(LOG_TAG,"onLoaderReset method is invoked ");
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


package com.example.earthquakereport;


import android.graphics.drawable.GradientDrawable;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /** Sample JSON response for a USGS query */
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Info} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Info> extractEarthquake(String requestedUrl)
    {
        URL url = CreateUrl(requestedUrl);

        String jsonresponse = "";
        try {
            jsonresponse = makeHttpRequest(url);
        }
        catch(IOException e) {
            Log.e(LOG_TAG,"Exception with makeHttpMethod method is:- ",e);
        }
        ArrayList<Info>Earthquake = extractJSON(jsonresponse);
        Log.i(LOG_TAG,"earthquakeextract method finished its work");
        return Earthquake;

    }
    public static URL CreateUrl(String requesturl)
    {
        URL url = null;
        try{
            url = new URL(requesturl);
        }
        catch(MalformedURLException e)
        {
            Log.e(LOG_TAG,"Exception with create url method:- ",e);
        }
        return url;
    }
    public static String makeHttpRequest(URL url) throws IOException {

        String jsonresponse = "";
        if(url == null)
            return jsonresponse;

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream Input = null;
        if(urlConnection.getResponseCode() == 200)
        {
            Input = urlConnection.getInputStream();
            jsonresponse = readfromInput(Input);
        }
        else{
            Log.e(LOG_TAG,"Exception due to source code = "+urlConnection.getResponseCode());
        }
        if(urlConnection != null)
                urlConnection.disconnect();

        if(Input != null)
                Input.close();

        return jsonresponse;

    }

    private static String readfromInput(InputStream inputstream) {
        StringBuilder output= new StringBuilder();
        try {
            if(inputstream != null)
            {
                InputStreamReader inputreader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
                BufferedReader buffer = new BufferedReader(inputreader);
                String line = buffer.readLine();
                while(line != null) {
                    output.append(line);
                    line = buffer.readLine();
                }
            }
        }
        catch(IOException e)
        {
            Log.e(LOG_TAG,"readfromInput method exception :- ",e);
        }
        return output.toString();
    }

    public static ArrayList<Info> extractJSON(String SAMPLE_JSON_RESPONSE) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Info> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject Root = new JSONObject(SAMPLE_JSON_RESPONSE);
            JSONArray feature = Root.getJSONArray("features");
            for(int i=0 ; i<feature.length() ; i++)
            {
                JSONObject A = feature.getJSONObject(i);
                JSONObject B = A.getJSONObject("properties");

                 long t = B.getLong("time");
                 Date dateObject = new Date (t);
                SimpleDateFormat d = new SimpleDateFormat("MMM DD,yyyy");
                String date = d.format(dateObject);
                d = new SimpleDateFormat("HH:mm a");
                String time = d.format(dateObject);

                String u = B.getString("url");

                Info I = new Info(B.getString("mag"),B.getString("place"),date+"\n"+time,u);
                earthquakes.add(I);

            }
            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}

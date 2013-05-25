package com.weblogic.lutskbus;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Message;
import android.os.Handler;
import android.os.Handler.Callback;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    private GoogleMap map;

    long starttime = 0;

    private LatLng Lutsk = new LatLng(50.747233, 25.325383);

    private static final String TAG_MARK = "mark";
    private static final String TAG_ID = "id";
    private static final String TAG_LAT = "lt";
    private static final String TAG_LNG = "ln";
    private static final String TAG_ROUTE = "r";

    private Map<Integer, Marker> markers = new HashMap<Integer, Marker>();


    //this  posts a message to the main thread from our timertask
    //and updates the textfield
    final Handler h = new Handler(new Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            if (isNetworkConnected()) {
                new AsyncTask<Void, Void, JSONObject>() {
                    private String url = "http://mak.lutsk.ua/MoveOnMap?level=1&nodeid=1";

                    @Override
                    protected JSONObject doInBackground(Void... params) {

                        JSONParser jParser = new JSONParser();

                        // getting JSON string from URL
                        JSONObject json = jParser.getJSONFromUrl(url);
                        return json;
                    }

                    protected void onPostExecute(JSONObject json) {
                        try {
                            // Getting Array of Contacts
                            JSONArray marks = json.getJSONArray(TAG_MARK);

                            // looping through All Contacts
                            for(int i = 0; i < marks.length(); i++){
                                JSONObject c = marks.getJSONObject(i);

                                // Storing each json item in variable
                                Integer id = c.getInt(TAG_ID);
                                Double lat = c.getDouble(TAG_LAT);
                                Double lng = c.getDouble(TAG_LNG);
                                String route = c.getString(TAG_ROUTE);
                                if (markers.get(id) != null) {
                                    markers.get(id).setPosition(new LatLng(lat, lng));
                                } else {
                                    Marker marker = map.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, lng))
                                            .title("Маршрут №"+route)
                                            .icon(BitmapDescriptorFactory
                                                    .fromResource(R.drawable.ic_launcher)));
                                    markers.put(id, marker);
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }

            return false;
        }


    });

    //tells handler to send a message
    class firstTask extends TimerTask {

        @Override
        public void run() {
            h.sendEmptyMessage(0);
        }
    }


    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Lutsk, 8));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        starttime = System.currentTimeMillis();
        timer = new Timer();
        timer.schedule(new firstTask(), 0, 5000);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            Toast.makeText(MainActivity.this, "You need internet connection!", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }


}

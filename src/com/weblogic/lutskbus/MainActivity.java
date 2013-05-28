package com.weblogic.lutskbus;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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


        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                drawMarker(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };


        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        Location mapStartPosition = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mapStartPosition.getLatitude(),
                mapStartPosition.getLongitude()), 16));

        if (!isNetworkConnected())
            Toast.makeText(MainActivity.this, R.string.need_internet, Toast.LENGTH_SHORT).show();

        starttime = System.currentTimeMillis();
        timer = new Timer();
        timer.schedule(new firstTask(), 0, 5000);
    }

    private Marker myMarker;

    private void drawMarker(Location location){

        LatLng currentPosition = new LatLng(location.getLatitude(),
                location.getLongitude());

        if (myMarker != null) {
            myMarker.setPosition(currentPosition);
        } else {
//            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//            Bitmap bmp = Bitmap.createBitmap(100, 100, conf);
//            Canvas canvas1 = new Canvas(bmp);
//
//            // paint defines the text color,
//            // stroke width, size
//            Paint color = new Paint();
//            color.setTextSize(35);
//            color.setColor(Color.BLACK);
//
//            //modify canvas
//            Matrix matrix = new Matrix();
//            matrix.setRotate(mRotation,source.getWidth()/2,source.getHeight()/2);
//            canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
//                    R.drawable.pin), 0,0, color);
//            canvas1.drawText("27А", 20, 50, color);

            myMarker = map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.me)));
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else
            return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        timer = new Timer();
        timer.schedule(new firstTask(), 0, 5000);
    }

}

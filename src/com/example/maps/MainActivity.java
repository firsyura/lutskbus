package com.example.maps;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
    static final LatLng Lutsk = new LatLng(50.747233, 25.325383);
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        Marker lutsk = map.addMarker(new MarkerOptions()
                .position(Lutsk)
                .title("Луцьк")
                .snippet("Луцьк найкращий!")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_launcher)));

        // Move the camera instantly to hamburg with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Lutsk, 15));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }
}

package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.models.Property;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PropertiesOnMapActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;

    private int LOCATION_PERMISSION_CODE = 1;
    private ConnectivityManager connectivityManager;
    private NetworkInfo mobileConnection;
    private NetworkInfo wifiConnection;

    private GoogleMap gMap;

    private Property chosenProperty;

    private String propertyName;
    private String propertyAddress;
    private double propertyLatitude,
            propertyLongitude;

    private FloatingActionButton FABChangeMapType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_properties_on_map);

        initializeViews();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment_propertiesOnMap);
        mapFragment.getMapAsync(this);

        FABChangeMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (gMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng BayombongDefault = new LatLng(16.4845001, 121.1563895);
        gMap.addMarker(new MarkerOptions().position(BayombongDefault).title("Bayombong")).showInfoWindow();
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BayombongDefault, 16.0f));

/*        LatLng previousLocation = new LatLng(propertyLatitude, propertyLongitude);
        gMap.addMarker(new MarkerOptions().position(previousLocation)
                .title(propertyName)
                .snippet(propertyAddress)).showInfoWindow();
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(previousLocation, gMap.getMaxZoomLevel()));*/
    }

    public void initializeViews() {
        FABChangeMapType = findViewById(R.id.FABChangeMapType);
    }
}
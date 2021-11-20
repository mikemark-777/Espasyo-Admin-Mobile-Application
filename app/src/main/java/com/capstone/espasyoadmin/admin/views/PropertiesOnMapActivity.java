package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PropertiesOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SupportMapFragment mapFragment;

    private int LOCATION_PERMISSION_CODE = 1;
    private ConnectivityManager connectivityManager;
    private NetworkInfo mobileConnection;
    private NetworkInfo wifiConnection;

    private ArrayList<Property> propertyMasterlist;
    private FloatingActionButton FABChangeMapType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_properties_on_map);

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment_propertiesOnMap);
        mapFragment.getMapAsync(this);

        FABChangeMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (gMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else if (gMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
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
        if (propertyMasterlist != null) {
            displayPropertiesOnMap();
        }
    }

    public void initializeViews() {
        FABChangeMapType = findViewById(R.id.FABChangeMapType);
    }

    public void getDataFromIntent(Intent intent) {
        propertyMasterlist = intent.getParcelableArrayListExtra("propertyMasterlist");
    }

    //will get all data
    public void displayPropertiesOnMap() {
        for (Property propertyObj : propertyMasterlist) {
            //get data from property
            String propertyName = propertyObj.getName();
            String address = propertyObj.getAddress();
            double latitude = propertyObj.getLatitude();
            double longitude = propertyObj.getLongitude();

            LatLng property = new LatLng(latitude, longitude);
            gMap.addMarker(new MarkerOptions()
                    .position(property)
                    .title(propertyName)
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                    .showInfoWindow();
            ;
        }
    }
}
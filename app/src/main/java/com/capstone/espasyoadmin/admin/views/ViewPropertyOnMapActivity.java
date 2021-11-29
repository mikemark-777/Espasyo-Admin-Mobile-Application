package com.capstone.espasyoadmin.admin.views;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.connectivityUtil.InternetConnectionUtil;
import com.capstone.espasyoadmin.models.Property;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ViewPropertyOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;

    private int LOCATION_PERMISSION_CODE = 1;
    private InternetConnectionUtil internetChecker;
    private ConnectivityManager connectivityManager;
    private FusedLocationProviderClient client;
    private FloatingActionButton changeMapType;

    private GoogleMap gMap;

    private Property chosenProperty;

    private String propertyName;
    private String propertyAddress;
    private double propertyLatitude, propertyLongitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_view_property_on_map);

        initializeViews();
        Intent intent = getIntent();
        getPropertyDataFromIntent(intent);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment_viewPropertyOnMap);
        mapFragment.getMapAsync(this);
        client = LocationServices.getFusedLocationProviderClient(ViewPropertyOnMapActivity.this);
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        internetChecker = new InternetConnectionUtil(this, connectivityManager);

        changeMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMapType();
            }
        });

    }

    public void initializeViews() {
        changeMapType = findViewById(R.id.changeMapTypeStudent_propertyDetails);
    }

    public void getPropertyDataFromIntent(Intent intent) {
        chosenProperty = intent.getParcelableExtra("chosenProperty");
        propertyName = chosenProperty.getName();
        propertyAddress = chosenProperty.getAddress();
        propertyLatitude = chosenProperty.getLatitude();
        propertyLongitude = chosenProperty.getLongitude();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setPolyLineOfMap(gMap);

        LatLng propertyLocation = new LatLng(propertyLatitude, propertyLongitude);
        gMap.addMarker(new MarkerOptions().position(propertyLocation)
                .title(propertyName)
                .snippet(propertyAddress)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(propertyLocation, 16.0f));
        getCurrentLocation();
    }

    public void setPolyLineOfMap(GoogleMap gMap) {
        //to specify the area of main location
        gMap.addPolyline(new PolylineOptions().clickable(true).color(Color.LTGRAY).add(
                new LatLng(16.4814312, 121.1542103),
                new LatLng(16.4826409, 121.1572306),
                new LatLng(16.4834756, 121.1572032),
                new LatLng(16.4843089, 121.15693),
                new LatLng(16.4845001, 121.1563895),
                new LatLng(16.4848, 121.1561731),
                new LatLng(16.4845163, 121.155666),
                new LatLng(16.4847609, 121.1552218),
                new LatLng(16.4838617, 121.1541471),
                new LatLng(16.4826408, 121.1531345),
                new LatLng(16.4814312, 121.1542103)
        ));
    }

    public void toggleMapType() {
        if (gMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (gMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (gMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    public void getCurrentLocation() {

        //check if the user has granted the permission for this functionality to access location
        if (ActivityCompat.checkSelfPermission(ViewPropertyOnMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (internetChecker.isConnectedToInternet()) {
                Task<Location> task = client.getLastLocation();

                task.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location location = task.getResult();
                            if (location != null) {

                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                LatLng usersLocation = new LatLng(latitude, longitude);
                                MarkerOptions markerOptions = new MarkerOptions().position(usersLocation).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.img_walking_person));
                                gMap.addMarker(markerOptions).showInfoWindow();

                            } else {
                                //location of device is disabled
                                showEnableLocationInSettingsDialog();
                            }
                        } else {
                            Toast.makeText(ViewPropertyOnMapActivity.this, "Task not successfull", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                internetChecker.showNoInternetConnectionDialog();
            }
        } else {
            requestLocationPermission();
        }
    }

    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Location permission is needed to access your location.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ViewPropertyOnMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    public void showEnableLocationInSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Use location?")
                .setMessage("To continue, you need to turn on location in your device.")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enableLocationInSettings();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public void enableLocationInSettings() {
        Intent openLocationInSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(openLocationInSettingsIntent);
    }
}
package com.capstone.espasyoadmin.admin.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.capstone.espasyoadmin.R;
import com.capstone.espasyoadmin.admin.CustomDialogs.CustomProgressDialog;
import com.capstone.espasyoadmin.models.Property;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PropertiesOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SupportMapFragment mapFragment;

    private FusedLocationProviderClient client;
    private int LOCATION_PERMISSION_CODE = 1;
    private ConnectivityManager connectivityManager;
    private NetworkInfo mobileConnection;
    private NetworkInfo wifiConnection;

    private ArrayList<Property> propertyMasterlist;
    private FloatingActionButton FABChangeMapType, FABGetAdminCurrentLocation;
    private ImageView btnBackToAdminDashboard;

    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_properties_on_map);

        initializeViews();
        Intent intent = getIntent();
        getDataFromIntent(intent);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment_propertiesOnMap);
        mapFragment.getMapAsync(this);

        client = LocationServices.getFusedLocationProviderClient(PropertiesOnMapActivity.this);

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

        FABGetAdminCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAdminCurrentLocation();
            }
        });

        btnBackToAdminDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    public void initializeViews() {
        FABChangeMapType = findViewById(R.id.FABChangeMapType);
        FABGetAdminCurrentLocation = findViewById(R.id.FABGetAdminCurrentLocation);

        btnBackToAdminDashboard = findViewById(R.id.btnBackToAdminDashboard);
        progressDialog = new CustomProgressDialog(this);

    }

    public void getDataFromIntent(Intent intent) {
        propertyMasterlist = intent.getParcelableArrayListExtra("propertyMasterlist");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setPolyLineOfMap(gMap);

        LatLng SaintMarysUniversity = new LatLng(16.483022, 121.155538);
        gMap.addMarker(new MarkerOptions().position(SaintMarysUniversity).title("Saint Mary's University").icon(BitmapDescriptorFactory.fromResource(R.drawable.img_university))).showInfoWindow();
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SaintMarysUniversity, 16.0f));
        if (propertyMasterlist != null) {
            displayPropertiesOnMap();
        }
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

        }

        //add onclicklistener to all property location
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                progressDialog.showProgressDialog("Preparing property...", false);
                Property clickedProperty = getPropertyClicked(marker.getTitle());

                if (clickedProperty == null) {
                    Toast.makeText(PropertiesOnMapActivity.this, "This is not a property", Toast.LENGTH_SHORT).show();
                    progressDialog.dismissProgressDialog();
                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing()) {
                                Intent intent = new Intent(PropertiesOnMapActivity.this, PropertyDetailsActivity.class);
                                intent.putExtra("property", clickedProperty);
                                startActivity(intent);
                                progressDialog.dismissProgressDialog();
                            }
                        }
                    }, 1500);
                }
                return false;
            }
        });
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

    public void getAdminCurrentLocation() {
        //check if the user has granted the permission for this functionality to access location
        if (ActivityCompat.checkSelfPermission(PropertiesOnMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isConnectedToInternet()) {
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
                                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(usersLocation, 16.0f, 0, 0)));
                                gMap.addMarker(markerOptions).showInfoWindow();

                            } else {
                                //location of device is disabled
                                showEnableLocationInSettingsDialog();
                            }
                        } else {
                            Toast.makeText(PropertiesOnMapActivity.this, "Task not successfull", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                showNoInternetConnectionDialog();
            }
        } else {
            requestLocationPermission();
        }
    }

    public Property getPropertyClicked(String clickedPropertyName) {

        Property pickedProperty = null;
        for (Property property : propertyMasterlist) {
            if (property.getName().equals(clickedPropertyName)) {
                pickedProperty = property;
            }
        }
        return pickedProperty;
    }

    //====== check internet connections =============

    //this will check the connection of the user (network connection)
    private boolean isConnectedToInternet() {
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        mobileConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifiConnection = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobileConnection != null && mobileConnection.isConnected() || wifiConnection != null && wifiConnection.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void showNoInternetConnectionDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.no_internet_connection_dialog, null);

        Button btnOkayInternetConnection = view.findViewById(R.id.btnOkayInternetConnection);

        AlertDialog noInternetDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnOkayInternetConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternetDialog.dismiss();
            }
        });

        noInternetDialog.show();
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

    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Location permission is needed to access your location.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PropertiesOnMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
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

    public void enableLocationInSettings() {
        Intent openLocationInSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(openLocationInSettingsIntent);
    }

}
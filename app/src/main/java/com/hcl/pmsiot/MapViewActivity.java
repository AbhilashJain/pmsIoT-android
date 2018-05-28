package com.hcl.pmsiot;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hcl.pmsiot.data.Boudary;
import com.hcl.pmsiot.data.Building;
import com.hcl.pmsiot.data.BuildingBoundary;
import com.hcl.pmsiot.service.IotMqttService;

import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, PmsMqttCallBack {

    private GoogleMap mMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LocationListener locationListener;
    private String sapId;
    private Location myLocation;
    private double longitude, latitude;
    private Publisher pb;
    private MqttHelper mqttHelper;
    private ArrayList<LatLng> arrayPoints = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        Intent intent = getIntent();
        sapId = intent.getStringExtra("sapId");
        pb = new Publisher(getApplicationContext(), this, sapId);


        /*arrayPoints.add(new LatLng(28.536988, 77.342913));
        arrayPoints.add(new LatLng(28.537119, 77.342617));
        arrayPoints.add(new LatLng(28.537184, 77.342677));
        arrayPoints.add(new LatLng(28.537324, 77.342446));
        arrayPoints.add(new LatLng(28.537854, 77.342921));
        arrayPoints.add(new LatLng(28.537583, 77.343405));*/

        RequestQueue queue = Volley.newRequestQueue(this);
        // Instantiate the RequestQueue.
        String url = "http://192.168.99.100:8074/allbuildings";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response", response);
                        // Display the first 500 characters of the response string.
                        Gson gson = new Gson();
                        try {
                            List<BuildingBoundary> buildingBoundaryList = gson.fromJson(response, new TypeToken<List<BuildingBoundary>>() {
                            }.getType());

                            if (buildingBoundaryList != null) {
                                for (BuildingBoundary buildingBoundary : buildingBoundaryList) {
                                    arrayPoints = new ArrayList<>();
                                    for (Boudary boudary : buildingBoundary.getBoudary()) {
                                        arrayPoints.add(new LatLng(Double.parseDouble(boudary.getLatitude()), Double.parseDouble(boudary.getLongitude())));

                                    }
                                    countPolygonPoints();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("MapViewActivity", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(error.getMessage() != null)
                                Log.e("MapViewActivity", error.getMessage());
                            else
                                Log.e("MapViewActivity", "error");
                        }
                    });
        queue.add(stringRequest);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setSubtitle("Your Location");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                pb.setLat(latitude);
                pb.setLongitute(longitude);
                try {

                    pb.publish("iot_data");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        startMqttService();
    }

    private void startMqttService() {
        Intent i = new Intent(this, IotMqttService.class);
        i.putExtra("sapId", sapId);
        startService(i);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        enableMyLocationIfPermitted();
        this.mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (myLocation != null) {
                longitude = myLocation.getLongitude();
                latitude = myLocation.getLatitude();
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(19f).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }

        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(28.535221, 77.34293833333);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }

        }
    }


    @Override
    public void messageArrived(String response) {
        Log.i("MpViewActivity", response);
        Gson gson = new Gson();
        try {
            List<Building> buildings = gson.fromJson(response, new TypeToken<List<Building>>() {
            }.getType());
            this.mMap.clear();
            if (buildings != null) {
                for (Building b : buildings) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(b.getLatitude(), b.getLongitude()))
                            .title(b.getLocation())
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void countPolygonPoints() {
        if (arrayPoints.size() >= 3) {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(arrayPoints);
            polygonOptions.strokeColor(Color.BLUE);
            polygonOptions.strokeWidth(7);
            polygonOptions.fillColor(Color.CYAN);
            Polygon polygon = mMap.addPolygon(polygonOptions);
        }
    }


}
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hcl.pmsiot.constant.PmsConstant;
import com.hcl.pmsiot.data.BoundaryData;
import com.hcl.pmsiot.data.DashboardResponse;
import com.hcl.pmsiot.data.LocationDetailData;
import com.hcl.pmsiot.data.NotificationData;
import com.hcl.pmsiot.data.UserDetailData;
import com.hcl.pmsiot.service.IotMqttService;
import com.hcl.pmsiot.service.MqttHelper;
import com.hcl.pmsiot.service.MqttPublisher;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, PmsMqttCallBack {

    private GoogleMap mMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LocationListener locationListener;
    private String sapId;
    private Location myLocation;
    private double longitude, latitude;
    private MqttPublisher pb;
    private Gson gson;
    private UserDetailData userDetailData;
    private List<Marker> nearByMarker = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        Intent intent = getIntent();
        sapId = intent.getStringExtra("sapId");
        startMqttService(sapId);

        pb = new MqttPublisher(getApplicationContext(), this, MessageFormat.format(PmsConstant.userNearbyNotificationTopic, sapId));

        userDetailData = new UserDetailData();
        userDetailData.setUserId(sapId);
        userDetailData.setOnline(true);
        gson = new Gson();

        /*arrayPoints.add(new LatLng(28.536988, 77.342913));
        arrayPoints.add(new LatLng(28.537119, 77.342617));
        arrayPoints.add(new LatLng(28.537184, 77.342677));
        arrayPoints.add(new LatLng(28.537324, 77.342446));
        arrayPoints.add(new LatLng(28.537854, 77.342921));
        arrayPoints.add(new LatLng(28.537583, 77.343405));*/

        RequestQueue queue = Volley.newRequestQueue(this);
        // Instantiate the RequestQueue.
        String url = PmsConstant.locationUrl;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response", response);
                        // Display the first 500 characters of the response string.
                        GsonBuilder gson = new GsonBuilder();
                        try {
                            Type collectionType = new TypeToken<DashboardResponse<List<LocationDetailData>>>(){}.getType();
                            DashboardResponse<List<LocationDetailData>> dashboardResponse = gson.create().fromJson(response, collectionType);
                            List<LocationDetailData> locationList = dashboardResponse.getData();
                            if (locationList != null) {
                                for (LocationDetailData location : locationList) {
                                    ArrayList<LatLng> arrayPoints  = new ArrayList<>();
                                    for (BoundaryData boundary : location.getBoundary()) {
                                        arrayPoints.add(new LatLng(boundary.getLatitude(), boundary.getLongitude()));

                                    }
                                    drawPolygonPoints(arrayPoints, location.isMaster());
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
        /*Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.setSubtitle("Your Location");*/

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userDetailData.setLatitude(location.getLatitude());
                userDetailData.setLongitude(location.getLongitude());
                try {
                    pb.publish(PmsConstant.userLocationTopic, gson.toJson(userDetailData));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("MapViewActivity", "Mqtt Publish Exception", e);
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



    }

    private void startMqttService(String sapId) {
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
    public void messageArrived(String topic, String response) {
        Log.i("MpViewActivity", response);

        try {
            NotificationData notificationData = gson.fromJson(response, new TypeToken<NotificationData>() {
            }.getType());

            //this.mMap.clear();
            if (notificationData != null) {
                if(notificationData.getData().containsKey("nearby")){
                    List<LocationDetailData> locationList = gson.fromJson(notificationData.getData().get("nearby"), new TypeToken<List<LocationDetailData>>() {}.getType());
                    if(locationList != null) {
                        removeMarker();
                        for(LocationDetailData location  : locationList) {
                            nearByMarker.add(mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .title(location.getName())
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                        };

                    }
                }

            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Mqtt Message Arrived Exception", e);
            e.printStackTrace();
        }
    }

    private void removeMarker(){
        for(Marker marker :nearByMarker){
            marker.remove();
        }
    }
    private void drawPolygonPoints(ArrayList<LatLng> arrayPoints, boolean isMaster) {
        if (arrayPoints.size() >= 3) {
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(arrayPoints);
            polygonOptions.strokeColor(Color.BLUE);
            polygonOptions.strokeWidth(7);
            if(!isMaster)
                polygonOptions.fillColor(Color.CYAN);
            mMap.addPolygon(polygonOptions);
        }
    }


}
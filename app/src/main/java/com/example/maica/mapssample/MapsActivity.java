package com.example.maica.mapssample;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.maica.mapssample.googleplaces.GMapV2Direction;
import com.example.maica.mapssample.googleplaces.GetRotueListTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import static com.example.maica.mapssample.View_List.restaulocation;
import static com.example.maica.mapssample.View_List.restauname;
import static com.example.maica.mapssample.View_List.restaudistance;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GMapV2Direction.DirecitonReceivedListener,GoogleMap.OnInfoWindowClickListener

{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private Marker restauMarker;
    LocationRequest mLocationRequest;
    int PROXIMITY_RADIUS = 10000;
    double latitude, longitude;
    double end_latitude, end_longitude;
    View_List getloc;


    LatLng startPosition;
    String startPositionTitle;
    String startPositionSnippet;

    LatLng destinationPosition;
    String destinationPositionTitle;
    String destinationPositionSnippet;
    ToggleButton tbMode;
    private Button btnDirection;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while loading map..... ");
        progressDialog.show();

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 5000);

        getloc = new View_List();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Object dataTransfer[];
                // D/end_lat: 14.405256605549967
                //     D/end_lng: 120.98458450287582
                dataTransfer = new Object[3];
                String url;
                url = getDirectionsUrl();
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(getloc.restaulatitude, getloc.restaulongitude);
                getDirectionsData.execute(dataTransfer);


                startPosition = new LatLng(latitude, longitude);
                startPositionTitle = "Current Location";
                startPositionSnippet = "Current Location";

                String distance = String.format("%.2f", restaudistance);

                destinationPosition = new LatLng(getloc.restaulatitude, getloc.restaulongitude);
                destinationPositionTitle = restauname + " : " +  distance + " km";
                destinationPositionSnippet = restaulocation;



            }
        }, 5000);

        btnDirection = (Button) findViewById(R.id.btnDirection);

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             getdir();
            }
        });
        tbMode = (ToggleButton) findViewById(R.id.tbMode);

        tbMode.setChecked(true);
    }

    public void getdir(){
        clearMap();

        MarkerOptions mDestination = new MarkerOptions()
                .position(destinationPosition)
                .title(destinationPositionTitle)
                .snippet(destinationPositionSnippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin1));

        MarkerOptions mStart = new MarkerOptions()
                .position(startPosition)
                .title(startPositionTitle)
                .snippet(startPositionSnippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin2));

        mMap.addMarker(mDestination);
        mMap.addMarker(mStart);

        if (tbMode.isChecked()) {
            new GetRotueListTask(MapsActivity.this, startPosition,
                    destinationPosition, GMapV2Direction.MODE_DRIVING, this)
                    .execute();
        } else {
            new GetRotueListTask(MapsActivity.this, startPosition,
                    destinationPosition, GMapV2Direction.MODE_WALKING, this)
                    .execute();
        }
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    public void clearMap() {
        mMap.clear();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private String getDirectionsUrl()
    {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+latitude+","+longitude);
        googleDirectionsUrl.append("&destination="+getloc.restaulatitude+","+getloc.restaulongitude);
        googleDirectionsUrl.append("&key="+"AIzaSyCAcfy-02UHSu2F6WeQ1rhQhkCr51eBL9g");
        return googleDirectionsUrl.toString();
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyBj-cnmMUY21M0vnIKz0k3tD3bRdyZea-Y");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }




    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(false);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));




        LatLng restaulatLng = new LatLng(getloc.restaulatitude, getloc.restaulongitude);

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(restaulatLng);
        markerOptions1.title(restauname+" : "+restaulocation);
        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        restauMarker = mMap.addMarker(markerOptions1);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(restaulatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(11));


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback,
                              boolean instant) {

        if (instant) {
            mMap.animateCamera(update, 1, callback);
        } else {
            mMap.animateCamera(update, 4000, callback);
        }
    }

    @Override
    public void OnDirectionListReceived(List<LatLng> mPointList) {
        if (mPointList != null) {
            PolylineOptions rectLine = new PolylineOptions().width(10).color(
                    Color.RED);
            for (int i = 0; i < mPointList.size(); i++) {
                rectLine.add(mPointList.get(i));
            }
            mMap.addPolyline(rectLine);

            CameraPosition mCPFrom = new CameraPosition.Builder()
                    .target(startPosition).zoom(15.5f).bearing(0).tilt(25)
                    .build();
            final CameraPosition mCPTo = new CameraPosition.Builder()
                    .target(destinationPosition).zoom(15.5f).bearing(0)
                    .tilt(50).build();

            changeCamera(CameraUpdateFactory.newCameraPosition(mCPFrom),
                    new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            changeCamera(CameraUpdateFactory
                                            .newCameraPosition(mCPTo),
                                    new GoogleMap.CancelableCallback() {

                                        @Override
                                        public void onFinish() {

                                            LatLngBounds bounds = new LatLngBounds.Builder()
                                                    .include(startPosition)
                                                    .include(
                                                            destinationPosition)
                                                    .build();
                                            changeCamera(
                                                    CameraUpdateFactory
                                                            .newLatLngBounds(
                                                                    bounds, 50),
                                                    null, false);
                                        }

                                        @Override
                                        public void onCancel() {
                                        }
                                    }, false);
                        }

                        @Override
                        public void onCancel() {
                        }
                    }, true);

        }
    }

    @Override
    public void onInfoWindowClick(Marker selectedMarker) {

        if (selectedMarker.getTitle().equals(startPositionTitle)) {
            Toast.makeText(this, "Marker Clicked: " + startPositionTitle,
                    Toast.LENGTH_LONG).show();
        } else if (selectedMarker.getTitle().equals(destinationPositionTitle)) {
            Toast.makeText(this, "Marker Clicked: " + destinationPositionTitle,
                    Toast.LENGTH_LONG).show();
        }
        selectedMarker.hideInfoWindow();

    }
}

package com.example.maica.mapssample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maica.mapssample.LocationUtil.PermissionUtils;
import com.example.maica.mapssample.googleplaces.GooglePlaces;
import com.example.maica.mapssample.googleplaces.models.Place;
import com.example.maica.mapssample.googleplaces.models.PlacesResult;
import com.example.maica.mapssample.googleplaces.models.Result;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created by Maica on 8/13/2017.
 */

public class Home_Dashboard extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback {

    //@BindView(R.id.btnLocation)Button btnProceed;
    //@BindView(R.id.tvAddress)TextView tvAddress;
    //@BindView(R.id.tvEmpty)TextView tvEmpty;
    //@BindView(R.id.rlPickLocation)RelativeLayout rlPick;

    private RelativeLayout rlPick;
    private TextView tvAddress, tvEmpty;
    private Button restaurant, cafe, bakery;


    // LogCat tag
    private static final String TAG = MyLocationUsingHelper.class.getSimpleName();

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

    private Location mLastLocation;

    // Google client to interact with Google API

    private GoogleApiClient mGoogleApiClient;

    public static double latitude;
    public static double longitude;

    // list of permissions

    public static ArrayList<String> listplacev1 = new ArrayList<String>();
    public static ArrayList<String> listplacerating = new ArrayList<String>();
    public static ArrayList<String> listplacealpha1 = new ArrayList<String>();
    public static ArrayList<String> listplacealpha = new ArrayList<String>();
    public static ArrayList<String> listplacedistance = new ArrayList<String>();
    public static ArrayList<String> listplacedistance1 = new ArrayList<String>();

    public static ArrayList<String> listplacenormal = new ArrayList<String>();


    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    boolean isPermissionGranted;
    Context cnt;

    public static String RESTAURANT_TYPE = "";
    Location targetlocation;
    public static final int REQUEST_LOCATION_CODE = 99;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_dashboard,container,false);

        restaurant = (Button) view.findViewById(R.id.restaurant);
        cafe = (Button) view.findViewById(R.id.cafe);
        bakery = (Button) view.findViewById(R.id.bakery);

        rlPick = (RelativeLayout) view.findViewById(R.id.rlPickLocation);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmpty);


        ButterKnife.bind(getActivity());



        //permissionUtils=new PermissionUtils(cnt);

        //permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        //permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        //permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);


        rlPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();

                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    targetlocation = new Location("");
                    targetlocation.setLatitude(latitude);
                    targetlocation.setLongitude(longitude);
                    getAddress();

                } else {

                    if(restaurant.isEnabled()&&cafe.isEnabled()&&bakery.isEnabled())
                        restaurant.setEnabled(false);
                        cafe.setEnabled(false);
                        bakery.setEnabled(false);

                    showToast("Couldn't get the location. Make sure location is enabled on the device");
                }
            }
        });


        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listplacev1.clear();
                listplacealpha1.clear();
                listplacenormal.clear();
                listplacealpha.clear();
                listplacerating.clear();
                listplacedistance.clear();
                listplacedistance1.clear();


                GooglePlaces googlePlaces = new GooglePlaces("AIzaSyBpHXpsw43sQmdYIsx29TnGFnxGK-B2Q8M");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    PlacesResult result = googlePlaces.getPlaces("restaurant", 300, latitude, longitude);


                    if (result.getStatusCode() == Result.StatusCode.OK) {
                        List<Place> places = result.getPlaces();

                        for (int i=0; i<places.size(); i++){
                            Place yo = places.get(i);

                            final String placename = yo.getName();
                            final String placeaddress = yo.getAddress();
                            Double ratings = yo.getRating();
                            Double distance = yo.getDistanceTo(targetlocation)/1000;

                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("location");
                            String uploadID = rootRef.push().getKey();
                            LocationList profile = new LocationList(uploadID, "", placename);
                            rootRef.child(placename).setValue(profile);

                            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(placename)){
                                            //hjkj
                                        }
                                        else{
                                            String uploadID = rootRef.push().getKey();
                                            LocationList profile = new LocationList(uploadID, "", placename);
                                            rootRef.child(placename).setValue(profile);
                                        }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                            listplacenormal.add(placename + "\n" + placeaddress);

                            listplacev1.add(ratings+" "+ i +" "+placename + "\n" + placeaddress);

                            Log.e("places",yo.getName()+"-"+yo.getAddress());

                            Collections.sort(listplacev1, Collections.<String>reverseOrder());

                            listplacealpha1.add("#@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");

                            Collections.sort(listplacealpha1);
                            String converted = String.format("%.2f", distance );
                            Log.e("check",converted+" #@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");

                            listplacedistance1.add(distance+" #@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");

                            Collections.sort(listplacedistance1);

                            Log.e("checkdistancenum",distance+" #@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");
                            Log.e("checkdistance",listplacedistance1.toString());

                        }

                        for(int ctr = 0; listplacev1.size()-1 >= ctr; ctr++) {

                            String getitem = listplacev1.get(ctr);
                            String stringchecker = "qwe";

                            getitem = getitem.substring(5, getitem.length());

                            if (getitem.contains(" ")) {
                                getitem = getitem.substring(1, getitem.length());
                                stringchecker = getitem.substring(0, 1);
                            }
                            if (stringchecker.contains(" ")) {
                                getitem = getitem.substring(1, getitem.length());
                            }
                            Log.e("item", getitem);

                            listplacerating.add(getitem);
                        }


                        for(int ctr = 0; listplacealpha1.size()-1 >= ctr; ctr++) {
                            String getitem = listplacealpha1.get(ctr);
                            String[] parts = getitem.split("#@");
                            String getplace = (parts[1].trim());


                            listplacealpha.add(getplace);
                        }

                        for(int ctr =0; listplacedistance1.size()-1>=ctr; ctr++){
                            String getitem = listplacedistance1.get(ctr);
                            String[] parts = getitem.split("#@");
                            String getplace = (parts[1].trim());

                            listplacedistance.add(getplace);
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getActivity(), View_List.class);

                RESTAURANT_TYPE = "Restaurant";

                startActivity(intent);

            }
        });


        cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listplacev1.clear();
                listplacealpha1.clear();
                listplacenormal.clear();
                listplacealpha.clear();
                listplacerating.clear();
                listplacedistance.clear();
                listplacedistance1.clear();


                GooglePlaces googlePlaces = new GooglePlaces("AIzaSyBpHXpsw43sQmdYIsx29TnGFnxGK-B2Q8M");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    PlacesResult result = googlePlaces.getPlaces("cafe", 300, latitude, longitude);


                    if (result.getStatusCode() == Result.StatusCode.OK) {
                        List<Place> places = result.getPlaces();

                        for (int i=0; i<places.size(); i++){
                            Place yo = places.get(i);

                            final String placename = yo.getName();
                            final String placeaddress = yo.getAddress();
                            Double ratings = yo.getRating();
                            Double distance = yo.getDistanceTo(targetlocation)/1000;

                            /**final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("location");
                            String uploadID = rootRef.push().getKey();
                            LocationList profile = new LocationList(uploadID, "", placename2);
                            rootRef.child(placename2).setValue(profile);

                            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(placename2)){
                                        //hjkj
                                    }
                                    else{
                                        String uploadID = rootRef.push().getKey();
                                        LocationList profile = new LocationList(uploadID, "", placename2);
                                        rootRef.child(placename2).setValue(profile);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }); **/



                            listplacenormal.add(placename + "\n" + placeaddress);

                            listplacev1.add(ratings+" "+ i +" "+placename + "\n" + placeaddress);

                            Log.e("places",yo.getName()+"-"+yo.getAddress());

                            Collections.sort(listplacev1, Collections.<String>reverseOrder());

                            listplacealpha1.add("#@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");

                            Collections.sort(listplacealpha1);
                            String converted = String.format("%.2f", distance );
                            Log.e("check",converted+" #@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");

                            listplacedistance1.add(distance+" #@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");

                            Collections.sort(listplacedistance1);

                            Log.e("checkdistancenum",distance+" #@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");
                            Log.e("checkdistance",listplacedistance1.toString());

                        }

                        for(int ctr = 0; listplacev1.size()-1 >= ctr; ctr++) {

                            String getitem = listplacev1.get(ctr);
                            String stringchecker = "qwe";

                            getitem = getitem.substring(5, getitem.length());

                            if (getitem.contains(" ")) {
                                getitem = getitem.substring(1, getitem.length());
                                stringchecker = getitem.substring(0, 1);
                            }
                            if (stringchecker.contains(" ")) {
                                getitem = getitem.substring(1, getitem.length());
                            }
                            Log.e("item", getitem);

                            listplacerating.add(getitem);
                        }


                        for(int ctr = 0; listplacealpha1.size()-1 >= ctr; ctr++) {
                            String getitem = listplacealpha1.get(ctr);
                            String[] parts = getitem.split("#@");
                            String getplace = (parts[1].trim());


                            listplacealpha.add(getplace);
                        }

                        for(int ctr =0; listplacedistance1.size()-1>=ctr; ctr++){
                            String getitem = listplacedistance1.get(ctr);
                            String[] parts = getitem.split("#@");
                            String getplace = (parts[1].trim());

                            listplacedistance.add(getplace);
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getActivity(), View_List.class);

                RESTAURANT_TYPE = "Cafe";

                startActivity(intent);

            }
        });

        bakery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listplacev1.clear();
                listplacealpha1.clear();
                listplacenormal.clear();
                listplacealpha.clear();
                listplacerating.clear();
                listplacedistance.clear();
                listplacedistance1.clear();


                GooglePlaces googlePlaces = new GooglePlaces("AIzaSyBpHXpsw43sQmdYIsx29TnGFnxGK-B2Q8M");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    PlacesResult result = googlePlaces.getPlaces("bakery", 300, latitude, longitude);


                    if (result.getStatusCode() == Result.StatusCode.OK) {
                        List<Place> places = result.getPlaces();

                        for (int i=0; i<places.size(); i++){
                            Place yo = places.get(i);

                            final String placename = yo.getName();
                            String placeaddress = yo.getAddress();
                            Double ratings = yo.getRating();
                            Double distance = yo.getDistanceTo(targetlocation)/1000;


                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("location");
                            String uploadID = rootRef.push().getKey();
                            LocationList profile = new LocationList(uploadID, "", placename);
                            rootRef.child(placename).setValue(profile);

                            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(placename)){
                                        //hjkj
                                    }
                                    else{
                                        String uploadID = rootRef.push().getKey();
                                        LocationList profile = new LocationList(uploadID, "", placename);
                                        rootRef.child(placename).setValue(profile);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            listplacenormal.add(placename + "\n" + placeaddress);

                            listplacev1.add(ratings+" "+ i +" "+placename + "\n" + placeaddress);

                            Log.e("places",yo.getName()+"-"+yo.getAddress());

                            Collections.sort(listplacev1, Collections.<String>reverseOrder());

                            listplacealpha1.add("#@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");

                            Collections.sort(listplacealpha1);
                            String converted = String.format("%.2f", distance );
                            Log.e("check",converted+" #@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");

                            listplacedistance1.add(distance+" #@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");

                            Collections.sort(listplacedistance1);

                            Log.e("checkdistancenum",distance+" #@"+placename + "\n" + placeaddress+"#@ #@"+i+"#@");
                            Log.e("checkdistance",listplacedistance1.toString());

                        }

                        for(int ctr = 0; listplacev1.size()-1 >= ctr; ctr++) {

                            String getitem = listplacev1.get(ctr);
                            String stringchecker = "qwe";

                            getitem = getitem.substring(5, getitem.length());

                            if (getitem.contains(" ")) {
                                getitem = getitem.substring(1, getitem.length());
                                stringchecker = getitem.substring(0, 1);
                            }
                            if (stringchecker.contains(" ")) {
                                getitem = getitem.substring(1, getitem.length());
                            }
                            Log.e("item", getitem);

                            listplacerating.add(getitem);
                        }


                        for(int ctr = 0; listplacealpha1.size()-1 >= ctr; ctr++) {
                            String getitem = listplacealpha1.get(ctr);
                            String[] parts = getitem.split("#@");
                            String getplace = (parts[1].trim());


                            listplacealpha.add(getplace);
                        }

                        for(int ctr =0; listplacedistance1.size()-1>=ctr; ctr++){
                            String getitem = listplacedistance1.get(ctr);
                            String[] parts = getitem.split("#@");
                            String getplace = (parts[1].trim());

                            listplacedistance.add(getplace);
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getActivity(), View_List.class);

                RESTAURANT_TYPE = "Bakery";

                startActivity(intent);

            }
        });


        // check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }
        return view;
    }

    private void getLocation() {

        try
        {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }

    }

    public Address getAddress(double latitude,double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    public void getAddress()
    {

        Address locationAddress=getAddress(latitude,longitude);

        if(locationAddress!=null)
        {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+="\n"+address1;

                tvEmpty.setVisibility(View.GONE);
                tvAddress.setText(currentLocation);
                tvAddress.setVisibility(View.VISIBLE);

                if(!restaurant.isEnabled()&&!cafe.isEnabled()&&!bakery.isEnabled())
                    restaurant.setEnabled(true);
                    cafe.setEnabled(true);
                    bakery.setEnabled(true);

            }

        }

    }

    /**
     * Creating google api client object
     * */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }




    /**
     * Method to verify google play services on the device
     * */

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(getActivity(),resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    // Permission check functions


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }




    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

    public void showToast(String message)
    {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

}

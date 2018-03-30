package com.example.maica.mapssample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.maica.mapssample.View_List.restaudistance;
import static com.example.maica.mapssample.View_List.restaulocation;
import static com.example.maica.mapssample.View_List.restauname;
import static com.example.maica.mapssample.View_List.restauphone;
import static com.example.maica.mapssample.View_List.restaurating;
import static com.example.maica.mapssample.View_List.restaureviews;


public class ViewRestaurant extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {


    TextView RestaurantName, RestaurantLocation, RestaurantDetails, RestaurantDistance, tryrating, reviewdetails, reviewrating, reviewuser;
    RatingBar RestaurantRating;
    Button viewonmap, btn_addreview;
    ListView listView;
    private List<AddReview> addreview;
    ProgressDialog progressDialog;
    DatabaseReference mDatabaseRef;
    rateUserAdapter adapter;


    private static final String LOG_TAG = "PlacesAPIActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private static final int PERMISSION_REQUEST_CODE = 100;
    String getplaceid = "";
    DataParser getlist = new DataParser();
    String placeId;
    Home_Dashboard getNearbyPlacesData = new Home_Dashboard();
    View_List getloc = new View_List();
    private GoogleMap mMap;
    private Marker restauMarker;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;

    FirebaseAuth auth;
    String usertype;
    Button savepic, browse;
    DatabaseReference mDatabaseRef2;

    Dialog dialog;
    private List <Menu> menulist;
    MenuListAdapter menuListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid();
        browse = (Button)findViewById(R.id.btn_browse);
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String restauname3 = restauname;
                dialog = new Dialog(ViewRestaurant.this);
                dialog.setTitle("Menu");
                dialog.setContentView(R.layout.menulist);
                menulist = new ArrayList<>();
                final ListView listView3 = (ListView)dialog.findViewById(R.id.menulistview);
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("Menu").child(restauname3);
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        menulist.clear();
                        progressDialog.dismiss();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Menu rateupload = snapshot.getValue(Menu.class);
                           menulist.add(rateupload);
                        }
                        menuListAdapter = new MenuListAdapter(ViewRestaurant.this, R.layout.menu_rowlist, menulist);
                        listView3.setAdapter(menuListAdapter);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                dialog.show();


            }
        });

        //review

        String restauname2 = restauname;
        //Toast.makeText(getApplicationContext(), restauname2, Toast.LENGTH_SHORT).show();

        addreview = new ArrayList<>();

        listView = (ListView) findViewById(R.id.listview_rate);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while loading rating information..... ");
        progressDialog.show();





        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        RestaurantName = (TextView) findViewById(R.id.restaurantname);
        RestaurantLocation = (TextView) findViewById(R.id.restaurantlocation);
        RestaurantDetails = (TextView) findViewById(R.id.restaurantdetail);
        tryrating = (TextView) findViewById(R.id.tryrating);
        RestaurantRating = (RatingBar) findViewById(R.id.rating);
        reviewdetails = (TextView) findViewById(R.id.reviewdetails);
        btn_addreview = (Button) findViewById(R.id.btn_review);
        viewonmap = (Button) findViewById(R.id.viewonmap);
        RestaurantDistance = (TextView) findViewById(R.id.distance);

        String distance = String.format("%.2f", restaudistance);

        btn_addreview.setOnClickListener(this);
        viewonmap.setOnClickListener(this);


        RestaurantName.setText(restauname);
        RestaurantLocation.setText(restaulocation);
        RestaurantDetails.setText(restauphone);
        tryrating.setText(restaurating.toString() + " stars");
        RestaurantRating.setRating(restaurating.floatValue());
        RestaurantDistance.setText(distance + " km");


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("rateuser").child(restauname2);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    addreview.clear();
                    progressDialog.dismiss();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        AddReview rateupload = snapshot.getValue(AddReview.class);
                        addreview.add(rateupload);

                    }
                    adapter = new rateUserAdapter(ViewRestaurant.this, R.layout.rateuser_layout, addreview);
                    listView.setAdapter(adapter);
                }
                else{
                    //Toast.makeText(getApplicationContext(), "Hi2", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    reviewdetails.setText("No Review");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        mGoogleApiClient = new GoogleApiClient.Builder(ViewRestaurant.this)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .build();


        if (true) {
            Object dataTransfer[];
            dataTransfer = new Object[1];
            String restaurant = "restaurant";
            String url;
            url = getUrl(getloc.restaulatitude, getloc.restaulongitude, restaurant);
            GetNearbyPlacesData2 getNearbyPlacesData = new GetNearbyPlacesData2();
            dataTransfer[0] = url;
            getNearbyPlacesData.execute(dataTransfer);
        }

        Log.e("What long and lang", getloc.restaulongitude + " " + getloc.restaulatitude);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.e("list of place_ID", getlist.getplaceid.toString());
                Log.e("list of place_ID Size", String.valueOf(getlist.getplaceid.size()));
                Log.e("list of Name", getlist.getname.toString());
                Log.e("list of Name Size", String.valueOf(getlist.getname.size()));
                Log.e("list of Address", getlist.getaddress.toString());
                Log.e("list of Address Size", String.valueOf(getlist.getaddress.size()));


                placePhotosTask();

                getlist.getplaceid.clear();
                getlist.getname.clear();
                getlist.getaddress.clear();
            }
        }, 5000);

        Handler handlerq = new Handler();
        handlerq.postDelayed(new Runnable() {
            public void run() {
                LatLng restaulatLng = new LatLng(getloc.restaulatitude, getloc.restaulongitude);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(getloc.restaulatitude, getloc.restaulongitude))
                        .title(restauname).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaulatLng, 15));
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                mMap.getUiSettings().setZoomGesturesEnabled(false);
                mMap.getUiSettings().setAllGesturesEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                if (ActivityCompat.checkSelfPermission(ViewRestaurant.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewRestaurant.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(false);
                //  mMap.animateCamera(CameraUpdateFactory.zoomBy(20));
            }
        }, 5000);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //Tester la connexion à l'api
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    private void placePhotosTask() {

        //On définit la view ou l'image sera affichée
        final ImageView display = (ImageView) findViewById(R.id.img_view);

        //On peut trouver l'Id de chaques place sur https://developers.google.com/places/place-id?hl=fr
        if(!getlist.getname.isEmpty()){
            for(int ctr = 0; getlist.getname.size()-1>=ctr;ctr++){

                if(getlist.getname.get(ctr).contains(RestaurantName.getText().toString()) && getlist.getaddress.get(ctr).contains(RestaurantLocation.getText().toString())){
                    getplaceid = getlist.getplaceid.get(ctr);
                    Log.e("successgetting ID",getlist.getplaceid.get(ctr));
                }else{
                    Log.e("Faile ID","Fail");
                }


            }
        }else{
            Log.e("no result","qweqe");
        }

        placeId = getplaceid; // Branderburg tor
        Log.i(LOG_TAG, placeId);

        new PhotoTask(display.getWidth(), display.getHeight(), mGoogleApiClient ) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                Log.i(LOG_TAG,"Entrain de s'executer");
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    // Photo has been loaded, display it.
                    display.setImageBitmap(attributedPhoto.bitmap);
                    Log.i(LOG_TAG,"Ok On post execute");

                    // Display the attribution as HTML content if set.
                    if (attributedPhoto.attribution == null) {
                        Log.i(LOG_TAG,"Pas d'attributs pour la photo");
                    } else {
                        Log.i(LOG_TAG, attributedPhoto.attribution.toString());
                    }

                }
            }
        }.execute(placeId);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace)
    {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 300);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyBpHXpsw43sQmdYIsx29TnGFnxGK-B2Q8M");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
            }
        } else {

            mMap.setMyLocationEnabled(true);
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onClick(View view){
        if (view == btn_addreview){
            startActivity(new Intent(this, Add_review.class));
        }
        if (view == viewonmap){
            startActivity(new Intent(this, MapsActivity.class));
        }
    }
}
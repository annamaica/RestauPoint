package com.example.maica.mapssample;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.maica.mapssample.googleplaces.GooglePlaces;
import com.example.maica.mapssample.googleplaces.models.DetailsResult;
import com.example.maica.mapssample.googleplaces.models.PlaceDetails;
import com.example.maica.mapssample.googleplaces.models.PlaceReview;
import com.example.maica.mapssample.googleplaces.models.PlacesResult;
import com.example.maica.mapssample.googleplaces.models.Result;
import com.example.maica.mapssample.googleplaces.models.Place;

import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.example.maica.mapssample.Home_Dashboard.latitude;
import static com.example.maica.mapssample.Home_Dashboard.longitude;


public class View_List extends AppCompatActivity{

    Spinner sort;
    ListView listplace;
    ArrayAdapter<String> adapter;

    public static String restauid;
    public static String restauname;
    public static String restaulocation;
    public static Double restaurating =0.0;
    public static Double restaudistance = 0.0;
    public static String restauphone;
    public static String restaureviews;
    public static Double restaulatitude;
    public static Double restaulongitude;
    public static boolean bundlegetter;
    public static String typegetter= " ";

    String Tagatrigger= "";
    int getitemnumber=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__list);


        listplace = (ListView) findViewById(R.id.list_view);
        final Home_Dashboard getNearbyPlacesData = new Home_Dashboard();
        adapter = new ArrayAdapter<String>(View_List.this, R.layout.list_item, R.id.name, getNearbyPlacesData.listplacev1);


        sort = (Spinner) findViewById(R.id.sort);

        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String items = sort.getSelectedItem().toString();

                if (items.equals("Alphabetically")){
                   // Collections.sort(getNearbyPlacesData.listplacealpha);
                    adapter = new ArrayAdapter<String>(View_List.this, R.layout.list_item, R.id.name, getNearbyPlacesData.listplacealpha);
                    Tagatrigger="Alpha";

                }
                else if (items.equals("Rating")){
                    adapter = new ArrayAdapter<String>(View_List.this, R.layout.list_item, R.id.name, getNearbyPlacesData.listplacerating);
                    Tagatrigger="Rating";

                }else if(items.equals("Distance")){
                    Tagatrigger="Distance";
                    adapter = new ArrayAdapter<String>(View_List.this, R.layout.list_item, R.id.name, getNearbyPlacesData.listplacedistance);

                }
                else{
                    adapter = new ArrayAdapter<String>(View_List.this, R.layout.list_item, R.id.name, getNearbyPlacesData.listplacenormal);

                }
                listplace.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Location targetlocation = new Location("");
        targetlocation.setLatitude(latitude);
        targetlocation.setLongitude(longitude);



        listplace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Home_Dashboard gettertype = new Home_Dashboard();
                String type = getNearbyPlacesData.RESTAURANT_TYPE;


                if(Tagatrigger.equals("Rating")){
                    String getnumb = "0";
                    String getitem = listplace.getItemAtPosition(i).toString();
                    Log.e("WAZZ1",getitem);

                    for(int ctr = 0; getNearbyPlacesData.listplacerating.size() - 1 >= ctr; ctr++){

                        String checkitem = getNearbyPlacesData.listplacev1.get(ctr);
                        Log.e("what is check item?",checkitem);
                        String itemtrimmer = checkitem.substring(5, checkitem.length());

                        String stringchecker = "qwe";

                        if(itemtrimmer.contains(" ")){
                            itemtrimmer = itemtrimmer.substring(1,itemtrimmer.length());

                            stringchecker = itemtrimmer.substring(0,1);
                        }
                        if(stringchecker.contains(" ")){
                            itemtrimmer = itemtrimmer.substring(1,itemtrimmer.length());
                        }

                        Log.e("check",getitem+" "+itemtrimmer);

                        if(getitem.equals(itemtrimmer)){
                            Log.e("Wqweqweqwe","true");
                            getnumb = checkitem.substring(4,6);
                            ctr = getNearbyPlacesData.listplacerating.size();
                            if(getnumb.contains(" ")){
                                getnumb = getnumb.substring(0,1);
                            }
                        }
                        else{
                            Log.e("Wqweqweqwe","false");
                        }
                    }

                    Log.e("what int?",getnumb);

                    getitemnumber = Integer.parseInt(getnumb);
                }
                else if(Tagatrigger.equals("Alpha")){

                    String getitem = listplace.getItemAtPosition(i).toString();

                    for(int ctr = 0; getNearbyPlacesData.listplacealpha.size() - 1 >= ctr; ctr++){
                        String checkitem = getNearbyPlacesData.listplacealpha1.get(ctr);
                        String getitemtotrim = checkitem;
                        String[] parts = getitemtotrim.split("#@");
                        String getplace = (parts[1].trim());
                        String getnum = (parts[3].trim());


                        getitemnumber = Integer.parseInt(getnum);
                        if(getitem.equals(getplace)){
                            ctr =  getNearbyPlacesData.listplacealpha.size();
                        }

                    }
                } else if(Tagatrigger.equals("Distance")){

                    String getitem = listplace.getItemAtPosition(i).toString();

                    for(int ctr = 0; getNearbyPlacesData.listplacedistance.size() - 1 >= ctr; ctr++){
                        String checkitem = getNearbyPlacesData.listplacedistance1.get(ctr);
                        String getitemtotrim = checkitem;
                        String[] parts = getitemtotrim.split("#@");
                        String getplace = (parts[1].trim());
                        String getnum = (parts[3].trim());


                        getitemnumber = Integer.parseInt(getnum);
                        if(getitem.equals(getplace)){
                            ctr =  getNearbyPlacesData.listplacedistance.size();
                        }

                    }
                }
                else{
                    getitemnumber = i;
                }



                if (type.equals("Restaurant")) {
                    GooglePlaces googlePlaces = new GooglePlaces("AIzaSyBpHXpsw43sQmdYIsx29TnGFnxGK-B2Q8M");
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    try {
                        PlacesResult result = googlePlaces.getPlaces("jollibee", 300, latitude, longitude);


                        if (result.getStatusCode() == Result.StatusCode.OK) {
                            List<Place> places = result.getPlaces();

                            Place yo = places.get(getitemnumber);

                            restauid = yo.getReference();
                            restauname = yo.getName();
                            restaulocation = yo.getAddress();
                            restaurating = yo.getRating();
                            restaudistance = yo.getDistanceTo(targetlocation)/1000;
                            restaulatitude = yo.getLatitude();
                            restaulongitude = yo.getLongitude();

                            DetailsResult detailsResult = googlePlaces.getPlaceDetails(yo.getReference());

                            if (detailsResult.getStatusCode() == Result.StatusCode.OK) {
                                PlaceDetails details = detailsResult.getDetails();

                                restauphone = details.getPhoneNumber();


                                List<PlaceReview> rev = detailsResult.getDetails().getReviews();

                                boolean hasreview = detailsResult.getDetails().hasReviews();
                                PlaceReview revv;
                                restaureviews = "";

                                if(hasreview){
                                    for(int ctr = 0; details.getReviews().size()-1 >= ctr ;ctr++) {
                                        revv = rev.get(ctr);
                                        restaureviews += "Ratings: " + revv.getRating() + "\nReviews: " + revv.getText() + "\nAuthor's Name: " + revv.getAuthorName()+"\n \n";
                                    }
                                }else{
                                    restaureviews = "No reviews";
                                }

                            }

                            Intent intent2 = new Intent(getApplicationContext(), ViewRestaurant.class);

                            startActivity(intent2);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                if (type.equals("Bakery")) {
                    GooglePlaces googlePlaces = new GooglePlaces("AIzaSyBpHXpsw43sQmdYIsx29TnGFnxGK-B2Q8M");
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    try {
                        PlacesResult result = googlePlaces.getPlaces("bakery", 300, latitude, longitude);


                        if (result.getStatusCode() == Result.StatusCode.OK) {
                            List<Place> places = result.getPlaces();

                            Place yo = places.get(getitemnumber);

                            restauid = yo.getReference();
                            restauname = yo.getName();
                            restaulocation = yo.getAddress();
                            restaurating = yo.getRating();
                            restaudistance = yo.getDistanceTo(targetlocation)/1000;
                            restaulatitude = yo.getLatitude();
                            restaulongitude = yo.getLongitude();

                            DetailsResult detailsResult = googlePlaces.getPlaceDetails(yo.getReference());

                            if (detailsResult.getStatusCode() == Result.StatusCode.OK) {
                                PlaceDetails details = detailsResult.getDetails();

                                restauphone = details.getPhoneNumber();


                                List<PlaceReview> rev = detailsResult.getDetails().getReviews();

                                boolean hasreview = detailsResult.getDetails().hasReviews();
                                PlaceReview revv;
                                restaureviews = "";

                                if(hasreview){
                                    for(int ctr = 0; details.getReviews().size()-1 >= ctr ;ctr++) {
                                        revv = rev.get(ctr);
                                        restaureviews += "Ratings: " + revv.getRating() + "\nReviews: " + revv.getText() + "\nAuthor's Name: " + revv.getAuthorName()+"\n \n";
                                    }
                                }else{
                                    restaureviews = "No reviews";
                                }

                            }

                            Intent intent2 = new Intent(getApplicationContext(), ViewRestaurant.class);

                            startActivity(intent2);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                if (type.equals("Cafe")) {
                    GooglePlaces googlePlaces = new GooglePlaces("AIzaSyBpHXpsw43sQmdYIsx29TnGFnxGK-B2Q8M");
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    try {
                        PlacesResult result = googlePlaces.getPlaces("cafe", 300, latitude, longitude);


                        if (result.getStatusCode() == Result.StatusCode.OK) {
                            List<Place> places = result.getPlaces();

                            Place yo = places.get(getitemnumber);


                            restauid = yo.getReference();
                            restauname = yo.getName();
                            restaulocation = yo.getAddress();
                            restaurating = yo.getRating();
                            restaudistance = yo.getDistanceTo(targetlocation)/1000;
                            restaulatitude = yo.getLatitude();
                            restaulongitude = yo.getLongitude();
                            
                            DetailsResult detailsResult = googlePlaces.getPlaceDetails(yo.getReference());

                            if (detailsResult.getStatusCode() == Result.StatusCode.OK) {
                                PlaceDetails details = detailsResult.getDetails();

                                restauphone = details.getPhoneNumber();


                                List<PlaceReview> rev = detailsResult.getDetails().getReviews();

                                boolean hasreview = detailsResult.getDetails().hasReviews();
                                PlaceReview revv;
                                restaureviews = "";

                                if(hasreview){
                                    for(int ctr = 0; details.getReviews().size()-1 >= ctr ;ctr++) {
                                        revv = rev.get(ctr);
                                        restaureviews += "Ratings: " + revv.getRating() + "\nReviews: " + revv.getText() + "\nAuthor's Name: " + revv.getAuthorName()+"\n \n";
                                    }
                                }else{
                                    restaureviews = "No reviews";
                                }


                            }

                            Intent intent2 = new Intent(getApplicationContext(), ViewRestaurant.class);

                            startActivity(intent2);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }


}


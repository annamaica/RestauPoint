package com.example.maica.mapssample;

/**
 * Created by Maica on 9/19/2017.
 */

public class LocationList {
    String locationID;
    String locationPhoto;
    String locationName;

    public LocationList(String locationID, String locationPhoto, String locationName) {
        this.locationID = locationID;
        this.locationPhoto = locationPhoto;
        this.locationName = locationName;
    }
    public String getLocationID () { return locationID; }

    public String getLocationPhoto() {
        return locationPhoto;
    }

    public String getLocationName() {
        return locationName;
    }

    public LocationList (){

    }
}

package com.example.maica.mapssample;

/**
 * Created by Maica on 8/8/2017.
 */

public class Place {
    String reference;
    String placeName;
    String vicinity;

    public Place(String reference, String placeName, String vicinity) {
        this.reference = reference;
        this.placeName = placeName;
        this.vicinity = vicinity;
    }

    public String getReference() {
        return reference;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getVicinity() {
        return vicinity;
    }
}

package com.example.maica.mapssample;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Priyanka
 */

public class GetNearbyPlacesData2 extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    String url;

    @Override
    protected String doInBackground(Object... objects) {
        url = (String)objects[0];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlaceList = null;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
      //  showNearbyPlaces(nearbyPlaceList);
    }


}

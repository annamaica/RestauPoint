package com.example.maica.mapssample;

/**
 * Created by romaingranger on 09/12/2016.
 */

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

/**
 * Created by romaingranger on 30/11/2016.
 */

//L'async task va prendre en entrée le string placeID et retourner l'image de ce lieu
abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

    private int mHeight;
    private int mWidth;
    final private GoogleApiClient mGoogleApiClient;


    //Pour le constructeur ne pas oublier de passer en paramètre googleApiclient

    public PhotoTask(int width, int height, GoogleApiClient googleApiClient) {

        mHeight = height;
        mWidth = width;
        mGoogleApiClient = googleApiClient;

    }

    /**
     * On charge la première photo qui correspond au lieu
     * On passe le place ID en paramètre
     */
    @Override
    protected AttributedPhoto doInBackground(String... params) {


        Log.d(getClass().getName(), "value = " + mHeight);
        Log.d(getClass().getName(), "value = " + mWidth);


        if (params.length != 1) {
            return null;
        }
        final String placeId = params[0];
        Log.d("PlaceId", "doInBackground(" + placeId + ")");
        AttributedPhoto attributedPhoto = null;

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();

            Log.d("Statut", String.format("Ok on a un résultat :). Il y a %d photos ", photoMetadataBuffer.getCount()));


            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                // Get the first bitmap and its attributions.
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                CharSequence attribution = photo.getAttributions();
                // Load a scaled bitmap for this photo.
                Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                        .getBitmap();

                attributedPhoto = new AttributedPhoto(attribution, image);
            }
            // Release the PlacePhotoMetadataBuffer.
            photoMetadataBuffer.release();

        } else {
            Log.d(getClass().getName(), "value = " + result.getStatus().getStatusCode());
            Log.i("Buffer", "Erreur dans la requête");

        }
        return attributedPhoto;
    }

    class AttributedPhoto {

        public final CharSequence attribution;

        public final Bitmap bitmap;

        public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
            this.attribution = attribution;
            this.bitmap = bitmap;
        }
    }
}
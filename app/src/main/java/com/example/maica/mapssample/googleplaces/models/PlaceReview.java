package com.example.maica.mapssample.googleplaces.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceReview implements Parcelable {

	private int mRating = 0;
	private String mAuthorName = "";
	private String mText = "";

	private PlaceReview(Parcel in) {
		mRating = in.readInt();
		mAuthorName = in.readString();
		mText = in.readString();
	}
	
	public PlaceReview(JSONObject jsonReview) {
		try {
			mRating = jsonReview.getInt("rating");
			mAuthorName = jsonReview.getString("author_name");
			mText = jsonReview.getString("text");
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}

	public int getRating() {
		return mRating;
	}
	
	public String getAuthorName() {
		return mAuthorName;
	}
	
	public String getText() {
		return mText;
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(mRating);
		out.writeString(mAuthorName);
		out.writeString(mText);
	}
	
	public static final Creator<PlaceReview> CREATOR = new Creator<PlaceReview>() {

		public PlaceReview createFromParcel(Parcel in) {
			return new PlaceReview(in);
		}

		public PlaceReview[] newArray(int size) {
			return new PlaceReview[size];
		}
	};
}

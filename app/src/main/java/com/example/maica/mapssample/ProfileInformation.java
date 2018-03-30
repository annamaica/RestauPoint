package com.example.maica.mapssample;

/**
 * Created by Maica on 8/13/2017.
 */

public class ProfileInformation {
    String userID;
    String username;
    String lastname;
    String firstname;
    String userImage;
    String url;
    String userType;

    public ProfileInformation(String userID, String username, String lastname, String firstname, String userImage, String url, String userType) {
        this.userID = userID;
        this.username = username;
        this.lastname = lastname;
        this.firstname = firstname;
        this.userImage = userImage;
        this.url = url;
        this.userType = userType;
    }
    public String getUserID () { return userID; }

    public String getUsername() {
        return username;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUrl() {
        return url;
    }

    public String getUserType(){ return userType; }

    public ProfileInformation(){

    }
}


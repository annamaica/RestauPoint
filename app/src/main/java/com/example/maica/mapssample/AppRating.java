package com.example.maica.mapssample;

/**
 * Created by Maica on 10/3/2017.
 */

public class AppRating {
    public String apprateUser;
    public String apprateNum;


    public String getapprateUser() {
        return apprateUser;
    }

    public String getapprateNum() {
        return apprateNum;
    }

    public AppRating(String apprateUser, String apprateNum) {
        this.apprateUser = apprateUser;
        this.apprateNum = apprateNum;
    }

    public AppRating(){

    }
}

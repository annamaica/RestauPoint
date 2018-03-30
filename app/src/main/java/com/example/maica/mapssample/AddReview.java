package com.example.maica.mapssample;

/**
 * Created by Maica on 9/16/2017.
 */

public class AddReview {
    public String rateID;
    public String rateUser;
    public String rateNum;
    public String rateComment;

    public String getRateID() {
        return rateID;
    }

    public String getRateUser() {
        return rateUser;
    }

    public String getRateNum() {
        return rateNum;
    }

    public String getRateComment() {
        return rateComment;
    }
    public AddReview (String rateID, String rateUSer, String rateNum, String rateComment){
        this.rateID = rateID ;
        this.rateUser = rateUSer;
        this.rateNum = rateNum ;
        this.rateComment = rateComment;

    }
    public AddReview(){

    }
}

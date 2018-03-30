package com.example.maica.mapssample;

/**
 * Created by Maica on 9/3/2017.
 */

public class LocationData
{
    private String name;
    private String address;

    public LocationData(String name, String address){
        this.name = name;
        this.address = address;

    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return address;
    }
    public LocationData(){

    }
}

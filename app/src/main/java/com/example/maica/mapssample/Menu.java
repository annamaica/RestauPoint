package com.example.maica.mapssample;

/**
 * Created by Maica on 9/21/2017.
 */

public class Menu {
    String menu;
    String picture;
    String price;

    public String getMenu() {
        return menu;
    }

    public String getPicture() {
        return picture;
    }
    public String getPrice(){
        return price;
    }

    public Menu (String menu, String picture, String price){
        this.menu = menu;
        this.picture = picture;
        this.price = price;
    }
    public Menu (){

    }
}

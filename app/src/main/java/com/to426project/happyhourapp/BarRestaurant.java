package com.to426project.happyhourapp;

import android.location.Location;
import android.media.tv.TvContract;
import android.support.design.widget.CoordinatorLayout;


import com.google.android.gms.maps.model.LatLng;

import java.util.jar.Attributes;

/**
 * Created by tieslobker on 09/11/17.
 */

public class BarRestaurant {

    public String Name;
    public String Location;
    public String HappyHourStart;
    public String HappyHourEnd;
    public String TypeDrink;
    public String TypeFood;
    public String Owner;
    public String Description;
    public String Phone;
    public boolean BusinessUser;
    public String Logo;
    public Double Latitude;
    public Double Longitude;


    public BarRestaurant(){

    }

    public BarRestaurant(String Name, String Location, String HappyHourStart, String HappyHourEnd, String TypeDrink,
                         String TypeFood, String Owner, String Description, String Phone, boolean BusinessUser, String Logo, Double Latitude, Double Longitude){

        this.Name = Name;
        this.Location = Location;
        this.HappyHourStart = HappyHourStart;
        this.HappyHourEnd = HappyHourEnd;
        this.TypeDrink = TypeDrink;
        this.TypeFood = TypeFood;
        this.Owner = Owner;
        this.Description = Description;
        this.Phone = Phone;
        this.BusinessUser = BusinessUser;
        this.Logo = Logo;
        this.Latitude = Latitude;
        this.Longitude = Longitude;

    }

}

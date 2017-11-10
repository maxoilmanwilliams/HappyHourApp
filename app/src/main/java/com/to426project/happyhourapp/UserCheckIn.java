package com.to426project.happyhourapp;

import android.location.Location;

/**
 * Created by tieslobker on 09/11/17.
 */

public class UserCheckIn {

    public String Location;
    public String Username;
    public String UID;
    public String Restaurant;

    public UserCheckIn(String Location,
            String Username,
            String UID,
            String Restaurant){
        this.Location = Location;
        this.Restaurant=Restaurant;
        this.Username = Username;
        this.UID = UID;
    }

    public UserCheckIn(){

    }
    /* Time of check-in    Date/time format
    Location            Coordinates
    User                Username
    Restaurant          Restaurant name */

    }

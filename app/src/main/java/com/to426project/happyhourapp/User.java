package com.to426project.happyhourapp;

/**
 * Created by tieslobker on 09/11/17.
 */

public class User {

    public String Email;
    public String Password;
    public String UID;
    public String Name;
    public String Type;
    public String Favorite;
    public String Location;
    public String Gender;
    public int Age;
    public int Level;

    public User(){

    }

    public User(String Email, String Password, String UID, String Name, String Type, String Favorite,
                String Location, String Gender, int Age, int Level) {

        this.Email = Email;
        this.Password = Password;
        this.UID = UID;
        this.Name = Name;
        this.Type = Type;
        this.Favorite = Favorite;
        this.Location = Location;
        this.Gender = Gender;
        this.Age = Age;
        this.Level = Level;

    }
}

package com.to426project.happyhourapp;

/**
 * Created by tieslobker on 09/11/17.
 */

public class Event {

    public String Location;
    public String StartDateTime;
    public String EndDateTime;
    public String Description;
    public String EventType;
    public String Organizer;

    public Event(){

    }

    public Event(String Location, String StartDateTime, String EndDateTime, String Description, String EventType, String Organizer){

        this.Location = Location;
        this.StartDateTime = StartDateTime;
        this.EndDateTime = EndDateTime;
        this.Description = Description;
        this.EventType = EventType;
        this.Organizer = Organizer;

    }
}

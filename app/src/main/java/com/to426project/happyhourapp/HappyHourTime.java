package com.to426project.happyhourapp;

/**
 * Created by max on 12/8/17.
 */

public class HappyHourTime {
    public String DayOfWeek;
    public String StartTime;
    public String EndTime;

    public HappyHourTime(){}

    public HappyHourTime(String DayOfWeek, String StartTime, String EndTime){
        this.DayOfWeek = DayOfWeek;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
    }
}

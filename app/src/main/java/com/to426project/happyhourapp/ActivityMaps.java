package com.to426project.happyhourapp;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityMaps extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private LatLng inputLocation;
    private String addressDesc;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = this.getClass().getSimpleName();
    private String weekDay;
    private String childNode;
    private ArrayList<BarRestaurant> list = new ArrayList<BarRestaurant>();
    private ArrayList<String> listIDS = new ArrayList<String>();
    private ArrayList<Boolean> happyNowList = new ArrayList<Boolean>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAuth = FirebaseAuth.getInstance();

        //Get name of day of week (i.e, Monday,Tuesday, etc...)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());

        final FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BarRestaurant");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();listIDS.clear();
                for (DataSnapshot dbResult : dataSnapshot.getChildren()){

                    Log.i("barRestaurant ", dbResult.getKey());

                    BarRestaurant barRestaurantAdd = dbResult.getValue(BarRestaurant.class);
                    Log.i("barRestaurant variable", barRestaurantAdd.Name+"\n"+barRestaurantAdd.Description + barRestaurantAdd.Latitude + barRestaurantAdd.Longitude);



                    Boolean happyHourToday = false;
                    Boolean happyHourNow = false;
                    String outputHappyHour="";
                    if (dbResult.hasChild("HappyHours")){
                        Log.i(TAG, "onDataChange: "
                        + dbResult.hasChild("HappyHours"));
                        if (dbResult.child("HappyHours").hasChild(weekDay)){
                            HappyHourTime happyHourTime = dbResult.child("HappyHours").child(weekDay).getValue(HappyHourTime.class);
                            assert happyHourTime != null;
                            if (happyHourTime.StartTime!=null || happyHourTime.EndTime!=null){
                                happyHourToday=true;
                                String start = cleanTimeHelper(happyHourTime.StartTime);
                                String end = cleanTimeHelper(happyHourTime.EndTime);
                                String day = happyHourTime.DayOfWeek;
                                happyHourNow = happyHourNow(happyHourTime.StartTime,happyHourTime.EndTime);
                                outputHappyHour = day + "'s  Happy Hour : " + start + "  -  " +
                                        end;
                            }
                        }
                    }



                    if (barRestaurantAdd.Latitude != null && barRestaurantAdd.Longitude != null){
                        addMarkerToMap(barRestaurantAdd,outputHappyHour, happyHourToday, happyHourNow);
                        list.add(barRestaurantAdd);
                        listIDS.add(dbResult.getKey());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Bundle bundle = getIntent().getParcelableExtra("bundle");
        inputLocation= bundle.getParcelable("inputLocation");
        addressDesc = bundle.getString("addressDesc");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        LatLng myLocation = new LatLng(inputLocation.latitude, inputLocation.longitude);
        float zoomLevel = 15.0f; //This goes up to 21

        mMap.addMarker(new MarkerOptions().position(myLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("You Are Here"))
                .setSnippet(addressDesc);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel));
    }

    public void addMarkerToMap(BarRestaurant barRestaurant, String HappyHourString, Boolean happyHourToday, Boolean happyHourNow){


        LatLng pointToAdd = new LatLng(barRestaurant.Latitude,barRestaurant.Longitude);
        Location myLocation = new Location("Point A");
        myLocation.setLatitude(inputLocation.latitude);
        myLocation.setLongitude(inputLocation.longitude);
        Location barLocation = new Location("Point B");
        barLocation.setLatitude(barRestaurant.Latitude);
        barLocation.setLongitude(barRestaurant.Longitude);

        double distance = (myLocation.distanceTo(barLocation) * 0.000621371);
        Marker marker;
        //TODO Change map marker color?
        if (happyHourToday && !happyHourNow){
            marker = mMap.addMarker(new MarkerOptions().
                    position(pointToAdd)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(barRestaurant.Name).snippet(HappyHourString));

        }else if (happyHourNow){
            marker = mMap.addMarker(new MarkerOptions().
                    position(pointToAdd)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(barRestaurant.Name).snippet(HappyHourString));

        }
        else{
            try{
                marker=mMap.addMarker(new MarkerOptions().
                        position(pointToAdd)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title(barRestaurant.Name).snippet("Distance: " + roundTwoDecimals(distance) + " miles\n"+HappyHourString));
            }catch (Exception exception){
                Log.e(TAG, "addMarkerToMap: ", exception);
            }

        }


    }
    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public String cleanTimeHelper(String time){
        String splitTime[]=time.split(":");
        String hours=splitTime[0].trim();
        Integer iHour = Integer.parseInt(hours);
        String minutes=splitTime[1].trim();
        Integer iMinute = Integer.parseInt(minutes);

        String timeSet = "";
        if (iHour > 12) {
            iHour -= 12;
            timeSet = "PM";
        } else if (iHour == 0) {
            iHour += 12;
            timeSet = "AM";
        } else if (iHour == 12){
            timeSet = "PM";
        }else{
            timeSet = "AM";
        }

        String min = "";
        if (iMinute < 10)
            min = "0" + minutes ;
        else
            min = String.valueOf(minutes);

        // Append in a StringBuilder

        return String.valueOf(iHour) + ':' +
                min + " " + timeSet;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.i(TAG, "onInfoWindowClick: " + marker.getId() + "\n"+ marker.getTag());
        String markerId = marker.getId();
        String numberOnly= markerId.replaceAll("[^0-9]", "");
        Integer whichMarker = Integer.parseInt(numberOnly);
        if (whichMarker>0){
            Log.i(TAG, "onInfoWindowClick: whichMarker  " + (whichMarker-1)+"\n list size: "+ list.size() + "idlist size: "+ listIDS.size());
            Log.i(TAG, "onInfoWindowClick: " + list.get(whichMarker-1).Name
                    + "\n" + listIDS.get(whichMarker-1));
            Bundle args = new Bundle();
            args.putString("childNode", listIDS.get(whichMarker-1));
            Intent newInfoIntent = new Intent(this, ActivityBarInfo.class);
            newInfoIntent.putExtra("bundle", args);
            startActivity(newInfoIntent);
        }


    }
    public Boolean happyHourNow(String startTime, String endTime){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String strNow = sdf.format(new Date());
        String splitTimeNow[]=strNow.split(":");
        String hoursNow=splitTimeNow[0].trim();
        Integer iHourNow = Integer.parseInt(hoursNow);
        String minutesNow=splitTimeNow[1].trim();
        Integer iMinuteNow = Integer.parseInt(minutesNow);

        String splitTimeStart[] = startTime.split(":");
        Integer iHourStart = Integer.parseInt(splitTimeStart[0].trim());
        Integer iMinuteStart = Integer.parseInt(splitTimeStart[1].trim());


        String splitTimeEnd[]= endTime.split(":");
        Integer iHourEnd = Integer.parseInt(splitTimeEnd[0].trim());
        Integer iMinuteEnd = Integer.parseInt(splitTimeEnd[1].trim());
        if (iHourNow<iHourEnd && iHourNow>iHourStart){
            return true;
        }else if (iHourNow==iHourEnd && iMinuteNow<iMinuteEnd){
            return true;
        }else if (iHourNow==iHourEnd && iMinuteNow>iMinuteEnd) {
            return false;
        }else if (iHourNow==iHourStart && iMinuteNow>=iMinuteStart){
            return true;
        }
        else
        {
            return false;
        }



    }
}

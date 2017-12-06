package com.to426project.happyhourapp;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ActivityMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng inputLocation;
    private String addressDesc;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = this.getClass().getSimpleName();
    private ArrayList<BarRestaurant> list = new ArrayList<BarRestaurant>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BarRestaurant");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dbResult : dataSnapshot.getChildren()){
                    Log.i("barRestaurant ", dbResult.getKey());
                    BarRestaurant barRestaurantAdd = dbResult.getValue(BarRestaurant.class);
                    Log.i("barRestaurant variable", barRestaurantAdd.Name+"\n"+barRestaurantAdd.Description);
                    list.add(barRestaurantAdd);
                    addMarkerToMap(barRestaurantAdd.Name, barRestaurantAdd.Description, barRestaurantAdd.Latitude,barRestaurantAdd.Longitude);
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

    public void addMarkerToMap(String Name, String Description, Double Latitude, Double Longitude){


        LatLng pointToAdd = new LatLng(Latitude,Longitude);
        Location myLocation = new Location("Point A");
        myLocation.setLatitude(inputLocation.latitude);
        myLocation.setLongitude(inputLocation.longitude);
        Location barLocation = new Location("Point B");
        barLocation.setLatitude(Latitude);
        barLocation.setLongitude(Longitude);

        double distance = (myLocation.distanceTo(barLocation) * 0.000621371);

        mMap.addMarker(new MarkerOptions().
                position(pointToAdd).
                title(Name)).setSnippet("Distance: " + roundTwoDecimals(distance) + " miles");

    }
    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

}

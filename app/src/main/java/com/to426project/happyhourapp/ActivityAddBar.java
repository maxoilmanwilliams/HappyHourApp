package com.to426project.happyhourapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityAddBar extends Activity implements View.OnClickListener{
    private final String TAG = this.getClass().getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    private EditText editTextBusinessName, editTextLocation,editTextHappyHourStart, editTextHappyHourEnd,
            editTextFoodType,editTextDrinkType, editTextPhone, editTextBusinessDescription;
    private Button buttonCancel, buttonSubmit;
    private LatLng barCoordinates;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mainMenuInflater = getMenuInflater();
        mainMenuInflater.inflate(R.menu.mainmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemHome) {

        }else if (item.getItemId()==R.id.itemFavorites ){
            Intent intentFavorites = new Intent(this, ActivityFavorites.class);
            this.startActivity(intentFavorites);
        }
        else if (item.getItemId()==R.id.itemMap ){
            Intent intentMaps = new Intent(this, ActivityMaps.class);
            this.startActivity(intentMaps);
        }
        else if (item.getItemId()==R.id.itemAddBar){
            Intent intentAddBar = new Intent(this, ActivityAddBar.class);
            this.startActivity(intentAddBar);

        }
        else if (item.getItemId()==R.id.itemLogOut ){
            mAuth.signOut();
            Intent intentLogIn = new Intent(this, MainActivity.class);
            this.startActivity(intentLogIn);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bar);

        editTextBusinessName = (EditText) findViewById(R.id.editTextBusinessName);
        editTextBusinessDescription = (EditText) findViewById(R.id.editTextBusinessDescription);

        editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        editTextHappyHourStart = (EditText) findViewById(R.id.editTextHappyHourStart);
        editTextHappyHourEnd = (EditText) findViewById(R.id.editTextHappyHourEnd);
        editTextFoodType = (EditText) findViewById(R.id.editTextFoodType);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextDrinkType = (EditText) findViewById(R.id.editTextDrinkType);

        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                editTextBusinessName.setText(place.getName());
                editTextLocation.setText(place.getAddress());
                editTextPhone.setText(place.getPhoneNumber());
                barCoordinates = place.getLatLng();
                //editTextBusinessDescription.setText(place.getAttributions());

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    //user signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());
                } else{
                    //user signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }
            }
        };
        buttonCancel.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        String mName = editTextBusinessName.getText().toString();
        String mLocation = editTextLocation.getText().toString();
        String mHappyHourStart = editTextHappyHourStart.getText().toString();
        String mHappyHourEnd = editTextHappyHourEnd.getText().toString();
        String mTypeDrink = editTextDrinkType.getText().toString();
        String mTypeFood = editTextFoodType.getText().toString();
        String mOwner = null;
        String mDescription = editTextBusinessDescription.getText().toString();
        String mPhone = editTextPhone.getText().toString();
        Boolean mBusinessUser = Boolean.FALSE;
        String mLogo= null;
        if (view == buttonSubmit){
            writeNewBarRestaurant(mName,mLocation,mHappyHourStart,mHappyHourEnd,mTypeDrink,mTypeFood,mOwner,mDescription,mPhone,mBusinessUser,mLogo, barCoordinates);
        }
        else if (view == buttonCancel){
            Intent intent = new Intent(this , ActivityWelcomeScreen.class);
            startActivity(intent);

        }

    }

    public void writeNewBarRestaurant(final String Name, String Location, String HappyHourStart, String HappyHourEnd, String TypeDrink,
                                      String TypeFood, String Owner, String Description, String Phone, boolean BusinessUser, String Logo, LatLng barCoordinates) {
        BarRestaurant newBarRestaurant = new BarRestaurant(Name,Location,HappyHourStart,HappyHourEnd,TypeDrink,TypeFood,Owner,Description,Phone,BusinessUser,Logo,barCoordinates.latitude,barCoordinates.longitude);

        final Intent successIntent = new Intent(this , ActivityWelcomeScreen.class);
        DatabaseReference myRef = mDatabase.child("BarRestaurant");
        myRef.push().setValue(newBarRestaurant)
                .addOnCompleteListener(ActivityAddBar.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ActivityAddBar.this, "Bar/Restaurant " + Name + " Created", Toast.LENGTH_LONG).show();

                            startActivity(successIntent);

                        }
                    }
                });

    }
}

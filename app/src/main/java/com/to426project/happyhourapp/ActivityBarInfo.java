package com.to426project.happyhourapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityBarInfo extends Activity implements View.OnClickListener{

    private TextView textViewName,textViewAddress,textViewDescription,textViewPhoneNumber, textViewHappyHour;
    private String childNode;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = this.getClass().getSimpleName();
    private Button buttonUpdateHappyHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BarRestaurant");

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        childNode = bundle.getString("childNode");


        myRef.child(childNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BarRestaurant barRestaurant = dataSnapshot.getValue(BarRestaurant.class);
                updateUI(barRestaurant);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        textViewName = findViewById(R.id.textViewName);

        textViewAddress = findViewById(R.id.textViewAddress);

        textViewDescription = findViewById(R.id.textViewDescription);

        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);

        textViewHappyHour = findViewById(R.id.textViewHappyHour);

        buttonUpdateHappyHour = findViewById(R.id.buttonUpdateHappyHour);
        buttonUpdateHappyHour.setOnClickListener(this);


    }

    public void updateUI(BarRestaurant barRestaurant){
        textViewName.setText(barRestaurant.Name);
        textViewAddress.setText(barRestaurant.Location);
        textViewHappyHour.setText(barRestaurant.HappyHourStart);
        textViewPhoneNumber.setText(barRestaurant.Phone);
        textViewDescription.setText(barRestaurant.Description);

    }

    @Override
    public void onClick(View view) {
        if (view == buttonUpdateHappyHour){
            //Intent newIntent = new Intent(this, FragmentUpdateHappyHour.class);
            //startActivity(newIntent);
            if (findViewById(R.id.fragment_container) != null) {

                // However, if we're being restored from a previous state,
                // then we don't need to do anything and should return or else
                // we could end up with overlapping fragments.

                // Create a new Fragment to be placed in the activity layout
                FragmentUpdateHappyHour firstFragment = new FragmentUpdateHappyHour();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                firstFragment.setArguments(getIntent().getExtras());
                Log.d(TAG, "onClick: ButtonUpdateHappyHour"+ (getIntent().getExtras()));

                // Add the fragment to the 'fragment_container' FrameLayout
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, firstFragment).commit();
            }
        }
    }
}

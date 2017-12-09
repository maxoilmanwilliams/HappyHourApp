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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityBarInfo extends Activity implements View.OnClickListener{

    private TextView textViewName,textViewAddress,textViewDescription,textViewPhoneNumber, textViewHappyHour;
    private String childNode;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = this.getClass().getSimpleName();
    private Button buttonUpdateHappyHour;
    private String weekDay;
    private String outputHappyHour = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);


        //Create auth token and database ref
        mAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BarRestaurant");

        //Get info about which bar/ restaurant from the intent bundle
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        childNode = bundle.getString("childNode");

       //Get name of day of week (i.e, Monday,Tuesday, etc...)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());




        //Get database ref for specific day of week happy hour
        DatabaseReference happyHourRef = myRef.child(childNode).child("HappyHours").child("todo add string day of week");





        myRef.child(childNode).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BarRestaurant barRestaurant = dataSnapshot.getValue(BarRestaurant.class);
                Log.d(TAG, "onDataChange: Has Child   " + weekDay + ":  " + dataSnapshot.hasChild("HappyHours"));
                Boolean happyHourToday = false;
                outputHappyHour="";
                if (dataSnapshot.hasChild("HappyHours")){
                    if (dataSnapshot.child("HappyHours").hasChild(weekDay)){
                        HappyHourTime happyHourTime = dataSnapshot.child("HappyHours").child(weekDay).getValue(HappyHourTime.class);
                        assert happyHourTime != null;
                        if (!happyHourTime.StartTime.isEmpty() || !happyHourTime.EndTime.isEmpty()){
                                String start = cleanTimeHelper(happyHourTime.StartTime);
                                String end = cleanTimeHelper(happyHourTime.EndTime);
                                String day = happyHourTime.DayOfWeek;
                                outputHappyHour = new StringBuilder().append(day).append("'s  Happy Hour : ").append(start).append("  -  ")
                                        .append(end).toString();
                        }else{
                            outputHappyHour= "Happy Hour: None Today";
                        }
                    }
                }
                updateUI(barRestaurant, outputHappyHour);

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

    public void updateUI(BarRestaurant barRestaurant, String outputHappyHour){
        textViewName.setText(barRestaurant.Name);
        textViewAddress.setText(barRestaurant.Location);
        textViewHappyHour.setText(outputHappyHour);
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
}

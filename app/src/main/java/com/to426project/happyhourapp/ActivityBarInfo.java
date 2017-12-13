package com.to426project.happyhourapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityBarInfo extends Activity implements View.OnClickListener{

    private TextView textViewName,textViewAddress,textViewDescription,textViewPhoneNumber, textViewHappyHour;
    private String childNode;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = this.getClass().getSimpleName();
    private Button buttonUpdateHappyHour;
    private String weekDay;
    private String outputHappyHour = "";
    private Boolean updateShowing;
    private MaterialFavoriteButton materialFavoriteButton;
    private BarRestaurant barObject;
    private Boolean initializedFavButton;

    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_info);
        materialFavoriteButton = (MaterialFavoriteButton) findViewById(R.id.FavButton);
        initializedFavButton = false;
        fragment=null;
        //Create auth token and database ref
        mAuth = FirebaseAuth.getInstance();
        user =mAuth.getCurrentUser();
        final FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BarRestaurant");

        //Get info about which bar/ restaurant from the intent bundle
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        childNode = bundle.getString("childNode");



       //Get name of day of week (i.e, Monday,Tuesday, etc...)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());
        updateShowing=false;



        //Get database ref for specific day of week happy hour
        DatabaseReference happyHourRef = myRef.child(childNode).child("HappyHours").child("todo add string day of week");
        DatabaseReference userRef = database.getReference("user").child(mAuth.getCurrentUser().getUid()).child("Favorites").child(childNode);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    materialFavoriteButton.setFavorite(true, false);
                    initializedFavButton = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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
                        if (happyHourTime.StartTime!=null || happyHourTime.EndTime!=null){
                                String start = cleanTimeHelper(happyHourTime.StartTime);
                                String end = cleanTimeHelper(happyHourTime.EndTime);
                                String day = happyHourTime.DayOfWeek;
                                happyHourToday= happyHourNow(happyHourTime.StartTime,happyHourTime.EndTime);
                                outputHappyHour = day + "'s  Happy Hour : " + start + "  -  " +
                                        end;
                        }else{
                            outputHappyHour= "Happy Hour: None Today";
                        }
                    }
                }
                updateUI(barRestaurant, outputHappyHour, happyHourToday);
                barObject= barRestaurant;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (!initializedFavButton){
            materialFavoriteButton.setFavorite(false,false);

        }
        materialFavoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                DatabaseReference userRef = database.getReference("user").child(user.getUid());
                /*
                if (favorite){
                    userRef.child("Favorites").child(childNode).setValue(barObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ActivityBarInfo.this, "New Favorite: \n" + barObject.Name, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }else {
                    userRef.child("Favorites").child(childNode).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ActivityBarInfo.this, "Favorite: " + barObject.Name + " removed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                **/
            }
        });
        materialFavoriteButton.setOnFavoriteAnimationEndListener(new MaterialFavoriteButton.OnFavoriteAnimationEndListener() {
            @Override
            public void onAnimationEnd(MaterialFavoriteButton buttonView, boolean favorite) {
                DatabaseReference userRef = database.getReference("user").child(user.getUid());
                if (favorite){
                    userRef.child("Favorites").child(childNode).setValue(barObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ActivityBarInfo.this, "New Favorite: \n" + barObject.Name, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }else {
                    userRef.child("Favorites").child(childNode).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ActivityBarInfo.this, "Favorite: " + barObject.Name + " removed", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
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

    public void updateUI(BarRestaurant barRestaurant, String outputHappyHour, Boolean happyNow){
        textViewName.setText(barRestaurant.Name);
        textViewAddress.setText(barRestaurant.Location);
        textViewHappyHour.setText(outputHappyHour);
        if (happyNow){
            textViewHappyHour.setTextColor(Color.parseColor("#008000"));
        }
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
                    fragment=firstFragment;
                    // In case this activity was started with special instructions from an
                    // Intent, pass the Intent's extras to the fragment as arguments
                    firstFragment.setArguments(getIntent().getExtras());
                    Log.d(TAG, "onClick: ButtonUpdateHappyHour"+ (getIntent().getExtras()));

                    // Add the fragment to the 'fragment_container' FrameLayout
                    if(!updateShowing){
                        getFragmentManager().beginTransaction()
                                .add(R.id.fragment_container, firstFragment).commit();
                        updateShowing=true;
                    }else{
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.fragment_container)).commit();
                        updateShowing=false;

                }



                }


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

    public String countTimeHelper(String time){
        String splitTime[]=time.split(":");
        String hours=splitTime[0].trim();
        Integer iHour = Integer.parseInt(hours);
        String minutes=splitTime[1].trim();
        Integer iMinute = Integer.parseInt(minutes);

        return String.valueOf(iHour)+ ":" + minutes;
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

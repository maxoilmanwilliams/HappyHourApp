package com.to426project.happyhourapp;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUpdateHappyHour extends Fragment implements View.OnClickListener {

    private CheckBox Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday;
    private TextView textViewStartTime, textViewEndTime;
    private Button buttonStartTime, buttonEndTime, buttonSubmit, buttonCancel;
    View view;
    private static String newTime;
    //private boolean requested;
    private String childNode;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = this.getClass().getSimpleName();
    private DatabaseReference mDatabase;

    public FragmentUpdateHappyHour() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments().getBundle("bundle");
        childNode = bundle.getString("childNode");
        Log.i(TAG, "onCreateView: childnode" + childNode);
        //childNode = getArguments().
                //.getString("childNode");
        view=inflater.inflate(R.layout.fragment_fragment_update_happy_hour, container, false);
         Monday = view.findViewById(R.id.checkBoxMonday);
         Tuesday = view.findViewById(R.id.checkBoxTuesday);
         Wednesday = view.findViewById(R.id.checkBoxWednesday);
         Thursday = view.findViewById(R.id.checkBoxThursday);
         Friday = view.findViewById(R.id.checkBoxFriday);
         Saturday = view.findViewById(R.id.checkBoxSaturday);
         Sunday = view.findViewById(R.id.checkBoxSunday);
         textViewStartTime = view.findViewById(R.id.textViewStartTime);
         textViewEndTime = view.findViewById(R.id.textViewEndTime);
        buttonStartTime = view.findViewById(R.id.buttonStartTime);
        buttonEndTime = view.findViewById(R.id.buttonEndTime);
        buttonSubmit = view.findViewById(R.id.buttonSubmitTimeUpdate);
        buttonCancel = view.findViewById(R.id.buttonCancelTimeUpdate);

        buttonStartTime.setOnClickListener(this);

        buttonEndTime.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == buttonStartTime){
            Log.d("ButtonstartTime", "onClick() called with: view = [" + view + "]");
                DialogFragment newFragment = new TimePickerFragment(textViewStartTime);

                newFragment.show(getFragmentManager(),"timePicker");

                textViewStartTime.setText(newTime);
            Log.d("ButtonStartTimePressed", "newTime=  \n" +newTime );

        }
        else if (view == buttonEndTime){
            DialogFragment newFragment = new TimePickerFragment(textViewEndTime);
            newFragment.show(getFragmentManager(),"timePicker");
            textViewEndTime.setText(newTime);

        }
        else if(view == buttonCancel){
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }
        else if (view==buttonSubmit){
            writeNewHappyHour();
        }
    }

    public void writeNewHappyHour(){
        String mStart = textViewStartTime.getText().toString();
        String mEnd = textViewEndTime.getText().toString();
        DatabaseReference myRef = mDatabase.child("BarRestaurant");

        if (Monday.isChecked()){
            HappyHourTime newHappyHourTime = new HappyHourTime("Monday", mStart,mEnd);
            myRef.child(childNode).child("HappyHours").child("Monday").setValue(newHappyHourTime);

        }else
        {
            HappyHourTime newHappyHourTime = new HappyHourTime("Monday",null,null);
            myRef.child(childNode).child("HappyHours").child("Monday").setValue(newHappyHourTime);
        }
        if (Tuesday.isChecked()){
            HappyHourTime newHappyHourTime = new HappyHourTime("Tuesday", mStart,mEnd);
            myRef.child(childNode).child("HappyHours").child("Tuesday").setValue(newHappyHourTime);

        }else
        {
            HappyHourTime newHappyHourTime = new HappyHourTime("Tuesday", null,null);
            myRef.child(childNode).child("HappyHours").child("Tuesday").setValue(newHappyHourTime);
        }
        if (Wednesday.isChecked()){
            HappyHourTime newHappyHourTime = new HappyHourTime("Wednesday", mStart,mEnd);
            myRef.child(childNode).child("HappyHours").child("Wednesday").setValue(newHappyHourTime);

        }else
        {
            HappyHourTime newHappyHourTime = new HappyHourTime("Wednesday", null,null);
            myRef.child(childNode).child("HappyHours").child("Wednesday").setValue(newHappyHourTime);
        }if (Thursday.isChecked()){
            HappyHourTime newHappyHourTime = new HappyHourTime("Thursday", mStart,mEnd);
            myRef.child(childNode).child("HappyHours").child("Thursday").setValue(newHappyHourTime);

        }else
        {
            HappyHourTime newHappyHourTime = new HappyHourTime("Thursday", null,null);
            myRef.child(childNode).child("HappyHours").child("Thursday").setValue(newHappyHourTime);
        }if (Friday.isChecked()){
            HappyHourTime newHappyHourTime = new HappyHourTime("Friday", mStart,mEnd);
            myRef.child(childNode).child("HappyHours").child("Friday").setValue(newHappyHourTime);

        }else
        {
            HappyHourTime newHappyHourTime = new HappyHourTime("Friday", null,null);
            myRef.child(childNode).child("HappyHours").child("Friday").setValue(newHappyHourTime);
        }if (Saturday.isChecked()){
            HappyHourTime newHappyHourTime = new HappyHourTime("Saturday", mStart,mEnd);
            myRef.child(childNode).child("HappyHours").child("Saturday").setValue(newHappyHourTime);

        }else
        {
            HappyHourTime newHappyHourTime = new HappyHourTime("Saturday", null,null);
            myRef.child(childNode).child("HappyHours").child("Saturday").setValue(newHappyHourTime);
        }if (Sunday.isChecked()){
            HappyHourTime newHappyHourTime = new HappyHourTime("Sunday", mStart,mEnd);
            myRef.child(childNode).child("HappyHours").child("Sunday").setValue(newHappyHourTime);

        }else
        {
            HappyHourTime newHappyHourTime = new HappyHourTime("Sunday", null,null);
            myRef.child(childNode).child("HappyHours").child("Sunday").setValue(newHappyHourTime);
        }
    }



    @SuppressLint("ValidFragment")
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private TextView textView;

        @SuppressLint("ValidFragment")
        public TimePickerFragment(TextView textView){
            this.textView = textView;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            String hour = Integer.toString(hourOfDay);
            String mMinute = Integer.toString(minute);
            newTime = hour + " : " + mMinute;
            textView.setText(newTime);

        }

    }


}

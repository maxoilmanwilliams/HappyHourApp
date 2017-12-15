package com.to426project.happyhourapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ActivityList extends Activity implements AdapterView.OnItemSelectedListener{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = this.getClass().getSimpleName();
    private ListView ListViewBars;
    private ArrayList<BarRestaurant> list = new ArrayList<BarRestaurant>();
    private ArrayList<BarRestaurant> listFull = new ArrayList<BarRestaurant>();

    private ArrayList<String> listIDS = new ArrayList<String>();
    private ArrayList<String> FilteredIDList = new ArrayList<String>();
    private ArrayList<Boolean> FilteredHappyNowList = new ArrayList<Boolean>();
    private ArrayList<Boolean> HappyNowList = new ArrayList<Boolean>();
    private ArrayList<Boolean> HappyTodayList = new ArrayList<Boolean>();
    private ArrayList<Boolean> FilteredHappyTodayList = new ArrayList<Boolean>();

    private Spinner spinnerFilter;
    private CustomAdapter adapter;
    private String weekDay;
    private EditText inputSearch;
    private Boolean filtered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final Intent newInfoIntent = new Intent(this, ActivityBarInfo.class);
        filtered=false;

        //Search Setup
        inputSearch= (EditText)findViewById(R.id.editTextSearchBox);
        //spinnerFilter = (Spinner)findViewById(R.id.spinnerFilterList);
        // Create an ArrayAdapter using the string array and a default spinner layout
        //ArrayAdapter<CharSequence> adapterFilter = ArrayAdapter.createFromResource(this,
                //R.array.spinnerOptions1, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        //adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        //spinnerFilter.setAdapter(adapterFilter);
        //spinnerFilter.setOnItemSelectedListener(this);
        ListViewBars = (ListView)findViewById(R.id.ListViewBars);
        ListViewBars.setClickable(true);
        ListViewBars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "onItemClick: "+ list.get(i).Name);
                //Log.i(TAG, "onItemClick: "+ FilteredIDList.get(i));

                Bundle args = new Bundle();
                if (filtered){
                    args.putString("childNode", FilteredIDList.get(i));
                    Log.i(TAG, "onItemClick: FilteredIDList.get(i) " + i + "\n"+ FilteredIDList.get(i));
                }else {
                    args.putString("childNode", listIDS.get(i));
                    Log.i(TAG, "onItemClick: listIDS.get(i) " + i + "\n"+ listIDS.get(i));

                }
                newInfoIntent.putExtra("bundle", args);
                startActivity(newInfoIntent);
            }
        });

        //SetUp Search Listener
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()){
                    Log.i(TAG, "onTextChanged: Char Sequence is empty trying to reset to the full list");
                    list= listFull;
                    try {
                        adapter.notifyDataSetChanged();

                    }catch (java.lang.NullPointerException exception){
                        Log.e(TAG, "onTextChanged: exception", exception );
                    }
                    filtered=false;
                }else {
                    filtered=true;
                    ActivityList.this.adapter.getFilter().filter(charSequence);

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mAuth = FirebaseAuth.getInstance();

        final FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BarRestaurant");

        //Get name of day of week (i.e, Monday,Tuesday, etc...)
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());
        Log.i(TAG, "onCreate: weekDay" + weekDay);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listFull.clear();
                list.clear();
                listIDS.clear();
                FilteredIDList.clear();
                HappyNowList.clear();
                HappyTodayList.clear();

                for (DataSnapshot dbResult : dataSnapshot.getChildren()){
                    Boolean HappyHourToday=false;
                    Boolean HappyHourNow=false;
                    Log.i("barRestaurant ", dbResult.getKey());
                    Log.i("barRestHappyhours New", String.valueOf(dbResult.hasChild("HappyHours")));

                    BarRestaurant barRestaurantAdd = dbResult.getValue(BarRestaurant.class);
                    Log.i("barRestaurant variable", barRestaurantAdd.Name+"\n"+barRestaurantAdd.Description);
                    if (dbResult.hasChild("HappyHours")){
                        Log.i("barRestHappyhours New2", String.valueOf(dbResult.hasChild("HappyHours")));
                        Log.i("barRestHappyweekday", String.valueOf(dbResult.child("HappyHours").hasChild(weekDay)));

                        if (dbResult.child("HappyHours").hasChild(weekDay)){
                            HappyHourTime happyHourTime = dbResult.child("HappyHours").child(weekDay).getValue(HappyHourTime.class);
                            Log.i(TAG, "onDataChange: happyHourtime Start " + happyHourTime.StartTime + "\nHappyHourEnd" + happyHourTime.EndTime);

                            Log.i(TAG, "onDataChange: happyHourtime Start " + happyHourTime.StartTime + "\nHappyHourEnd" + happyHourTime.EndTime);

                            if (happyHourTime.StartTime!=null && happyHourTime.EndTime!=null){
                                String start = cleanTimeHelper(happyHourTime.StartTime);
                                String end = cleanTimeHelper(happyHourTime.EndTime);
                                String day = happyHourTime.DayOfWeek;
                                HappyHourNow= happyHourNow(happyHourTime.StartTime,happyHourTime.EndTime);
                                HappyHourToday= true;

                            }else{
                                //outputHappyHour= "Happy Hour: None Today";
                            }
                        }
                    }else {
                        Log.w(TAG, "onDataChange: else ");
                    }
                    Log.i(TAG, "onDataChange: HappyHourNow var : " + HappyHourNow);
                    Log.i(TAG, "onDataChange: HappyHourToday var: " + HappyHourToday);
                    HappyNowList.add(HappyHourNow);
                    HappyTodayList.add(HappyHourToday);

                    list.add(barRestaurantAdd);
                    listIDS.add(dbResult.getKey());
                    listFull.add(barRestaurantAdd);
                }
                adapter = new CustomAdapter();
                ListViewBars.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Intent intentFavorites = new Intent(this, ActivityFavorites.class);
        final Intent intentNearby = new Intent(this, ActivityList.class);
        final Intent intentMaps = new Intent(this, ActivityMaps.class);
        final Intent intentList = new Intent(this, ActivityList.class);


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_nearby) {
                    //startActivity(intentMaps);
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                }else if(tabId == R.id.tab_listview){
                    startActivity(intentList);
                }
                else if (tabId == R.id.tab_favorites){
                    startActivity(intentFavorites);
                }
                else if (tabId == R.id.tab_nearby){
                    startActivity(intentNearby);
                }
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mainMenuInflater = getMenuInflater();
        mainMenuInflater.inflate(R.menu.mainmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemHome) {
            Intent intentHome = new Intent(this, ActivityWelcomeScreen.class);
            this.startActivity(intentHome);

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = adapterView.getItemAtPosition(i).toString();
        Log.i(TAG, "onItemSelected: Spinner" + selectedItem);
        if (Objects.equals(selectedItem, "Happy Hour Now")){
            Log.i(TAG, "onItemSelected: Spinner" + selectedItem);

            ActivityList.this.adapter.getFilter().filter(selectedItem);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    class CustomAdapter extends BaseAdapter implements Filterable{

        @Override
        public int getCount() {
            Log.d(TAG, "getCount: "+ list.size());
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.barlistlayout, null);

            TextView textViewName = view.findViewById(R.id.textViewName);
            TextView textViewLocation = view.findViewById(R.id.textViewLocation);
            TextView textViewDescription = view.findViewById(R.id.textViewDescription);

            textViewName.setText(list.get(i).Name);
            textViewLocation.setText(list.get(i).Location);
            if (filtered){
                Log.i(TAG, "getView: Filtered = " + filtered);
                Log.i(TAG, "getView: FilteredHappyNowList.get(i) = " + FilteredHappyNowList.get(i));

                Log.i(TAG, "getView: FilteredHappyTodayList.get(i) = " + FilteredHappyTodayList.get(i));

                if (FilteredHappyNowList.get(i)){
                    Log.i(TAG, "getView: Happy Now Branch");
                    textViewDescription.setText("Happy Hour Now");
                    view.setBackgroundColor(Color.parseColor("#aaffb6"));
                }else if (FilteredHappyTodayList.get(i)){
                    Log.i(TAG, "getView: Happy Today Branch");

                    textViewDescription.setText("Happy Hour Today");
                    view.setBackgroundColor(Color.parseColor("#aae8ff"));

                }else{
                    textViewDescription.setText(list.get(i).Description);
                }
                Log.d(TAG, "getView() returned: " + textViewDescription.getText().toString());

            }else{
                Log.i(TAG, "getView: Filtered = " + filtered);
                Log.d(TAG, "getView: HappyNowList.get(i)"+HappyNowList.get(i));


           if (HappyNowList.get(i)){
                    textViewDescription.setText("Happy Hour Now");
               view.setBackgroundColor(Color.parseColor("#aaffb6"));

           }else if (HappyTodayList.get(i)){
                    textViewDescription.setText("Happy Hour Today");
               view.setBackgroundColor(Color.parseColor("#aae8ff"));

           }else{
                    textViewDescription.setText(list.get(i).Description);
                }
                Log.d(TAG, "getView() returned: " + textViewDescription);
            }


            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<BarRestaurant> FilteredArrayBars = new ArrayList<BarRestaurant>();
                    constraint = constraint.toString().toLowerCase();
                    if (constraint==null || constraint.length()==0){
                        results.values = list;
                        results.count = list.size();
                    }else if (constraint=="Happy Hour Now"){
                        Log.i(TAG, "performFiltering: HappyHourNow");
                        FilteredIDList.clear();
                        FilteredHappyNowList.clear();
                        FilteredHappyTodayList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            BarRestaurant barRestaurantFilteredAdd = list.get(i);
                            if (HappyNowList.get(i))  {
                                FilteredArrayBars.add(barRestaurantFilteredAdd);

                                FilteredIDList.add(listIDS.get(i));
                                FilteredHappyTodayList.add(HappyTodayList.get(i));
                                FilteredHappyNowList.add(HappyNowList.get(i));
                                Log.i(TAG, "performFiltering: CHECK CHECK " + FilteredIDList.size());
                                Log.i(TAG, "performFiltering: check Filtered HappyTodayList" + FilteredHappyTodayList.size());

                                Log.i(TAG, "performFiltering: check Filtered HappyNowList" + FilteredHappyNowList.size());
                            }
                        }
                        results.count = FilteredArrayBars.size();
                        results.values = FilteredArrayBars;
                        Log.e("VALUES", results.values.toString());
                    }
                    else{
                        FilteredIDList.clear();
                        FilteredHappyNowList.clear();
                        FilteredHappyTodayList.clear();
                        for (int i = 0; i < list.size(); i++) {
                            BarRestaurant barRestaurantFilteredAdd = list.get(i);
                            if (barRestaurantFilteredAdd.Name.toLowerCase().startsWith(constraint.toString()))  {
                                FilteredArrayBars.add(barRestaurantFilteredAdd);

                                FilteredIDList.add(listIDS.get(i));
                                FilteredHappyTodayList.add(HappyTodayList.get(i));
                                FilteredHappyNowList.add(HappyNowList.get(i));
                                Log.i(TAG, "performFiltering: CHECK CHECK " + FilteredIDList.size());
                                Log.i(TAG, "performFiltering: check Filtered HappyTodayList" + FilteredHappyTodayList.size());

                                Log.i(TAG, "performFiltering: check Filtered HappyNowList" + FilteredHappyNowList.size());
                            }
                        }
                        results.count = FilteredArrayBars.size();
                        results.values = FilteredArrayBars;
                        Log.e("VALUES", results.values.toString());
                    }



                    return results;
                }
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    list = (ArrayList<BarRestaurant>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
            return filter;
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

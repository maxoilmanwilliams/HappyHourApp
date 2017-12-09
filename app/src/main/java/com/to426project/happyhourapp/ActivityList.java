package com.to426project.happyhourapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class ActivityList extends Activity{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = this.getClass().getSimpleName();
    private ListView ListViewBars;
    private ArrayList<BarRestaurant> list = new ArrayList<BarRestaurant>();
    private ArrayList<String> listIDS = new ArrayList<String>();

    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final Intent newInfoIntent = new Intent(this, ActivityBarInfo.class);
        ListViewBars = (ListView)findViewById(R.id.ListViewBars);
        ListViewBars.setClickable(true);
        ListViewBars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, "onItemClick: "+ list.get(i).Name);
                Log.i(TAG, "onItemClick: "+ listIDS.get(i).toString());

                Bundle args = new Bundle();
                args.putString("childNode", listIDS.get(i).toString());
                newInfoIntent.putExtra("bundle", args);
                startActivity(newInfoIntent);
            }
        });
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
                    listIDS.add(dbResult.getKey());
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



        class CustomAdapter extends BaseAdapter{

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
            textViewDescription.setText(list.get(i).Description);
            Log.d(TAG, "getView() returned: " + textViewDescription);

            return view;
        }
    }
}

package com.to426project.happyhourapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class ActivityEvents extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = this.getClass().getSimpleName();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        mAuth = FirebaseAuth.getInstance();

        final Intent intentFavorites = new Intent(this, ActivityFavorites.class);
        //final Intent intentNearby = new Intent(this, ActivityList.class);
        final Intent intentEvent = new Intent(this, ActivityEvents.class);

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
                else if (tabId == R.id.tab_event){
                    startActivity(intentEvent);
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
}

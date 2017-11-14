package com.to426project.happyhourapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class ActivityWelcomeScreen extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        final Intent intentHome = new Intent(this, ActivityEvents.class);
        final Intent intentFavorites = new Intent(this, ActivityFavorites.class);
        final Intent intentNearby = new Intent(this, ActivityList.class);


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_favorites) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                }else if(tabId == R.id.tab_friends){
                    startActivity(intentHome);
                }
                else if (tabId == R.id.tab_nearby){
                    startActivity(intentNearby);
                }
            }
        });
    }
}

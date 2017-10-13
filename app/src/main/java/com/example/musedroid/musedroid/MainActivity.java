package com.example.musedroid.musedroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static MuseumAdapter museumAdapter;
    public static GetFirebase getFirebase;
    public static String appLanguage;
    public ProgressBar progressBar;
    private ViewPager viewPager;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private String[] pageTitle = {"All Museums", "Near by Museums", "Fragment 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUserLanguage();
        progressBar = findViewById(R.id.mainProgressBar);
        progressBar.setVisibility(View.GONE);
        if (savedInstanceState == null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.getVisibility();
            getFirebase = new GetFirebase();
            museumAdapter = new MuseumAdapter(new ArrayList<Museum>());
            //museumAdapter = getFirebase.listViewFromFirebase(new MuseumAdapter(new ArrayList<Museum>()), progressBar, appLanguage);
        }


        viewPager = (ViewPager) findViewById(R.id.view_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        setSupportActionBar(toolbar);

        //create default navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //setting Tab layout (number of Tabs = number of ViewPager pages)
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for (int i = 0; i < 3; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(pageTitle[i]));
        }

        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //handling navigation view item event
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        //set viewpager adapter
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //change Tab selection when swipe ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //change ViewPager page when tab selected

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    //set language in preferences manager equals to locale of the current phone
    private void setUserLanguage() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        //Check phone shareprefenrences if it is null set it to locale else set it equals to user
        // checked preference
        appLanguage = sharedPrefs.getString("prefAppLanguage", "NULL");
        if (appLanguage.equals("NULL")) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("prefAppLanguage", Locale.getDefault().getLanguage());
            editor.apply();
            appLanguage = sharedPrefs.getString("prefAppLanguage", "NULL");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.go_to_maps) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //to prevent current item select over and over
        if (item.isChecked()) {
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.Profile:
                item.setCheckable(false);
                if (auth.getCurrentUser() != null) {
                    item.setChecked(false);
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    item.setChecked(false);
                }
                break;
            case R.id.Logout:
                item.setCheckable(false);
                if (auth.getCurrentUser() != null) {
                    auth.signOut();
                    item.setChecked(false);
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
                break;
            case R.id.Settings:
                item.setCheckable(false);
                if (auth.getCurrentUser() != null) {

                    startActivity(new Intent(MainActivity.this, UserSettingActivity.class));
                }
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
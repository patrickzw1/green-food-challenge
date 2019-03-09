package com.example.hca127.greenfood;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hca127.greenfood.fragments.AboutFragment;
import com.example.hca127.greenfood.fragments.AddingFoodFragment;
import com.example.hca127.greenfood.fragments.CommunityFragment;
import com.example.hca127.greenfood.fragments.FacebookShareFragment;
import com.example.hca127.greenfood.fragments.LoginFragment;
import com.example.hca127.greenfood.fragments.PledgeFragment;
import com.example.hca127.greenfood.fragments.ProfileFragment;
import com.example.hca127.greenfood.fragments.RestaurantFragment;
import com.example.hca127.greenfood.fragments.ResultFragment;
import com.example.hca127.greenfood.fragments.SuggestionFragment;
import com.example.hca127.greenfood.fragments.ViewMealsFragment;
import com.example.hca127.greenfood.objects.Diet;
import com.example.hca127.greenfood.objects.LocalUser;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawer;
    private LocalUser mLocalUser;
    private TextView mUserEmail;
    private TextView mUserName;
    private FirebaseUser mFireUser;
    private FirebaseAuth mAuthentication;
    private ImageView mProfile;
    private DatabaseReference mDatabase;

    private ArrayList<Integer> mIconIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideKeyboard(this);
        mAuth = FirebaseAuth.getInstance();
        mDrawer = findViewById(R.id.drawer);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.Navigation_drawer_open, R.string.Navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset){
                hideKeyboard(MainActivity.this);
            }
        };
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // load local user from shared Preferences
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("mLocalUser", "");

        mLocalUser = gson.fromJson(json, LocalUser.class);
        if(mLocalUser == null){
            mLocalUser = new LocalUser();
        }else{
            DatabaseReference userDatabase = mDatabase.child(mLocalUser.getUserId());
            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mLocalUser.setName((String) dataSnapshot.child("name").getValue());
                    double temp_pledge = (double)dataSnapshot.child("pledge").getValue();
                    mLocalUser.setPledge(temp_pledge);

                    mLocalUser.setCity((int)(long) dataSnapshot.child("city").getValue());
                    mLocalUser.setProfileIcon((int)(long)dataSnapshot.child("icon_index").getValue());

                    setLocalUser(mLocalUser);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        mIconIds = new ArrayList<>(Arrays.asList(
                R.drawable.tree, R.drawable.sunglasses, R.drawable.dog,
                R.drawable.cat, R.drawable.monkey, R.drawable.ghost
        ));

        updateHeader();
        // connect to Firebase Auth and update user if exist
        mAuthentication = FirebaseAuth.getInstance();
        mFireUser = mAuthentication.getCurrentUser();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddingFoodFragment()).commit();
            navigationView.setCheckedItem(R.id.menu_add_food);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        }else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.menu_community:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CommunityFragment()).addToBackStack(null).commit();
                break;
            case R.id.menu_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).addToBackStack(null).commit();
                break;
            case R.id.menu_calculator:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AddingFoodFragment()).addToBackStack("AddingFoodFragment").commit();
                break;
            case R.id.menu_user:
                if(mLocalUser.getUserId().equals("")){
                    Toast.makeText(this, "Area Only Opens For Logged-in Users",Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new LoginFragment()).addToBackStack(null).commit();
                    NavigationView navigationView = findViewById(R.id.navigation_view);
                    navigationView.setCheckedItem(R.id.menu_community);
                }else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).addToBackStack(null).commit();
                }
                break;
            case R.id.menu_pledge:
                if(mLocalUser.getUserId().equals("")){
                    Toast.makeText(this, "Area Only Opens For Logged-in Users",Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new LoginFragment()).addToBackStack(null).commit();
                    NavigationView navigationView = findViewById(R.id.navigation_view);
                    navigationView.setCheckedItem(R.id.menu_community);
                }else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PledgeFragment()).addToBackStack(null).commit();
                }
                break;
            case R.id.menu_meal:
                if(mLocalUser.getUserId().equals("")){
                    Toast.makeText(this, "Area Only Opens For Logged-in Users",Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new LoginFragment()).addToBackStack(null).commit();
                    NavigationView navigationView = findViewById(R.id.navigation_view);
                    navigationView.setCheckedItem(R.id.menu_community);
                }else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ViewMealsFragment()).addToBackStack(null).commit();
                }
                break;
            case R.id.menu_result:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ResultFragment()).addToBackStack(null).commit();
                break;
            case R.id.menu_suggestion:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SuggestionFragment()).addToBackStack(null).commit();
                break;
            case R.id.menu_Login:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LoginFragment()).addToBackStack(null).commit();
                break;
            case R.id.menu_LogOff:
                mAuth.signOut();
                mLocalUser = new LocalUser();
                updateHeader();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LoginFragment()).addToBackStack(null).commit();
                hideKeyboard(this);
                Toast.makeText(this, "Successfully Logged Off!",Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_facebook:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FacebookShareFragment()).addToBackStack(null).commit();
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Diet getLocalUserDiet() {
        return mLocalUser.getCurrentDiet();
    }

    public void setLocalUserDiet(Diet diet) {
        mLocalUser.setCurrentDiet(diet);
    }

    public LocalUser getLocalUser() {
        return mLocalUser;
    }

    public void setLocalUser(LocalUser user) {
        mLocalUser = user;
        saveLocalUser();
        updateHeader();
    }

    private void saveLocalUser() {
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mLocalUser);
        prefsEditor.putString("mLocalUser", json);
        prefsEditor.apply();
    }

    private void updateHeader() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        mUserEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
        mUserEmail.setText(mLocalUser.getUserEmail());
        mUserName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        mUserName.setText(mLocalUser.getName());
        mProfile = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profilePicture);
        mProfile.setImageResource(mIconIds.get(mLocalUser.getProfileIcon()));

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}

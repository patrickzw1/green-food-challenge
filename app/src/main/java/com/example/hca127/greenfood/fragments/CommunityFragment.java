package com.example.hca127.greenfood.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hca127.greenfood.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CommunityFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ImageView mAbout;
    private Spinner mCitySpinner;
    private TextView mParticipantDisplay, mReducedDisplay, mTreesDisplay, mAverageDisplay;
    private String mCurrentCity;
    private DatabaseReference mDatabase;
    private ProgressBar mProgressBar;
    private ArrayList<TextView> mOtherPledges;
    private ArrayList<ImageView> mOtherIcons;
    private ScrollView mScroll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mParticipantDisplay = (TextView) view.findViewById(R.id.community_pledge_participant);
        mReducedDisplay = (TextView)view.findViewById(R.id.community_pledge_total);
        mTreesDisplay = (TextView)view.findViewById(R.id.community_pledge_trees);
        mAverageDisplay = (TextView)view.findViewById(R.id.community_pledge_average);
        mScroll = view.findViewById(R.id.scroll);

        mOtherPledges = new ArrayList<>();
        mOtherPledges.add((TextView)view.findViewById(R.id.otherPledge0));
        mOtherPledges.add((TextView)view.findViewById(R.id.otherPledge1));
        mOtherPledges.add((TextView)view.findViewById(R.id.otherPledge2));
        mOtherPledges.add((TextView)view.findViewById(R.id.otherPledge3));
        mOtherPledges.add((TextView)view.findViewById(R.id.otherPledge4));
        mOtherPledges.add((TextView)view.findViewById(R.id.otherPledge5));
        mOtherPledges.add((TextView)view.findViewById(R.id.otherPledge6));
        mOtherPledges.add((TextView)view.findViewById(R.id.otherPledge7));
        mOtherIcons = new ArrayList<>();
        int[] tempId = {R.id.otherIcon0, R.id.otherIcon1, R.id.otherIcon2, R.id.otherIcon3,
                R.id.otherIcon4, R.id.otherIcon5, R.id.otherIcon6, R.id.otherIcon7};
        for(int i = 0; i<tempId.length; i++){
            mOtherIcons.add((ImageView)view.findViewById(tempId[i]));
        }


        mCitySpinner = view.findViewById(R.id.community_cityList);
        mCitySpinner.setOnItemSelectedListener(this);
        mCurrentCity = "total";
        mCitySpinner.setSelection(0);
        mProgressBar = view.findViewById(R.id.community_progress_bar);

        SharedPreferences google_account_info = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = google_account_info.getString("name","");
        String email = google_account_info.getString("email","");

        mAbout = (ImageView) view.findViewById(R.id.about_button_community);
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).addToBackStack(null).commit();
                NavigationView navigationView = getActivity().findViewById(R.id.navigation_view);
                navigationView.setCheckedItem(R.id.menu_about);
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mProgressBar.setVisibility(View.VISIBLE);
        Query userPledges;

        if(position==0){
            mCurrentCity = "total";
            userPledges= mDatabase.child("users").orderByChild("pledge");
        }else {
            mCurrentCity = Integer.toString(position);
            userPledges= mDatabase.child("users").orderByChild("city").equalTo(position);
        }

        DatabaseReference community = mDatabase.child("Community pledge").child(mCurrentCity);
        community.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOParticipant = (int)(long)dataSnapshot.child("participant").getValue();
                mParticipantDisplay.setText(String.valueOf(numOParticipant));
                double pledged = (double)dataSnapshot.child("pledge").getValue();
                mReducedDisplay.setText(String.valueOf(Math.round(pledged*100)/100));
                if(numOParticipant != 0){
                    mAverageDisplay.setText(String.valueOf(Math.round(pledged/numOParticipant*100)/100));
                }else {
                    mAverageDisplay.setText("0");
                }
                mTreesDisplay.setText(String.valueOf(Math.round(pledged/22*100)/100));
                mProgressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        userPledges.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userChildren = dataSnapshot.getChildren();
                clearOtherPledges();
                int index = 0;
                ArrayList<DataSnapshot> userReference = new ArrayList<>();
                for(DataSnapshot user : userChildren){
                    userReference.add(user);
                    if(userReference.size()>8){
                        userReference.remove(0);
                    }
                }
                for (int i = userReference.size()-1; i>=0; i--) {
                    DataSnapshot user = userReference.get(i);
                    double tempDouble = (double)user.child("pledge").getValue();
                    tempDouble = Math.round(tempDouble*100)/100;
                    String temp = String.format(getResources().getString(R.string.community_other_pledge),
                            user.child("name").getValue(), String.valueOf(tempDouble));
                    temp = temp + " kgs";
                    mOtherPledges.get(index).setText(temp);
                    mOtherPledges.get(index).setVisibility(View.VISIBLE);
                    int tempInt = (int)(long)user.child("icon_index").getValue();
                    int[] pictureIds = {
                            R.drawable.tree, R.drawable.sunglasses, R.drawable.dog,
                            R.drawable.cat, R.drawable.monkey, R.drawable.ghost
                    };
                    mOtherIcons.get(index).setImageResource(pictureIds[tempInt]);
                    mOtherIcons.get(index).setVisibility(View.VISIBLE);
                    index++;
                    if(index >= mOtherPledges.size() || index>= mOtherIcons.size()){
                        break;
                    }
                }


                mProgressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference users = mDatabase.child("users");
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void clearOtherPledges() {
        for(int i = 0; i<mOtherIcons.size(); i++){
            mOtherPledges.get(i).setText("");
            mOtherIcons.get(i).setVisibility(View.GONE);
            mOtherPledges.get(i).setVisibility(View.GONE);
        }

    }
}

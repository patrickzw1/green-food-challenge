package com.example.hca127.greenfood.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import com.example.hca127.greenfood.MainActivity;
import com.example.hca127.greenfood.R;
import com.example.hca127.greenfood.objects.Diet;
import com.example.hca127.greenfood.objects.LocalUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Arrays;

public class AddingFoodFragment extends Fragment {

    private Diet mDiet;
    private ImageView mNextImageView;
    private ArrayList<RadioGroup> mRadioGroups;
    private ArrayList<Integer> mRadioChoices;
    private LocalUser mLocalUser;
    private DatabaseReference mUserData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adding_food, container, false);

        mDiet = ((MainActivity)getActivity()).getLocalUserDiet();
        mRadioGroups = new ArrayList<>();
        mRadioChoices = new ArrayList<>();

        mLocalUser = ((MainActivity)getActivity()).getLocalUser();
        mUserData = FirebaseDatabase.getInstance().getReference().child("users").child(mLocalUser.getUserId());

        mNextImageView = view.findViewById(R.id.nextImageView);
        mNextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getUserInput();

                ((MainActivity)getActivity()).setLocalUserDiet(mDiet);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ResultFragment()).addToBackStack(null).commit();
                ((NavigationView)getActivity().findViewById(R.id.navigation_view)).setCheckedItem(R.id.menu_result);
            }
        });

        int[] RadioId = {
                R.id.beefRadioGroup, R.id.lambRadioGroup, R.id.chickenRadioGroup,
                R.id.fishRadioGroup, R.id.porkRadioGroup, R.id.eggRadioGroup,
                R.id.vegRadioGroup, R.id.breadRadioGroup
        };
        int[] RadioCheck = {
                R.id.beefRadio2, R.id.lambRadio2, R.id.chickenRadio2,
                R.id.fishRadio2, R.id.porkRadio2, R.id.eggRadio2,
                R.id.veggieRadio2, R.id.breadRadio2
        };

        for(int i = 0; i<RadioId.length; i++) {
            RadioGroup tempGroup = (RadioGroup) view.findViewById(RadioId[i]);
            mRadioGroups.add(tempGroup);
            tempGroup.check(RadioCheck[i]);
        }

        return view;
    }

    public void getUserInput() {
        String choice;
        String level;

        ArrayList<String> mFoodNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ingredient_name)));
        ArrayList<String> mCarbonCoefficient = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.carbon_coefficient)));
        ArrayList<String> mAverageConsumption = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.annual_average_consumption)));

        for (int i = 0; i<mRadioGroups.size(); i++) {
            mRadioChoices.add(mRadioGroups.get(i).getCheckedRadioButtonId());
            choice = getResources().getResourceEntryName(mRadioChoices.get(i));
            level = choice.substring( choice.length()-1, choice.length());
            mDiet.addNewIngredient(mFoodNames.get(i), Float.parseFloat(mCarbonCoefficient.get(i)),
                    Float.parseFloat(mAverageConsumption.get(i)),
                    Float.parseFloat(level));
        }
        ((MainActivity)getActivity()).setLocalUserDiet(mDiet);
    }
}

package com.example.hca127.greenfood.fragments;

import android.support.design.widget.NavigationView;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hca127.greenfood.GlideApp;
import com.example.hca127.greenfood.MainActivity;
import com.example.hca127.greenfood.R;
import com.example.hca127.greenfood.objects.Diet;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class ViewMealsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private ArrayList<TextView> mMealRestaurants;
    private ArrayList<TextView> mMealTitles;
    private ArrayList<TextView> mMealDescriptions;
    private ArrayList<TextView> mMealLocations;
    private ArrayList<TextView> mMealIngredients;
    private ArrayList<ImageView> mMealImages;
    private ArrayList<ImageView> mDescriptionBoxes;
    private Spinner mCityFilter;
    private Spinner mIngredientFilter;
    private Button mMyMeals;
    private DatabaseReference mDatabase;
    private FirebaseStorage mCloudStorage;

    private int mIngredientIndex;
    private int mCityIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_meals, container, false);
        mCloudStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("meals");
        final int[] restaurants = {R.id.meal_1_restaurant, R.id.meal_2_restaurant,
                R.id.meal_3_restaurant, R.id.meal_4_restaurant, R.id.meal_5_restaurant};
        final int[] title = {R.id.meal_1_title, R.id.meal_2_title,
                R.id.meal_3_title, R.id.meal_4_title, R.id.meal_5_title};
        final int[] description = {R.id.meal_1_description, R.id.meal_2_description,
                R.id.meal_3_description, R.id.meal_4_description, R.id.meal_5_description};
        final int[] location = {R.id.meal_1_location, R.id.meal_2_location,
                R.id.meal_3_location, R.id.meal_4_location, R.id.meal_5_location};
        final int[] ingredient = {R.id.meal_1_ingredient, R.id.meal_2_ingredient,
                R.id.meal_3_ingredient, R.id.meal_4_ingredient, R.id.meal_5_ingredient};
        final int[] image = {R.id.meal_1_image, R.id.meal_2_image, R.id.meal_3_image,
                R.id.meal_4_image, R.id.meal_5_image};
        final int[] descriptionBox = {R.id.meal_1_description_box, R.id.meal_2_description_box,
                R.id.meal_3_description_box, R.id.meal_4_description_box, R.id.meal_5_description_box};
        mMealTitles = new ArrayList<>();
        mMealLocations = new ArrayList<>();
        mMealDescriptions = new ArrayList<>();
        mMealRestaurants = new ArrayList<>();
        mMealIngredients = new ArrayList<>();
        mMealImages = new ArrayList<>();
        mDescriptionBoxes = new ArrayList<>();
        for(int i = 0; i<title.length; i++){
            mMealTitles.add((TextView)view.findViewById(title[i]));
            mMealLocations.add((TextView)view.findViewById(location[i]));
            mMealRestaurants.add((TextView)view.findViewById(restaurants[i]));
            mMealIngredients.add((TextView)view.findViewById(ingredient[i]));
            mMealDescriptions.add((TextView)view.findViewById(description[i]));
            mMealImages.add((ImageView) view.findViewById(image[i]));
            mDescriptionBoxes.add((ImageView) view.findViewById(descriptionBox[i]));
        }

        mMyMeals = view.findViewById(R.id.my_meals_button);
        mMyMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RestaurantFragment()).addToBackStack(null).commit();
            }
        });
        mCityFilter = view.findViewById(R.id.city_filter);
        mCityFilter.setOnItemSelectedListener(this);
        mCityFilter.setSelection(0);
        mIngredientFilter = view.findViewById(R.id.ingredient_filter);
        mIngredientFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mIngredientIndex = position;
                updateView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mCityIndex = position;
        updateView();
    }

    private void updateView(){
        Query mealList;
        final String[] Ingredients = getActivity().getResources().getStringArray(R.array.ingredient_name);
        final String[] Citys = getActivity().getResources().getStringArray(R.array.city_array);

        if(mCityIndex==0){
            mealList= mDatabase;
        }else {
            mealList= mDatabase.orderByChild("city index").equalTo(mCityIndex);
        }
        mealList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> mealChildren = dataSnapshot.getChildren();
                resetViews();
                ArrayList<DataSnapshot> mealOrderedList = new ArrayList<>();
                for(DataSnapshot meal : mealChildren){
                    if(mIngredientIndex ==0 || (long)meal.child("protein").getValue() == mIngredientIndex-1) {
                        mealOrderedList.add(meal);
                        if (mealOrderedList.size() > 5) {
                            mealOrderedList.remove(0);
                        }
                    }
                }
                ArrayList<DataSnapshot> mealRevertedList = new ArrayList<>();
                for(int i = 0; i<mealOrderedList.size(); i++){
                    mealRevertedList.add(mealOrderedList.get(mealOrderedList.size()-i-1));
                }
                int i = 0;
                for(DataSnapshot meal : mealRevertedList){
                    ShowView(i);
                    mMealRestaurants.get(i).setText((String) meal.child("restaurant").getValue());
                    mMealTitles.get(i).setText((String) meal.child("meal name").getValue());
                    mMealDescriptions.get(i).setText((String) meal.child("description").getValue());
                    mMealIngredients.get(i).setText(Ingredients[
                            (int)(long) meal.child("protein").getValue()]);
                    mMealLocations.get(i).setText(Citys[
                            (int) (long) meal.child("city index").getValue()]);
                    String imageLink = (String) meal.child("imageLink").getValue();
                    if (imageLink == null || !imageLink.equals("")) {
                        StorageReference httpsReference = mCloudStorage.getReferenceFromUrl(imageLink);
                        GlideApp.with(((MainActivity) getActivity()).getApplicationContext())
                                .load(httpsReference)
                                .into(mMealImages.get(i));
                    } else {
                        mMealImages.get(i).setImageResource(R.drawable.ic_meal);
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ShowView(int i) {
        mMealTitles.get(i).setVisibility(View.VISIBLE);
        mMealLocations.get(i).setVisibility(View.VISIBLE);
        mMealRestaurants.get(i).setVisibility(View.VISIBLE);
        mMealIngredients.get(i).setVisibility(View.VISIBLE);
        mMealDescriptions.get(i).setVisibility(View.VISIBLE);
        mMealImages.get(i).setVisibility(View.VISIBLE);
        mDescriptionBoxes.get(i).setVisibility(View.VISIBLE);
    }

    private void resetViews() {
        for(int i = 0; i<mMealTitles.size(); i++){
            mMealTitles.get(i).setVisibility(View.GONE);
            mMealLocations.get(i).setVisibility(View.GONE);
            mMealRestaurants.get(i).setVisibility(View.GONE);
            mMealIngredients.get(i).setVisibility(View.GONE);
            mMealDescriptions.get(i).setVisibility(View.GONE);
            mMealImages.get(i).setVisibility(View.GONE);
            mDescriptionBoxes.get(i).setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

package com.example.hca127.greenfood.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/*
*   holds a list of ingredients(food) that makes up a diet plan
*   holds the yearly emission of such diet and
*   the date of record of the diet
*/

public class Diet implements Serializable {
    private ArrayList<Food> mBasket;
    private float mTotalUserCo2Emission = 0;
    private Date mDate;

    public Diet(){
        mBasket = new ArrayList<>();
        mDate = new Date();
    }
    
     // instantiates an average diet
    public Diet(boolean b){
        mBasket = new ArrayList<>();
        mDate = new Date();
        addNewIngredient("Beef", 27, 25, 2);
        addNewIngredient("Lamb", 39.2f, 1, 2);
        addNewIngredient("Chicken", 6.9f, 33, 2);
        addNewIngredient("Fish", 11.9f, 9, 2);
        addNewIngredient("Pork", 12.1f, 21, 2);
        addNewIngredient("Eggs", 4.8f, 10, 2);
        addNewIngredient("Veggies", 2, 40, 2);
        addNewIngredient("Bread", 2.7f, 32, 2);
    }

    public String getFoodName(int index){ return mBasket.get(index).getFoodName();}
    public float getIngCarbon(int index){ return mBasket.get(index).getCarbonCoefficient();}
    public float getAvgConsumption(int index){ return mBasket.get(index).getAverageConsumption();}
    public float getUserConsumption(int index){ return mBasket.get(index).getUserConsumption();}
    public float getIngUserCo2Emission(int index){ return mBasket.get(index).getUserCarbonEmission();}
    public int getSize(){ return mBasket.size();}

    public void addNewIngredient(String foodName, float carbonCoefficient, float averageConsumption, float userConsumption){

        boolean duplicate = false;
        for (int i = 0; i < mBasket.size(); i++) {
            if (foodName.equals(mBasket.get(i).getFoodName())) {
                mBasket.get(i).setCarbonCoefficient(carbonCoefficient);
                mBasket.get(i).setAverageConsumption(averageConsumption);
                mBasket.get(i).setUserConsumption(userConsumption);

                duplicate = true;
            }
        }

        if (!duplicate) {
            Food i = new Food(foodName, carbonCoefficient, averageConsumption, userConsumption);
            mBasket.add(i);
        }
        calculateTotalUserCo2Emission();
    }

    public float getUserDietEmission(){
        return mTotalUserCo2Emission;
    }

    public Date getDate(){ return mDate;}

    public float getSuggestedDietEmission(){
        return this.mTotalUserCo2Emission - this.getSuggestedDietSavingAmount();
    }

    public float getSuggestedDietSavingAmount() {
        float emissionIncrease = mBasket.get(getSuggestionMinIndex()).getEmissionIncrease();
        float emissionReduction = mBasket.get(getSuggestionMaxIndex()).getEmissionReduction();
        return emissionReduction - emissionIncrease;
    }

    public void calculateTotalUserCo2Emission(){
        mTotalUserCo2Emission = 0;
        for(int i = 0; i < mBasket.size() ; i++){
            mTotalUserCo2Emission += mBasket.get(i).getUserCarbonEmission();
        }
    }

    public int getSuggestionMinIndex() {
        int minIndex = 0;
        float minValue = mBasket.get(0).getCarbonCoefficient();
        float temp;
        for(int i = 0; i < mBasket.size() ; i++){
            if(mBasket.get(i).getUserConsumption() < 1.45f) {
                temp = mBasket.get(i).getCarbonCoefficient();
                if (temp < minValue) {
                    minValue = temp;
                    minIndex = i;
                }
            }
        }

        return minIndex;
    }

    public int getSuggestionMaxIndex() {
        int maxIndex = 0;
        float maxValue = mBasket.get(0).getUserCarbonEmission();

        float temp;
        for(int i = 1; i < mBasket.size() ; i++){
            temp = mBasket.get(i).getUserCarbonEmission();
            if (maxValue < temp) {
                maxValue = temp;
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public float getCarbonFromSpecificFood(int index) {
        return mBasket.get(index).getUserCarbonEmission();
    }
}

package com.example.hca127.greenfood.objects;

import java.io.Serializable;

/*
*   Food ingredient class
*   holds:
*   ingredient names
*   carbon emission coefficient
*   average consumption in canada
*   user's consumption
*   generated suggestion amounts
*   total emission by user's consumption of this ingredient
*/

public class Food implements Serializable {
    private String mFoodName;
    private float mCarbonCoefficient;
    private float mAverageConsumption;
    private float mUserConsumption;
    private float mLoweredConsumption;
    private float mIncreasedConsumption;
    private float mUserCarbonEmission;

    public Food(String foodName, float mCarbonCoefficient, float mAverageConsumption, float mUserConsumption){
        this.mFoodName = foodName;
        this.mCarbonCoefficient = mCarbonCoefficient;
        this.mAverageConsumption = mAverageConsumption;
        setUserConsumption(mUserConsumption);
        this.mUserCarbonEmission = calculateUserCarbonEmission();
    }

    public float getUserCarbonEmission(){
        return (mCarbonCoefficient)*(mAverageConsumption)*(mUserConsumption);
    }

    private float calculateUserCarbonEmission(){
        return (mCarbonCoefficient)*(mAverageConsumption)*(mUserConsumption);
    }

    public float getEmissionIncrease() {
        return (mCarbonCoefficient)*(mAverageConsumption)*(mIncreasedConsumption)-this.mUserCarbonEmission;
    }

    public float getEmissionReduction() {
        return this.mUserCarbonEmission-(mCarbonCoefficient)*(mAverageConsumption)*(mLoweredConsumption);
    }

    public String getFoodName() {
        return mFoodName;
    }

    public void setFoodName(String foodName) {
        this.mFoodName = foodName;
    }

    public float getCarbonCoefficient() {
        return mCarbonCoefficient;
    }

    public void setCarbonCoefficient(float carbonCoefficient) {
        this.mCarbonCoefficient = carbonCoefficient;
        this.mUserCarbonEmission = calculateUserCarbonEmission();
    }

    public float getAverageConsumption() {
        return mAverageConsumption;
    }

    public void setAverageConsumption(float averageConsumption) {
        this.mAverageConsumption = averageConsumption;
        this.mUserCarbonEmission = calculateUserCarbonEmission();
    }

    public float getUserConsumption() {
        return mUserConsumption;
    }

    public void setUserConsumption(float userConsumption) {

        if (userConsumption == 1) {
            this.mUserConsumption = 1.5f;
            this.mIncreasedConsumption = 1.8f;
            this.mLoweredConsumption = 1.0f;
        } else if (userConsumption == 2) {
            this.mUserConsumption = 1f;
            this.mIncreasedConsumption = 1.5f;
            this.mLoweredConsumption = 0.5f;
        } else if (userConsumption == 3) {
            this.mUserConsumption = 0.5f;
            this.mIncreasedConsumption = 1f;
            this.mLoweredConsumption = 0f;
        } else {
            this.mUserConsumption = 0;
            this.mIncreasedConsumption = 0.5f;
            this.mLoweredConsumption = 0f;
        }
        this.mUserCarbonEmission = calculateUserCarbonEmission();
    }

}

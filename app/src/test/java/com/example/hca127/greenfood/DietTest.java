package com.example.hca127.greenfood;

import com.example.hca127.greenfood.objects.Diet;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DietTest {

    private Diet testDiet = new Diet();

    @Before
    public void setup() {
        float totalUserCarbonEmission = 0;
        testDiet.addNewIngredient("testFood", 20, 20, 2);
        for (int i = 0; i < testDiet.getSize(); i++) {
            totalUserCarbonEmission += testDiet.getIngUserCo2Emission(i);
        }
    }

    @Test
    public void getIngName() {
        assertEquals("testFood", testDiet.getFoodName(0));
    }

    @Test
    public void getIngCarbon() {
        assertEquals(20, testDiet.getIngCarbon(0), 0.001);
    }

    @Test
    public void getUserConsumption() {
        assertEquals(1, testDiet.getUserConsumption(0), 0.001);
    }

    @Test
    public void getAveConsumption() {
        assertEquals(20, testDiet.getAvgConsumption(0), 0.001);
    }

    @Test
    public void getIngUserCo2Emission() {
        testDiet.calculateTotalUserCo2Emission();
        assertEquals(400, testDiet.getUserDietEmission(), 0.001);
    }

    @Test
    public void addNewIngredient() {
        testDiet.addNewIngredient("testFood2", 20, 20, 2);
        assertEquals("testFood2", testDiet.getFoodName(1));
    }

    @Test
    public void get_total_user_co2_emission() {
        testDiet.calculateTotalUserCo2Emission();
        float testFloat = testDiet.getUserDietEmission();
        assertEquals(400, testFloat, 0.001);
    }

    @Test
    public void calculate_total_user_co2_emission() {
        testDiet.addNewIngredient("anotherTestFood", 30, 30, 1);
        float testFloat = 0;
        for (int i = 0; i < testDiet.getSize(); i++) {
            testFloat += testDiet.getIngCarbon(i) *
                    testDiet.getUserConsumption(i) *
                    testDiet.getAvgConsumption(i);
        }

        testDiet.calculateTotalUserCo2Emission();
        assertEquals(testFloat, testDiet.getUserDietEmission(), 0.001);
    }

    @Test
    public void getSuggestionMinIndex()
    {
        testDiet = new Diet();
        testDiet.addNewIngredient("one", 123, 30, 2);
        testDiet.addNewIngredient("two", 17, 30, 2);
        testDiet.addNewIngredient("three", 30, 30, 2);
        assertEquals(1, testDiet.getSuggestionMinIndex());
        testDiet = new Diet();
        testDiet.addNewIngredient("one", 10, 10, 3);
        testDiet.addNewIngredient("two", 20, 10, 2);
        testDiet.addNewIngredient("three", 30, 10, 1);
        assertEquals(0, testDiet.getSuggestionMinIndex());
    }

    @Test
    public void getSuggestionMaxIndex(){
        testDiet = new Diet();
        testDiet.addNewIngredient("one", 20, 30, 2);
        testDiet.addNewIngredient("two", 40, 30, 2);
        testDiet.addNewIngredient("three", 30, 30, 2);
        int max = testDiet.getSuggestionMaxIndex();
        assertEquals(1,max);

        testDiet = new Diet();
        testDiet.addNewIngredient("one", 10, 10, 3);
        assertEquals(50f, testDiet.getUserDietEmission(), 0.001);

        testDiet = new Diet();
        testDiet.addNewIngredient("one", 20, 10, 2);
        assertEquals(200f, testDiet.getUserDietEmission(), 0.001) ;

        testDiet = new Diet();
        testDiet.addNewIngredient("one", 30, 10, 1);
        assertEquals(450f, testDiet.getUserDietEmission(), 0.001) ;

        testDiet = new Diet();
        testDiet.addNewIngredient("one", 10, 10, 3);
        testDiet.addNewIngredient("two", 20, 10, 2);
        testDiet.addNewIngredient("three", 30, 10, 1);

        assertEquals(50f, testDiet.getCarbonFromSpecificFood(0), 0.001) ;
        assertEquals(200f, testDiet.getCarbonFromSpecificFood(1), 0.001) ;
        assertEquals(450f, testDiet.getCarbonFromSpecificFood(2), 0.001) ;

        assertEquals(700f, testDiet.getUserDietEmission(), 0.001) ;
        assertEquals(2,testDiet.getSuggestionMaxIndex());
    }

    @Test
    public void getSuggestedDietEmissionTest() {
        testDiet = new Diet();
        testDiet.addNewIngredient("one", 10, 10, 3);
        testDiet.addNewIngredient("two", 20, 10, 2);
        testDiet.addNewIngredient("three", 30, 10, 1);
        assertEquals(700f, testDiet.getUserDietEmission(), 0.001);
        assertEquals(100f, testDiet.getSuggestedDietSavingAmount(), 0.001);
        assertEquals(600f, testDiet.getSuggestedDietEmission(), 0.001);
    }

    @Test
    public void getDate(){
        Date date = new Date();
        assertEquals(date.getMinutes(), testDiet.getDate().getMinutes());
    }

    @Test
    public void duplicateCheck() {
        testDiet = new Diet();
        testDiet.addNewIngredient("one", 10, 10, 2);
        testDiet.addNewIngredient("two", 20, 10, 2);
        testDiet.addNewIngredient("two", 20, 10, 2);
        assertEquals(300f, testDiet.getUserDietEmission(), 0.001);
        testDiet = new Diet();
        testDiet.addNewIngredient("one", 10, 10, 2);
        testDiet.addNewIngredient("two", 20, 10, 2);
        testDiet.addNewIngredient("two", 30, 10, 2);
        assertEquals(400f, testDiet.getUserDietEmission(), 0.001);
    }


    @Test
    public void defaultDiet() {
        testDiet = new Diet(true);
        assertEquals(1517.5f, testDiet.getUserDietEmission(), 0.001);
    }


}

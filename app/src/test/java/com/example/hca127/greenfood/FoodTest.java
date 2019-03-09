package com.example.hca127.greenfood;

import com.example.hca127.greenfood.objects.Food;

import org.junit.Test;

import static org.junit.Assert.*;

public class FoodTest {

    Food test = new Food("beef", 20, 20,2);

    @Test
    public void foodTest(){
        Food testing = new Food("lamb",20,20,2);
        assertEquals("lamb", testing.getFoodName());
        assertEquals(20, testing.getCarbonCoefficient(),0.001);
        assertEquals(20, testing.getAverageConsumption(), 0.001);
        assertEquals(1.0, testing.getUserConsumption(),0.001);
    }

    @Test
    public void getUserCarbonEmissionTest() {
        double carbon_test_double = test.getCarbonCoefficient();
        double avg_consumption_test_double = test.getAverageConsumption();
        double user_consumption_test_double = test.getUserConsumption();
        assertEquals(carbon_test_double*avg_consumption_test_double*user_consumption_test_double, test.getUserCarbonEmission(),0.001);
    }

    @Test
    public void userCarbonTestTwo() {

        assertEquals(400.0, test.getUserCarbonEmission(),0.001);
    }

    @Test
    public void getFoodNameTest() {
        assertEquals("beef",test.getFoodName());
    }

    @Test
    public void setFoodNameTest() {
        test.setFoodName("lamb");
        assertEquals("lamb",test.getFoodName());
    }

    @Test
    public void getCarbonCoefficientTest() {
        assertEquals(20,test.getCarbonCoefficient(), 0.001);
    }

    @Test
    public void setCarbon_coefficient() {
        test.setCarbonCoefficient(50);
        assertEquals(50, test.getCarbonCoefficient(), 0.001);
    }

    @Test
    public void getAverage_consumption() {
        assertEquals(20, test.getAverageConsumption(), 0.001);
    }

    @Test
    public void setAverage_consumption() {
        test.setAverageConsumption(50);
        assertNotEquals(20, test.getUserConsumption(), 0.001);
        assertEquals(50, test.getAverageConsumption(), 0.001);
    }

    @Test
    public void getUser_consumption() {
        assertEquals(1, test.getUserConsumption(), 0.001);
    }

    @Test
    public void setUser_consumption() {
        test.setUserConsumption(1);
        assertEquals(1.5, test.getUserConsumption(),0.001);
        test.setUserConsumption(2);
        assertEquals(1, test.getUserConsumption(),0.001);
        test.setUserConsumption(3);
        assertEquals(0.5, test.getUserConsumption(),0.001);
        test.setUserConsumption(4);
        assertEquals(0, test.getUserConsumption(),0.001);

    }

    @Test
    public void getLoweredEmissionTest() {
        test = new Food("basketCase", 20, 20,1);
        assertEquals(600, test.getUserCarbonEmission(), 0.001);
        assertEquals(200, test.getEmissionReduction(), 0.001);

        test.setUserConsumption(2);
        assertEquals(400, test.getUserCarbonEmission(), 0.001);
        assertEquals(200, test.getEmissionReduction(), 0.001);

        test.setUserConsumption(3);
        assertEquals(200, test.getUserCarbonEmission(), 0.001);
        assertEquals(200, test.getEmissionReduction(), 0.001);

        test.setUserConsumption(4);
        assertEquals(0, test.getUserCarbonEmission(), 0.001);
        assertEquals(0, test.getEmissionReduction(), 0.001);

    }

    @Test
    public void getIncreasedEmissionTest() {
        test = new Food("basketCase", 20, 20,4);
        assertEquals(0, test.getUserCarbonEmission(), 0.001);
        assertEquals(200, test.getEmissionIncrease(), 0.001);

        test.setUserConsumption(3);
        assertEquals(200, test.getUserCarbonEmission(), 0.001);
        assertEquals(200, test.getEmissionIncrease(), 0.001);

        test.setUserConsumption(2);
        assertEquals(400, test.getUserCarbonEmission(), 0.001);
        assertEquals(200, test.getEmissionIncrease(), 0.001);

        test.setUserConsumption(1);
        assertEquals(600, test.getUserCarbonEmission(), 0.001);
        assertEquals(120, test.getEmissionIncrease(), 0.001);

    }


}
package com.example.hca127.greenfood;

import com.example.hca127.greenfood.objects.Diet;
import com.example.hca127.greenfood.objects.LocalUser;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class LocalUserTest {
    private LocalUser testUser;
    private Diet testDiet = new Diet();
    private Diet testDiet2 = new Diet();
    ArrayList<Diet> testLocalUserDiet;

    @Before
    public void setUp()
    {
        testUser = new LocalUser();
        testUser.setUserId("123");
        testUser.setCity(0);
        testUser.setName("patrick");
        testUser.setPledge(10.23);
        testUser.setUserEmail("patrick@gmail.com");
        testDiet.addNewIngredient("Beef", 20, 20, 2);
        testDiet2.addNewIngredient("Chicken", 20, 20, 2);
        testLocalUserDiet = new ArrayList<Diet>();
        testLocalUserDiet.add(testDiet);
    }

    @Test
    public void getUserId() {
        assertEquals("123", testUser.getUserId());
    }

    @Test
    public void getName() {
        assertEquals("patrick", testUser.getName());
    }

    @Test
    public void getUserEmail() {
        assertEquals("patrick@gmail.com", testUser.getUserEmail());
    }

    @Test
    public void getPledge() {
        assertEquals(10.23, testUser.getPledge(), 0.001);
    }

    @Test
    public void getCity() {
        assertEquals(0, testUser.getCity());
    }

    @Test
    public void getCurrentDiet() {
        assertEquals(testLocalUserDiet.get(0).getFoodName(0),testUser.getCurrentDiet().getFoodName(0));
    }

    @Test
    public void setUserId() {
        testUser.setUserId("testUserID");
        assertEquals("testUserID", testUser.getUserId());

    }

    @Test
    public void setFirstName() {
        testUser.setName("testUserName");
        assertEquals("testUserName", testUser.getName());
    }

    @Test
    public void setUserEmail() {
        testUser.setUserEmail("testUserEmail");
        assertEquals("testUserEmail", testUser.getUserEmail());
    }

    @Test
    public void setPledge() {
        testUser.setPledge(1.1);
        assertEquals(1.1,testUser.getPledge(),0.01);
    }

    @Test
    public void setCity() {
        testUser.setCity(1);
        assertEquals(1, testUser.getCity());
    }

    @Test
    public void userIcon() {
        testUser.setProfileIcon(2);
        assertEquals(2, testUser.getProfileIcon());
        testUser.setProfileIcon(3);
        assertEquals(3, testUser.getProfileIcon());
    }

    @Test
    public void setCurrentDiet() {
        Diet testDietFor = new Diet(true);
        testUser.setCurrentDiet(testDietFor);
        assertEquals(1517.5f, testUser.getCurrentDiet().getUserDietEmission(), 0.001f);
    }

    @Test
    public void testPledgeByIndex() {
        testUser.setPledgeByIndex(1);
        assertEquals(168.611f, testUser.getPledge(), 0.001f);

    }
}
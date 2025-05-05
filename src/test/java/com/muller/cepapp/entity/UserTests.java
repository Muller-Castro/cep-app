package com.muller.cepapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.muller.cepapp.TestData;

public class UserTests {

    @Test
    @DisplayName("User constructor should work")
    void createUserInstance() {
        User user = new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, TestData.ROLE);
        assertNotNull(user);
        assertEquals(TestData.NAME, user.getName());
        assertEquals(TestData.EMAIL, user.getEmail());
        assertEquals(TestData.PASSWORD, user.getPassword());
    }
    
}

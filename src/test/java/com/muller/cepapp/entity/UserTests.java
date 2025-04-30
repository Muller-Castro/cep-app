package com.muller.cepapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTests {

    private static final String NAME     = "User Name";
    private static final String EMAIL    = "user.name@example.com";
    private static final String PASSWORD = "password123";

    @Test
    @DisplayName("User constructor should work")
    void createUserInstance() {
        User user = new User(NAME, EMAIL, PASSWORD);
        assertNotNull(user);
        assertEquals(NAME, user.getName());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(PASSWORD, user.getPassword());
    }
    
}

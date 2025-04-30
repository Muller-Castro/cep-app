package com.muller.cepapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AddressTests {

    private static final String NAME     = "User Name";
    private static final String EMAIL    = "user.name@example.com";
    private static final String PASSWORD = "password123";

    private static final String STREET       = "STREET";
    private static final String NUMBER       = "123";
    private static final String COMPLEMENT   = "COMPLEMENT";
    private static final String NEIGHBORHOOD = "NEIGHBORHOOD";
    private static final String CITY         = "CITY";
    private static final String STATE        = "SP";
    private static final String ZIP_CODE     = "12345678";

    @Test
    @DisplayName("Address constructor should work")
    void createAddressInstance() {
        User user = new User(NAME, EMAIL, PASSWORD);
        assertNotNull(user);
        Address address = new Address(STREET, NUMBER, COMPLEMENT, NEIGHBORHOOD, CITY, STATE, ZIP_CODE, user);
        assertNotNull(address);
        assertEquals(STREET, address.getStreet());
        assertEquals(NUMBER, address.getNumber());
        assertEquals(COMPLEMENT, address.getComplement());
        assertEquals(NEIGHBORHOOD, address.getNeighborhood());
        assertEquals(CITY, address.getCity());
        assertEquals(STATE, address.getState());
        assertEquals(ZIP_CODE, address.getZipCode());
        assertEquals(user, address.getUser());
    }
    
}

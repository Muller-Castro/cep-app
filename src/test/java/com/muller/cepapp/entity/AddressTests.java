package com.muller.cepapp.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.muller.cepapp.TestData;

public class AddressTests {

    @Test
    @DisplayName("Address constructor should work")
    void createAddressInstance() {
        User user = new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, TestData.ROLE);
        assertNotNull(user);
        Address address = new Address(TestData.STREET, TestData.NUMBER, TestData.COMPLEMENT, TestData.NEIGHBORHOOD, TestData.CITY, TestData.STATE, TestData.ZIP_CODE, user);
        assertNotNull(address);
        assertEquals(TestData.STREET, address.getStreet());
        assertEquals(TestData.NUMBER, address.getNumber());
        assertEquals(TestData.COMPLEMENT, address.getComplement());
        assertEquals(TestData.NEIGHBORHOOD, address.getNeighborhood());
        assertEquals(TestData.CITY, address.getCity());
        assertEquals(TestData.STATE, address.getState());
        assertEquals(TestData.ZIP_CODE, address.getZipCode());
        assertEquals(user, address.getUser());
    }
    
}

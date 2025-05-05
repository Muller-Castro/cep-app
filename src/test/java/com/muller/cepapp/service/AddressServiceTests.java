package com.muller.cepapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.muller.cepapp.TestData;
import com.muller.cepapp.entity.Address;
import com.muller.cepapp.entity.User;
import com.muller.cepapp.exception.AddressNotFoundException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback(true)
public class AddressServiceTests {

    @Autowired
    private AddressService addressService;

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    private User user;
    
    @BeforeEach
    void setup() {
        user = userService.createUser(new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, TestData.ROLE));

        addressService.createAddress(new Address(TestData.STREET, TestData.NUMBER, TestData.COMPLEMENT, TestData.NEIGHBORHOOD, TestData.CITY, TestData.STATE, TestData.ZIP_CODE, user), user.getId());
    }

    @Test
    @DisplayName("Should throw \"AddressNotFoundException\" if updating non existent address")
    void shouldFailUpdateAddressNotFound() throws Exception {
        long nonExistentAddressId = -1L;

        Address updatedAddress = new Address("Updated Street", "123", "Updated Complement", "Updated Neighborhood", "Updated City", "Updated State", "01001000", user);

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () -> {
            addressService.updateAddress(nonExistentAddressId, updatedAddress);
        });

        assertEquals(String.format(AddressService.ADDRESS_NOT_FOUND_MESSAGE, String.valueOf(nonExistentAddressId)), exception.getMessage());
    }
    
}

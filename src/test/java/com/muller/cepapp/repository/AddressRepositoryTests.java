package com.muller.cepapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.muller.cepapp.TestData;
import com.muller.cepapp.entity.Address;
import com.muller.cepapp.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AddressRepositoryTests {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save address and retrieve by ID")
    void saveAddressAndRetrieveById() {
        User user = new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD);
        User savedUser = userRepository.save(user);
        Address address = new Address(TestData.STREET, TestData.NUMBER, TestData.COMPLEMENT, TestData.NEIGHBORHOOD, TestData.CITY, TestData.STATE, TestData.ZIP_CODE, savedUser);
        Address savedAddress = addressRepository.save(address);
        Optional<Address> foundAddress = addressRepository.findById(savedAddress.getId());
        assertTrue(foundAddress.isPresent());
        assertEquals(TestData.STREET, foundAddress.get().getStreet());
        assertEquals(savedUser.getId(), foundAddress.get().getUser().getId());
    }

    @Test
    @DisplayName("Should find addresses by user")
    void findAddressesByUser() {
        User user = new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD);
        User savedUser = userRepository.save(user);
        addressRepository.saveAll(List.of(
                new Address("STREET_A", "1", null, "NEIGHBORHOOD_A", "CITY_A", "SP", "11111111", savedUser),
                new Address("STREET_B", "2", null, "NEIGHBORHOOD_B", "CITY_B", "GO", "22222222", savedUser)
        ));
        List<Address> foundAddresses = addressRepository.findByUser(savedUser);
        assertEquals(2, foundAddresses.size());
        assertTrue(foundAddresses.stream().anyMatch(a -> a.getStreet().equals("STREET_A")));
        assertTrue(foundAddresses.stream().anyMatch(a -> a.getStreet().equals("STREET_B")));
    }

    @Test
    @DisplayName("Should find addresses by user with pagination")
    void findAddressesByUserWithPagination() {
        User user = new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD);
        User savedUser = userRepository.save(user);
        addressRepository.saveAll(List.of(
                new Address("STREET_A", "1", null, "NEIGHBORHOOD_A", "CITY_A", "SP", "11111111", savedUser),
                new Address("STREET_B", "2", null, "NEIGHBORHOOD_B", "CITY_B", "GO", "22222222", savedUser),
                new Address("STREET_C", "3", null, "NEIGHBORHOOD_C", "CITY_C", "RJ", "33333333", savedUser)
        ));
        Page<Address> page = addressRepository.findByUser(savedUser, PageRequest.of(0, 2));
        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalElements());
    }
    
}

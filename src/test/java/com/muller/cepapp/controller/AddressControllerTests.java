package com.muller.cepapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.AbstractMap.SimpleEntry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muller.cepapp.TestData;
import com.muller.cepapp.TestMethods;
import com.muller.cepapp.entity.Address;
import com.muller.cepapp.entity.User;
import com.muller.cepapp.service.AddressService;
import com.muller.cepapp.service.JwtService;
import com.muller.cepapp.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AddressControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AddressService addressService;

    private User testUser;

    private String jwtToken;

    private Address testAddress;

    @BeforeEach
    void setup() {
        testUser = userService.createUser(new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, User.Role.ROLE_ADMIN));

        jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(TestData.EMAIL));

        testAddress = addressService.createAddress(new Address(TestData.STREET, TestData.NUMBER, TestData.COMPLEMENT, TestData.NEIGHBORHOOD, TestData.CITY, TestData.STATE, TestData.ZIP_CODE, testUser));
    }

    @Test
    @DisplayName("Should get all addresses")
    void shouldGetAllAddresses() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/addresses")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].street").value(testAddress.getStreet()));
    }

    @Test
    @DisplayName("Should get address by ID")
    void shouldGetAddressById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/addresses/" + testAddress.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.street").value(testAddress.getStreet()));
    }

    @Test
    @DisplayName("Should return NOT FOUND when getting address by non existent ID")
    void shouldReturnNotFoundForNonExistentId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/addresses/-1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create a new address")
    void shouldCreateAddress() throws Exception {
        Address newAddress = new Address("New Street", "456", "Apt 10", "New Neighborhood", "New City", "SP", "54321876", testUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddress))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.street").value("New Street"));
    }

    @Test
    @DisplayName("Should update an existing address")
    void shouldUpdateAddress() throws Exception {
        Address updatedAddress = new Address("Updated Street", "789", "Suite 20", "Updated Neighborhood", "Updated City", "RJ", "98765432", testUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/addresses/" + testAddress.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAddress))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.street").value("Updated Street"));
    }

    @Test
    @DisplayName("Should return NOT FOUND when updating a non existent address")
    void shouldReturnNotFoundWhenUpdatingNonExistentAddress() throws Exception {
        Address updatedAddress = new Address("Updated Street", "789", "Suite 20", "Updated Neighborhood", "Updated City", "RJ", "98765432", testUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/addresses/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAddress))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete an address")
    void shouldDeleteAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/addresses/" + testAddress.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/addresses/" + testAddress.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Admin should be able to get all addresses")
    void adminShouldGetAllAddresses() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/addresses")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Regular user should NOT be able to get all addresses")
    void regularUserShouldNotBeAbleToGetAllAddresses() throws Exception {
        SimpleEntry<User, String> regularUserAndToken = TestMethods.createRegularUser(userService, userDetailsService, jwtService);

        mockMvc.perform(MockMvcRequestBuilders.get("/addresses")
                .header("Authorization", "Bearer " + regularUserAndToken.getValue()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin should be able to get address by ID")
    void adminShouldGetAddressById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/addresses/" + testAddress.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Regular user should be able to get address by ID")
    void regularUserShouldGetAddressById() throws Exception {
        SimpleEntry<User, String> regularUserAndToken = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        User regularUser = regularUserAndToken.getKey();
        String regularUserToken = regularUserAndToken.getValue();
        Address addressForRegularUser = addressService.createAddress(new Address("Regular User Street", "123", "Apt 5", "Some Neighborhood", "Some City", "SP", "12345678", regularUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/addresses/" + addressForRegularUser.getId())
                .header("Authorization", "Bearer " + regularUserToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin should be able to create a new address")
    void adminShouldCreateAddress() throws Exception {
        Address newAddress = new Address("New Street", "456", "Apt 10", "New Neighborhood", "New City", "SP", "54321876", testUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddress))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Regular user should be able to create a new address")
    void regularUserShouldBeAbleToCreateAddress() throws Exception {
        SimpleEntry<User, String> regularUserAndToken = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        User regularUser = regularUserAndToken.getKey();
        String regularUserToken = regularUserAndToken.getValue();
        Address newAddress = new Address("New Street", "456", "Apt 10", "New Neighborhood", "New City", "SP", "54321876", regularUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddress))
                .header("Authorization", "Bearer " + regularUserToken))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.street").value("New Street"));
    }

    @Test
    @DisplayName("Admin should be able to update an existing address")
    void adminShouldBeAbleToUpdateAddress() throws Exception {
        Address updatedAddress = new Address("Updated Street", "789", "Suite 20", "Updated Neighborhood", "Updated City", "SP", "98765432", testUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/addresses/" + testAddress.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAddress))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Regular user should be able to update an existing address")
    void regularUserShouldBeAbleToUpdateAddress() throws Exception {
        SimpleEntry<User, String> regularUserAndToken = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        User regularUser = regularUserAndToken.getKey();
        String regularUserToken = regularUserAndToken.getValue();
        Address updatedAddress = new Address("Updated Street", "789", "Suite 20", "Updated Neighborhood", "Updated City", "SP", "98765432", regularUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/addresses/" + testAddress.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAddress))
                .header("Authorization", "Bearer " + regularUserToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin should be able to delete an address")
    void adminShouldBeAbleToDeleteAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/addresses/" + testAddress.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Regular user should NOT be able to delete an address")
    void regularUserShouldNotBeAbleToDeleteAddress() throws Exception {
        SimpleEntry<User, String> regularUserAndToken = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        String regularUserToken = regularUserAndToken.getValue();
        mockMvc.perform(MockMvcRequestBuilders.delete("/addresses/" + testAddress.getId())
                .header("Authorization", "Bearer " + regularUserToken))
                .andExpect(status().isForbidden());
    }
    
}

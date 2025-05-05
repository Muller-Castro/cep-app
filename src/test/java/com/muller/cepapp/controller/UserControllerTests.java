package com.muller.cepapp.controller;

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
import com.muller.cepapp.entity.User;
import com.muller.cepapp.service.JwtService;
import com.muller.cepapp.service.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.AbstractMap.SimpleEntry;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerTests {

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

    private User testUser;

    private String jwtToken;

    @BeforeEach
    void setup() {
        testUser = userService.createUser(new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, User.Role.ROLE_ADMIN));

        jwtToken = jwtService.generateToken(userDetailsService.loadUserByUsername(TestData.EMAIL));
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + testUser.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(testUser.getName()));
    }

    @Test
    @DisplayName("Should return NOT FOUND when getting user by non existent ID")
    void shouldReturnNotFoundForNonExistentId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/-1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get user by email")
    void shouldGetUserByEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/email?email=" + testUser.getEmail())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(testUser.getName()));
    }

    @Test
    @DisplayName("Should return NOT FOUND when getting user by non existent email")
    void shouldReturnNotFoundForNonExistentEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/email?email=nonexistent@example.com")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create a new user")
    void shouldCreateUser() throws Exception {
        User newUser = new User("New User", "newuser@example.com", "password123", User.Role.ROLE_USER);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New User"));
    }

    @Test
    @DisplayName("Should update an existing user")
    void shouldUpdateUser() throws Exception {
        User updatedUser = new User("Updated User", "updateduser@example.com", "newpassword123", User.Role.ROLE_USER);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    @DisplayName("Should return NOT FOUND when updating a non existent user")
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        User updatedUser = new User("Updated User", "updateduser@example.com", "newpassword123", User.Role.ROLE_USER);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete a user")
    void shouldDeleteUser() throws Exception {
        SimpleEntry<User, String> pair = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        User newUser = pair.getKey();
        String token = pair.getValue();

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + newUser.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + newUser.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should allow admin to get all users")
    void shouldAllowAdminToGetAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow admin to get user by ID")
    void shouldAllowAdminToGetUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + testUser.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow admin to get user by email")
    void shouldAllowAdminToGetUserByEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/email?email=" + testUser.getEmail())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow admin to create a user")
    void shouldAllowAdminToCreateUser() throws Exception {
        User newUser = new User("New User", "newuser@example.com", "password123", User.Role.ROLE_USER);
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should allow admin to update a user")
    void shouldAllowAdminToUpdateUser() throws Exception {
        User updatedUser = new User("Updated User", "updateduser@example.com", "newpassword123", User.Role.ROLE_USER);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser))
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow admin to delete a user")
    void shouldAllowAdminToDeleteUser() throws Exception {
        SimpleEntry<User, String> pair = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        User userToDelete = pair.getKey();
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + userToDelete.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should not allow regular user to get all users")
    void shouldNotAllowRegularUserToGetAllUsers() throws Exception {
        SimpleEntry<User, String> regularUser = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .header("Authorization", "Bearer " + regularUser.getValue()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow regular user to get their own user data")
    void shouldAllowRegularUserToGetOwnData() throws Exception {
        SimpleEntry<User, String> regularUser = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        User user = regularUser.getKey();
        String token = regularUser.getValue();
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + user.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should not allow regular user to get another user's data")
    void shouldNotAllowRegularUserToGetOtherUserData() throws Exception {
        SimpleEntry<User, String> regularUser = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/" + testUser.getId())
                .header("Authorization", "Bearer " + regularUser.getValue()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow regular user to update their own user data")
    void shouldAllowRegularUserToUpdateOwnData() throws Exception{
        SimpleEntry<User, String> regularUserPair = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        User regularUser = regularUserPair.getKey();
        String token = regularUserPair.getValue();
        User updatedUser = new User("Updated Regular User", "updatedregularuser@example.com", "newpassword123", User.Role.ROLE_USER);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + regularUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should not allow regular user to update other user's data")
    void shouldNotAllowRegularUserToUpdateOtherUserData() throws Exception {
        SimpleEntry<User, String> regularUser = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        User updatedUser = new User("Updated User", "updateduser@example.com", "newpassword123", User.Role.ROLE_USER);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser))
                .header("Authorization", "Bearer " + regularUser.getValue()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow regular user to delete their own account")
    void shouldAllowRegularUserToDeleteOwnAccount() throws Exception {
        SimpleEntry<User, String> regularUser = TestMethods.createRegularUser(userService, userDetailsService, jwtService);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + regularUser.getKey().getId())
                .header("Authorization", "Bearer " + regularUser.getValue()))
                .andExpect(status().isNoContent());
    }
    
}

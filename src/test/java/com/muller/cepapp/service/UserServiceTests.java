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
import com.muller.cepapp.entity.User;
import com.muller.cepapp.exception.UserNotFoundException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback(true)
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        userService.createUser(new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, TestData.ROLE));
    }

    @Test
    @DisplayName("Should throw \"UserNotFoundException\" if updating non existent user")
    void shouldFailUpdateUserNotFound() throws Exception {
        long nonExistentUserId = -1L;
        User updatedUser = new User("Updated Name", "updated@example.com", "newPassword", TestData.ROLE);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(nonExistentUserId, updatedUser);
        });

        assertEquals(String.format(UserService.USER_NOT_FOUND_MESSAGE, String.valueOf(nonExistentUserId)), exception.getMessage());
    }
    
}

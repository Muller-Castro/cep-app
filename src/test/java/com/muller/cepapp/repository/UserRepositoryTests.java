package com.muller.cepapp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.data.domain.Sort;

import com.muller.cepapp.TestData;
import com.muller.cepapp.entity.User;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTests {
    
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save user and retrieve by ID")
    void saveUserAndRetrieveById() {
        User user = new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, TestData.ROLE);
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(user.getName(), foundUser.get().getName());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
        assertEquals(user.getPassword(), foundUser.get().getPassword());
    }

    @Test
    @DisplayName("Should find user by email")
    void findUserByEmail() {
        User user = new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, TestData.ROLE);
        userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());
        assertTrue(foundUser.isPresent());
        assertEquals(user.getName(), foundUser.get().getName());
    }

    @Test
    @DisplayName("Should fail if user email is null")
    void saveUserWithNullEmailShouldFailDatabaseConstraint() {
        User invalidUser = new User("Invalid User", null, "password", TestData.ROLE);
        assertThrows(ConstraintViolationException.class, () -> userRepository.save(invalidUser));
    }

    @Test
    @DisplayName("Should find all users with pagination")
    void findAllUsersWithPagination() {
        User user = new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, TestData.ROLE);
        userRepository.saveAll(List.of(
                user,
                new User("User B", "b@example.com", "password", TestData.ROLE),
                new User("User C", "c@example.com", "password", TestData.ROLE)
        ));
        Page<User> page = userRepository.findAll(PageRequest.of(0, 2));
        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalElements());
    }

    @Test
    @DisplayName("Should find all users with ascending name order")
    void findAllUsersSortedByNameAscending() {
        User user = new User(TestData.NAME, TestData.EMAIL, TestData.PASSWORD, TestData.ROLE);
        userRepository.saveAll(List.of(
                new User("John Doe", "john.doe@example.com", "password", TestData.ROLE),
                user,
                new User("Jane Doe", "jane.doe@example.com", "password", TestData.ROLE)
        ));
        List<User> users = userRepository.findAll(Sort.by("name").ascending());
        assertEquals("Jane Doe", users.get(0).getName());
        assertEquals("John Doe", users.get(1).getName());
        assertEquals("User Name", users.get(2).getName());
    }

}

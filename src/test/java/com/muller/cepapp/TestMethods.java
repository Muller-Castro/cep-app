package com.muller.cepapp;

import java.util.AbstractMap.SimpleEntry;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.muller.cepapp.entity.User;
import com.muller.cepapp.service.JwtService;
import com.muller.cepapp.service.UserService;

public class TestMethods {

    public static SimpleEntry<User, String> createRegularUser(UserService userService, UserDetailsService userDetailsService, JwtService jwtService) {
        User regularUser = new User("Regular User", "regularuser@example.com", "password123", User.Role.ROLE_USER);
        userService.createUser(regularUser);
        UserDetails regularUserDetails = userDetailsService.loadUserByUsername(regularUser.getEmail());
        String regularUserToken = jwtService.generateToken(regularUserDetails);

        return new SimpleEntry<>(regularUser, regularUserToken);
    }
    
}

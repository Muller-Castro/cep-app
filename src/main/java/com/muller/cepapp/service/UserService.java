package com.muller.cepapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.muller.cepapp.entity.User;
import com.muller.cepapp.exception.UserNotFoundException;
import com.muller.cepapp.repository.UserRepository;

@Service
public class UserService {

    public static final String USER_NOT_FOUND_MESSAGE = "User with ID '%s' not found.";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        user.setPassword(encryptPassword(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<User> getAllUsers(Sort sort) {
        return userRepository.findAll(sort);
    }

    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            existingUser.setName(updatedUser.getName());
            existingUser.setEmail(updatedUser.getEmail());
            
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(encryptPassword(updatedUser.getPassword()));
            }           
            return userRepository.save(existingUser);
        }
        
        throw new UserNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, id.toString()));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
    
}

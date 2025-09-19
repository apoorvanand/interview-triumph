package com.ecommerce.services;

import com.ecommerce.models.User;
import com.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // Logic for registering a new user
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String password) {
        // Logic for user login
        return userRepository.findByEmailAndPassword(email, password);
    }

    public Optional<User> updateUserProfile(Long userId, User updatedUser) {
        // Logic for updating user profile
        return userRepository.findById(userId).map(user -> {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            // Update other fields as necessary
            return userRepository.save(user);
        });
    }

    public Optional<User> getUserById(Long userId) {
        // Logic for retrieving a user by ID
        return userRepository.findById(userId);
    }
}
package com.backend.phi.service;

import com.backend.phi.entity.User;
import com.backend.phi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create or update user
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Get user by email (if you have this method in repository)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Check if email exists (if defined in repository)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Delete user by ID
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
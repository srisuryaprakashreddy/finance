package com.project.finance.service;

import com.project.finance.model.User;
import com.project.finance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user, String rawPin) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPin(passwordEncoder.encode(rawPin));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkPin(User user, String rawPin) {
        return passwordEncoder.matches(rawPin, user.getPin());
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    // Update PIN (hashed)
    public void updatePin(User user, String newPin) {
        String encodedPin = passwordEncoder.encode(newPin);
        user.setPin(encodedPin);
        userRepository.save(user);
    }

    // Update password (hashed)
    public void updatePassword(User user, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    // Check if the provided password matches the user's password
    public boolean checkPassword(User user, String currentPassword) {
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    // Check if the provided pin matches the user's pin


}

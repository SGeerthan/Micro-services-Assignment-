package com.stationery.auth_service.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.stationery.auth_service.repository.UserRepository;
import com.stationery.auth_service.entity.User;
import com.stationery.auth_service.dto.RegisterRequest;
import com.stationery.auth_service.dto.LoginRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User register(RegisterRequest request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return userRepository.save(user);
    }

    public String login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow();

        if(!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return "Login Successful";
    }
}

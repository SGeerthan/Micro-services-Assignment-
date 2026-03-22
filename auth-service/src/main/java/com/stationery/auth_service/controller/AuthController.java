package com.stationery.auth_service.controller;

import com.stationery.auth_service.dto.AuthResponse;
import com.stationery.auth_service.dto.LoginRequest;
import com.stationery.auth_service.dto.RegisterRequest;
import com.stationery.auth_service.entity.User;
import com.stationery.auth_service.repository.UserRepository;
import com.stationery.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    // ─── Public Endpoints ──────────────────────────────────────────────────────
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Auth Service is up and running");
    }

    /** Register a new user (or admin if role=ROLE_ADMIN is passed) */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /** Login and receive JWT + role */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ─── User Endpoints (any authenticated user) ────────────────────────────

    /** Returns the currently authenticated user's email (from JWT subject) */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> me(java.security.Principal principal) {
        return ResponseEntity.ok("Logged in as: " + principal.getName());
    }

    /** Returns the currently authenticated user's full profile */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getProfile(java.security.Principal principal) {
        return ResponseEntity.ok(authService.getUserInfo(principal.getName()));
    }

    // ─── Admin-Only Endpoints ───────────────────────────────────────────────

    /** List all registered users – ADMIN only */
    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}

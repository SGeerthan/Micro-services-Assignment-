package com.stationery.product_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    @Value("${auth-service.url}")
    private String authServiceUrl;

    /**
     * Validates JWT token by calling auth-service
     * This is the single source of truth for token validation
     * 
     * @param token JWT token
     * @return TokenValidationResult with email, role, and validity
     */
    public TokenValidationResult validateToken(String token) {
        try {
            String url = authServiceUrl + "/api/auth/validate-token";
            
            // Create headers with Bearer token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            // Call auth-service validation endpoint
            LinkedHashMap<String, Object> response = restTemplate.postForObject(
                    url, 
                    entity, 
                    LinkedHashMap.class
            );
            
            if (response == null) {
                return new TokenValidationResult(false, null, null);
            }
            
            Boolean valid = (Boolean) response.get("valid");
            String userEmail = (String) response.get("userEmail");
            String role = (String) response.get("role");
            
            return new TokenValidationResult(
                    valid != null && valid,
                    userEmail,
                    role
            );
            
        } catch (Exception e) {
            log.error("Error validating token with auth-service: {}", e.getMessage());
            return new TokenValidationResult(false, null, null);
        }
    }

    /**
     * DTO for token validation result
     */
    public static class TokenValidationResult {
        public final boolean valid;
        public final String userEmail;
        public final String role;

        public TokenValidationResult(boolean valid, String userEmail, String role) {
            this.valid = valid;
            this.userEmail = userEmail;
            this.role = role;
        }

        public boolean isValid() {
            return valid;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getRole() {
            return role;
        }
    }
}

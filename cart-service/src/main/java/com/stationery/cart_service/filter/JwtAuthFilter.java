package com.stationery.cart_service.filter;

import com.stationery.cart_service.service.AuthServiceClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthServiceClient authServiceClient;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        if (path.equals("/error")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs")
            || path.equals("/swagger-ui.html")
            || path.startsWith("/webjars/")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Delegate token validation to auth-service
                AuthServiceClient.TokenValidationResult validationResult = authServiceClient.validateToken(token);

                if (validationResult.isValid()) {
                    String email = validationResult.getUserEmail();
                    String role = validationResult.getRole();

                    // Create authority with ROLE_ prefix if not already present
                    String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.singletonList(grantedAuthority)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            }
        } catch (Exception e) {
            // Token validation failed, continue without authentication
        }

        filterChain.doFilter(request, response);
    }
}

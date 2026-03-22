package com.stationery.cart_service.controller;

import com.stationery.cart_service.dto.AddToCartRequest;
import com.stationery.cart_service.dto.CartResponse;
import com.stationery.cart_service.dto.CheckoutResponse;
import com.stationery.cart_service.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Principal principal) {
        return ResponseEntity.ok(cartService.getCart(principal.getName()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(Principal principal, @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(principal.getName(), request));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(Principal principal, @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeFromCart(principal.getName(), productId));
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(Principal principal, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        return ResponseEntity.ok(cartService.checkout(principal.getName(), token));
    }
}

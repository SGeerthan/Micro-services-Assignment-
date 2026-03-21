package com.stationery.cart_service.controller;

import com.stationery.cart_service.dto.*;
import com.stationery.cart_service.service.CartService;
import com.stationery.cart_service.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtService jwtService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItemToCart(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AddCartItemRequest request) {
        String email = jwtService.extractEmail(token);
        return ResponseEntity.ok(cartService.addItemToCart(email, request));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("Authorization") String token) {
        String email = jwtService.extractEmail(token);
        return ResponseEntity.ok(cartService.getCart(email));
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(itemId, request.getQuantity()));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestHeader("Authorization") String token) {
        String email = jwtService.extractEmail(token);
        cartService.clearCart(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<CartSummaryResponse> getCartSummary(@RequestHeader("Authorization") String token) {
        String email = jwtService.extractEmail(token);
        return ResponseEntity.ok(cartService.getCartSummary(email));
    }
}

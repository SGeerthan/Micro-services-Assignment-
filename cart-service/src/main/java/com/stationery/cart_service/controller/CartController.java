package com.stationery.cart_service.controller;

import com.stationery.cart_service.dto.AddToCartRequest;
import com.stationery.cart_service.dto.CartResponse;
import com.stationery.cart_service.dto.UpdateCartItemRequest;
import com.stationery.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.stationery.cart_service.entity.Coupon;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart Service", description = "Shopping cart, coupon, and admin coupon-management endpoints")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current cart", description = "Returns the authenticated user's cart")
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(cartService.getCart(userEmail));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product to the authenticated user's cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added to cart"),
            @ApiResponse(responseCode = "400", description = "Invalid cart item request")
    })
    public ResponseEntity<CartResponse> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(cartService.addItemToCart(userEmail, request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity", description = "Updates the quantity of an item in the cart")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            Authentication authentication,
            @Parameter(description = "Cart item ID", example = "1") @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(cartService.updateItemQuantity(userEmail, itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes one item from the authenticated user's cart")
    public ResponseEntity<CartResponse> removeItemFromCart(
            Authentication authentication,
            @Parameter(description = "Cart item ID", example = "1") @PathVariable Long itemId) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(cartService.removeItemFromCart(userEmail, itemId));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear cart", description = "Removes every item from the authenticated user's cart")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String userEmail = authentication.getName();
        cartService.clearCart(userEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/coupon")
    @Operation(summary = "Apply coupon", description = "Applies a coupon code to the authenticated user's cart")
    public ResponseEntity<CartResponse> applyCoupon(
            Authentication authentication,
            @RequestBody java.util.Map<String, String> request) {
        String userEmail = authentication.getName();
        String code = request.get("code");
        return ResponseEntity.ok(cartService.applyCoupon(userEmail, code));
    }

    @DeleteMapping("/coupon")
    @Operation(summary = "Remove coupon", description = "Removes the applied coupon from the authenticated user's cart")
    public ResponseEntity<CartResponse> removeCoupon(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(cartService.removeCoupon(userEmail));
    }

    // Admin endpoints for coupon management
    @GetMapping("/admin/coupons")
    @Operation(summary = "List coupons", description = "Returns all coupons; admin only")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(cartService.getAllCoupons());
    }

    @PostMapping("/admin/coupons")
    @Operation(summary = "Create coupon", description = "Creates a new coupon; admin only")
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(cartService.saveCoupon(coupon));
    }

    @DeleteMapping("/admin/coupons/{id}")
    @Operation(summary = "Delete coupon", description = "Deletes a coupon by ID; admin only")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        cartService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test-product/{id}")
    @Operation(summary = "Test product lookup", description = "Internal endpoint used to verify downstream product lookup")
    public ResponseEntity<?> testProduct(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cartService.testProductFetch(id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching product: " + e.getMessage());
        }
    }
}

package com.stationery.order_service.controller;

import com.stationery.order_service.dto.*;
import com.stationery.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Create checkout - persists the order as paid after user confirmation.
     */
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> createCheckout(
            Authentication authentication,
            @RequestBody CheckoutRequest request) {
        String userEmail = authentication.getName();
        request.setUserEmail(userEmail);
        OrderResponse response = orderService.createCheckout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all orders for the current user
     */
    @GetMapping("/user")
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        List<OrderResponse> orders = orderService.getUserOrders(userEmail);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get a specific order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Get all orders (admin only)
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Get payment history for an order
     */
    @GetMapping("/{orderId}/payments")
    public ResponseEntity<List<PaymentResponse>> getPaymentHistory(@PathVariable Long orderId) {
        List<PaymentResponse> payments = orderService.getPaymentHistory(orderId);
        return ResponseEntity.ok(payments);
    }
}
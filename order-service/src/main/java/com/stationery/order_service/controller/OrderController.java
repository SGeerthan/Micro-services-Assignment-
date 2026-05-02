package com.stationery.order_service.controller;

import com.stationery.order_service.dto.*;
import com.stationery.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Service", description = "Checkout, order history, and payment endpoints")
public class OrderController {

    private final OrderService orderService;

    /**
     * Create checkout - persists the order as paid after user confirmation.
     */
    @PostMapping("/checkout")
        @Operation(summary = "Create checkout", description = "Creates a paid order from the current cart")
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Checkout created"),
            @ApiResponse(responseCode = "400", description = "Invalid checkout request")
        })
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
    @Operation(summary = "Get current user's orders", description = "Returns all orders placed by the authenticated user")
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        List<OrderResponse> orders = orderService.getUserOrders(userEmail);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get a specific order by ID
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Returns a specific order by its identifier")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Get all orders (admin only)
     */
    @GetMapping
    @Operation(summary = "Get all orders", description = "Returns all orders in the system")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Get payment history for an order
     */
    @GetMapping("/{orderId}/payments")
    @Operation(summary = "Get payment history", description = "Returns payment records for a given order")
    public ResponseEntity<List<PaymentResponse>> getPaymentHistory(@PathVariable Long orderId) {
        List<PaymentResponse> payments = orderService.getPaymentHistory(orderId);
        return ResponseEntity.ok(payments);
    }
}
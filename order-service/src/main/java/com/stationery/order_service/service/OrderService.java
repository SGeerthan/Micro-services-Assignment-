package com.stationery.order_service.service;

import com.stationery.order_service.dto.*;
import com.stationery.order_service.entity.Order;
import com.stationery.order_service.entity.OrderItem;
import com.stationery.order_service.entity.Payment;
import com.stationery.order_service.kafka.OrderEventProducer;
import com.stationery.order_service.repository.OrderItemRepository;
import com.stationery.order_service.repository.OrderRepository;
import com.stationery.order_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CartClient cartClient;
    private final OrderEventProducer orderEventProducer;

    /**
     * Create an order from checkout request and mark it as paid after confirmation.
     */
    @Transactional
    public OrderResponse createCheckout(CheckoutRequest request) {
        try {
            // Create order entity
            Order order = new Order();
            order.setUserEmail(request.getUserEmail());
            order.setSubtotalAmount(request.getSubtotalAmount());
            order.setTaxAmount(request.getTaxAmount());
            order.setShippingAmount(request.getShippingAmount());
            order.setTotalAmount(request.getTotalAmount());
            order.setStatus("PENDING");
            order.setShippingAddress(request.getShippingAddress());
            order.setShippingCity(request.getShippingCity());
            order.setShippingZipCode(request.getShippingZipCode());
            order.setShippingCountry(request.getShippingCountry());
            order.setBillingAddress(request.getBillingAddress());
            order.setBillingCity(request.getBillingCity());
            order.setBillingZipCode(request.getBillingZipCode());
            order.setBillingCountry(request.getBillingCountry());
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());

            // Save order
            Order savedOrder = orderRepository.save(order);
            final Order orderForItems = savedOrder;

            // Save order items
            List<OrderItem> orderItems = request.getItems().stream()
                    .map(itemRequest -> {
                        OrderItem item = new OrderItem();
                        item.setOrder(orderForItems);
                        item.setProductId(itemRequest.getProductId());
                        item.setProductName(itemRequest.getProductName());
                        item.setProductPrice(itemRequest.getProductPrice());
                        item.setProductImageUrl(itemRequest.getProductImageUrl());
                        item.setQuantity(itemRequest.getQuantity());
                        item.setSubtotal(itemRequest.getSubtotal());
                        return item;
                    })
                    .collect(Collectors.toList());

            orderItemRepository.saveAll(orderItems);
            savedOrder.setItems(orderItems);

            // Create initial payment record (PENDING status)
            Payment payment = new Payment();
            payment.setOrder(savedOrder);
            payment.setAmount(savedOrder.getTotalAmount());
            payment.setStatus("COMPLETED");
            payment.setPaymentMethod("ONLINE");
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            payment.setCompletedAt(LocalDateTime.now());
            
            Payment savedPayment = paymentRepository.save(payment);
            savedOrder.setPayments(List.of(savedPayment));
            savedOrder.setStatus("PAID");
            savedOrder.setPaidAt(LocalDateTime.now());
            savedOrder.setUpdatedAt(LocalDateTime.now());

            // Publish order created event
            orderEventProducer.publishOrderCreated(savedOrder);
            
            log.info("Order created and marked paid: {}", savedOrder.getId());
            return mapOrderToResponse(savedOrder);

        } catch (Exception e) {
            log.error("Error creating checkout: {}", e.getMessage());
            throw new RuntimeException("Failed to create checkout: " + e.getMessage());
        }
    }

    /**
     * Get all orders for a specific user
     */
    public List<OrderResponse> getUserOrders(String userEmail) {
        List<Order> orders = orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
        return orders.stream()
                .map(this::mapOrderToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific order by ID
     */
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapOrderToResponse(order);
    }

    /**
     * Get all orders (admin functionality)
     */
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapOrderToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get payment history for an order
     */
    public List<PaymentResponse> getPaymentHistory(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream()
                .map(this::mapPaymentToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map Order entity to OrderResponse DTO
     */
    private OrderResponse mapOrderToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userEmail(order.getUserEmail())
                .items(order.getItems().stream()
                        .map(this::mapOrderItemToResponse)
                        .collect(Collectors.toList()))
                .payments(order.getPayments().stream()
                        .map(this::mapPaymentToResponse)
                        .collect(Collectors.toList()))
                .subtotalAmount(order.getSubtotalAmount())
                .taxAmount(order.getTaxAmount())
                .shippingAmount(order.getShippingAmount())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingZipCode(order.getShippingZipCode())
                .shippingCountry(order.getShippingCountry())
                .billingAddress(order.getBillingAddress())
                .billingCity(order.getBillingCity())
                .billingZipCode(order.getBillingZipCode())
                .billingCountry(order.getBillingCountry())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .paidAt(order.getPaidAt())
                .build();
    }

    /**
     * Map OrderItem entity to OrderItemResponse DTO
     */
    private OrderItemResponse mapOrderItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productPrice(item.getProductPrice())
                .productImageUrl(item.getProductImageUrl())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }

    /**
     * Map Payment entity to PaymentResponse DTO
     */
    private PaymentResponse mapPaymentToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }
}
package com.stationery.cart_service.service;

import com.stationery.cart_service.dto.*;
import com.stationery.cart_service.entity.Cart;
import com.stationery.cart_service.entity.CartItem;
import com.stationery.cart_service.exception.ResourceNotFoundException;
import com.stationery.cart_service.repository.CartItemRepository;
import com.stationery.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RestTemplate restTemplate;

    private static final String PRODUCT_SERVICE_URL = "http://localhost:8082/api/products/";

    @Transactional
    public CartResponse addItemToCart(String userEmail, AddCartItemRequest request) {
        // 1. Validate product with product-service
        ProductResponse product = restTemplate.getForObject(PRODUCT_SERVICE_URL + request.getProductId(), ProductResponse.class);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found in product-service: " + request.getProductId());
        }

        if (product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }

        // 2. Get or create cart
        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseGet(() -> Cart.builder()
                        .userEmail(userEmail)
                        .items(new ArrayList<>())
                        .build());

        // 3. Add or update item
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setUnitPrice(product.getPrice()); // Update with latest price
            item.calculateSubtotal();
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(request.getQuantity())
                    .imageUrl(product.getImageUrl())
                    .build();
            newItem.calculateSubtotal();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    public CartResponse getCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .map(this::mapToResponse)
                .orElseGet(() -> CartResponse.builder()
                        .userEmail(userEmail)
                        .items(new ArrayList<>())
                        .build());
    }

    @Transactional
    public CartResponse updateItemQuantity(Long itemId, int quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            Cart cart = item.getCart();
            cart.getItems().remove(item);
            Cart savedCart = cartRepository.save(cart);
            return mapToResponse(savedCart);
        }

        item.setQuantity(quantity);
        item.calculateSubtotal();
        Cart savedCart = cartRepository.save(item.getCart());
        return mapToResponse(savedCart);
    }

    @Transactional
    public CartResponse removeItem(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Transactional
    public void clearCart(String userEmail) {
        cartRepository.findByUserEmail(userEmail).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    public CartSummaryResponse getCartSummary(String userEmail) {
        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElse(null);

        if (cart == null) {
            return CartSummaryResponse.builder()
                    .totalItems(0)
                    .totalQuantity(0)
                    .totalPrice(BigDecimal.ZERO)
                    .build();
        }

        int totalItems = cart.getItems().size();
        int totalQuantity = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
        BigDecimal totalPrice = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartSummaryResponse.builder()
                .totalItems(totalItems)
                .totalQuantity(totalQuantity)
                .totalPrice(totalPrice)
                .build();
    }

    private CartResponse mapToResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userEmail(cart.getUserEmail())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .items(cart.getItems().stream()
                        .map(this::mapToItemResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private CartItemResponse mapToItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .imageUrl(item.getImageUrl())
                .subtotal(item.getSubtotal())
                .addedAt(item.getAddedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}

package com.stationery.cart_service.service;

import com.stationery.cart_service.client.AuthClient;
import com.stationery.cart_service.client.OrderClient;
import com.stationery.cart_service.client.ProductClient;
import com.stationery.cart_service.dto.*;
import com.stationery.cart_service.entity.Cart;
import com.stationery.cart_service.entity.CartItem;
import com.stationery.cart_service.repository.CartItemRepository;
import com.stationery.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final AuthClient authClient;
    private final OrderClient orderClient;

    public CartResponse getCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(String userEmail, AddToCartRequest request) {
        // 1. Validate product
        ProductResponse product = productClient.getProduct(request.getProductId());
        if (product == null || !Boolean.TRUE.equals(product.getAvailability())) {
            throw new RuntimeException("Product not available");
        }

        Cart cart = getOrCreateCart(userEmail);

        // 2. Add or update item
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProductId(request.getProductId());
            item.setProductName(product.getName());
            item.setImageUrl(product.getImageUrl());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(request.getQuantity());
            cart.getItems().add(item);
        } else {
            item.setQuantity(item.getQuantity() + request.getQuantity());
            // Update prices in case they changed
            item.setUnitPrice(product.getPrice());
        }

        item.calculateSubtotal();
        cartRepository.save(cart);

        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse removeFromCart(String userEmail, Long productId) {
        Cart cart = getOrCreateCart(userEmail);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Transactional
    public CheckoutResponse checkout(String userEmail, String token) {
        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 1. Revalidate every item against product-service
        double totalAmount = 0.0;
        for (CartItem item : cart.getItems()) {
            ProductResponse product = productClient.getProduct(item.getProductId());
            if (product == null || !Boolean.TRUE.equals(product.getAvailability())) {
                throw new RuntimeException("Product " + item.getProductName() + " is no longer available");
            }
            // Update to latest price
            item.setUnitPrice(product.getPrice());
            item.setProductName(product.getName());
            item.setImageUrl(product.getImageUrl());
            item.calculateSubtotal();
            totalAmount += item.getSubtotal();
        }
        cartRepository.saveAndFlush(cart);

        // 2. Resolve userId from auth-service
        AuthClient.UserInfo userInfo = authClient.getUserInfo(userEmail, token);
        if (userInfo == null || userInfo.getId() == null) {
            throw new RuntimeException("Could not resolve user identity");
        }

        // 3. Send order request
        OrderClient.OrderRequest orderRequest = new OrderClient.OrderRequest(userInfo.getId(), totalAmount);
        OrderClient.OrderResponse orderResponse = orderClient.placeOrder(orderRequest);

        if (orderResponse == null || orderResponse.getId() == null) {
            throw new RuntimeException("Order placement failed");
        }

        // 4. Clear cart after success
        cart.getItems().clear();
        cartRepository.save(cart);

        return new CheckoutResponse("Order placed successfully", orderResponse.getId(), totalAmount);
    }

    private Cart getOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserEmail(userEmail);
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse mapToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserEmail(cart.getUserEmail());
        response.setItems(cart.getItems().stream().map(item -> new CartItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getImageUrl(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
        )).collect(Collectors.toList()));
        
        response.setTotalAmount(cart.getItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum());
        
        return response;
    }
}

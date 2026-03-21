package com.stationery.cart_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private String userEmail;
    private List<CartItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public int getTotalItems() {
        return items != null ? items.size() : 0;
    }

    public int getTotalQuantity() {
        return items != null ? items.stream().mapToInt(CartItemResponse::getQuantity).sum() : 0;
    }

    public BigDecimal getTotalPrice() {
        return items != null ? items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
    }
}

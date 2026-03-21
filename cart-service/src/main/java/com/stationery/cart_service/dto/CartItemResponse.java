package com.stationery.cart_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private String productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String imageUrl;
    private BigDecimal subtotal;
    private java.time.LocalDateTime addedAt;
    private java.time.LocalDateTime updatedAt;
}

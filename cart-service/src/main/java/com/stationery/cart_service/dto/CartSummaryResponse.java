package com.stationery.cart_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryResponse {
    private int totalItems;
    private int totalQuantity;
    private BigDecimal totalPrice;
}

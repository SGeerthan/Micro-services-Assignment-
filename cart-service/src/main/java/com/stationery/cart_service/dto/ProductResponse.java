package com.stationery.cart_service.dto;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private String imageUrl;
    private Boolean availability;
}

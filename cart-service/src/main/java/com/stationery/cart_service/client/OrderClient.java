package com.stationery.cart_service.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OrderClient {

    private final RestTemplate restTemplate;

    @Value("${services.order}")
    private String orderServiceUrl;

    public OrderResponse placeOrder(OrderRequest request) {
        String url = orderServiceUrl + "/api/orders";
        return restTemplate.postForObject(url, request, OrderResponse.class);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderRequest {
        private Long userId;
        private Double totalAmount;
    }

    @Data
    public static class OrderResponse {
        private Long id;
        private Double totalAmount;
        private String status;
    }
}

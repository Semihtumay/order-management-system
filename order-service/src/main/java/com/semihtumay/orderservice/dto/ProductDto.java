package com.semihtumay.orderservice.dto;

import java.math.BigDecimal;

public record ProductDto(
        String productId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal totalPrice) {
}

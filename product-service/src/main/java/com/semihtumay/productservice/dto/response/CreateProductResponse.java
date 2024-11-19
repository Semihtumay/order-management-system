package com.semihtumay.productservice.dto.response;

import java.math.BigDecimal;

public record CreateProductResponse(
        String id,
        String name,
        BigDecimal price,
        Integer quantity,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal totalPrice
) {
}

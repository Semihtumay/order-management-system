package com.semihtumay.productservice.dto.request;

import java.math.BigDecimal;

public record UpdateProductRequest(
        String name,
        BigDecimal price,
        Integer quantity,
        BigDecimal tax,
        BigDecimal discount
) {
}

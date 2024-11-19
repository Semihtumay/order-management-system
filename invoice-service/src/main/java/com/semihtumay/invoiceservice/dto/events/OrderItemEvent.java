package com.semihtumay.invoiceservice.dto.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record OrderItemEvent(
        String productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal totalPrice
) {
}

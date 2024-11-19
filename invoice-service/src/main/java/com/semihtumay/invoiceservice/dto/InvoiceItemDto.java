package com.semihtumay.invoiceservice.dto;

import java.math.BigDecimal;

public record InvoiceItemDto(
        String productName,
        BigDecimal price,
        BigDecimal tax,
        BigDecimal discount,
        Integer quantity,
        BigDecimal totalPrice) {
}

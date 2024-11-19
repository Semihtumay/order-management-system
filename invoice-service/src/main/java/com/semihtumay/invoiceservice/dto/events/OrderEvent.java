package com.semihtumay.invoiceservice.dto.events;

import java.math.BigDecimal;
import java.util.List;

public record OrderEvent(
        String id,
        BigDecimal totalAmount,
        List<OrderItemEvent> items
) {
}

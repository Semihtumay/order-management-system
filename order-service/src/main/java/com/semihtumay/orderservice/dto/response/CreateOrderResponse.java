package com.semihtumay.orderservice.dto.response;

import com.semihtumay.orderservice.dto.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderResponse(
        String id,
        List<OrderItemDto> items,
        BigDecimal totalAmount
) {
}

package com.semihtumay.orderservice.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        String id,
        BigDecimal totalAmount,
        List<OrderItemDto> items) {

}

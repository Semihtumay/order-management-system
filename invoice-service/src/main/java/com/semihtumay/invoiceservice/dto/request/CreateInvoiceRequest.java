package com.semihtumay.invoiceservice.dto.request;

import com.semihtumay.invoiceservice.dto.InvoiceItemDto;

import java.math.BigDecimal;
import java.util.List;

public record CreateInvoiceRequest(
        String orderId,
        BigDecimal totalAmount,
        List<InvoiceItemDto> items
) {
}

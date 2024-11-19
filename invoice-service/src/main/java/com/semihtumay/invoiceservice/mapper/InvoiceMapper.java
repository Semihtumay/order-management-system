package com.semihtumay.invoiceservice.mapper;

import com.semihtumay.invoiceservice.dto.InvoiceItemDto;
import com.semihtumay.invoiceservice.dto.events.OrderEvent;
import com.semihtumay.invoiceservice.dto.request.CreateInvoiceRequest;
import com.semihtumay.invoiceservice.model.Invoice;
import com.semihtumay.invoiceservice.model.InvoiceItem;

import java.util.List;
import java.util.stream.Collectors;

public class InvoiceMapper {

    public static CreateInvoiceRequest mapToCreateInvoiceRequest(OrderEvent orderEvent) {
        return new CreateInvoiceRequest(
                orderEvent.id(),
                orderEvent.totalAmount(),
                orderEvent.items().stream().map(item ->
                        new InvoiceItemDto(
                                item.productName(),
                                item.price(),
                                item.tax(),
                                item.discount(),
                                item.quantity(),
                                item.totalPrice()
                        )
                ).toList()
        );
    }

    public static Invoice requestToInvoice(CreateInvoiceRequest request){
        Invoice invoice = new Invoice();
        invoice.setOrderId(request.orderId());
        invoice.setTotalAmount(request.totalAmount());

        List<InvoiceItem> invoiceItems = request.items().stream()
                .map(item -> {
                    InvoiceItem invoiceItem = new InvoiceItem();
                    invoiceItem.setInvoice(invoice);
                    invoiceItem.setProductName(item.productName());
                    invoiceItem.setPrice(item.price());
                    invoiceItem.setTax(item.tax());
                    invoiceItem.setDiscount(item.discount());
                    invoiceItem.setQuantity(item.quantity());
                    invoiceItem.setTotalPrice(item.totalPrice());
                    return invoiceItem;
                }).collect(Collectors.toList());

        invoice.setItems(invoiceItems);

        return invoice;
    }
}

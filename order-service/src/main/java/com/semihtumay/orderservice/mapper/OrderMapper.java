package com.semihtumay.orderservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semihtumay.orderservice.dto.OrderDto;
import com.semihtumay.orderservice.dto.OrderItemDto;
import com.semihtumay.orderservice.dto.ProductDto;
import com.semihtumay.orderservice.dto.response.CreateOrderResponse;
import com.semihtumay.orderservice.exception.JsonConversionException;
import com.semihtumay.orderservice.model.Order;
import com.semihtumay.orderservice.model.OrderItem;
import com.semihtumay.orderservice.model.Outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMapper {


    public static CreateOrderResponse orderToResponse(Order order) {
        List<OrderItemDto> items = order.getItems().stream().map(orderItem ->
                new OrderItemDto(
                        UUID.fromString(orderItem.getProductId()),
                        orderItem.getProductName(),
                        orderItem.getQuantity(),
                        orderItem.getPrice(),
                        orderItem.getTax(),
                        orderItem.getDiscount(),
                        orderItem.getTotalPrice()
                )
        ).collect(Collectors.toList());

        return new CreateOrderResponse(
                order.getId().toString(),
                items,
                order.getTotalAmount()
        );
    }

    public static OrderItem productDtoToOrderItemDto(ProductDto productDto){

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(productDto.productId());
        orderItem.setProductName(productDto.productName());
        orderItem.setPrice(productDto.price());
        orderItem.setTax(productDto.tax());
        orderItem.setDiscount(productDto.discount());
        orderItem.setQuantity(productDto.quantity());
        orderItem.setTotalPrice(productDto.totalPrice());

        return orderItem;
    }

    public static Outbox orderToOutbox(Order order){

        try {

            ObjectMapper mapper = new ObjectMapper();
            OrderDto orderDto = orderToOrderDto(order);
            String payload = mapper.writeValueAsString(orderDto);

            Outbox outbox = new Outbox();
            outbox.setAggregateId(order.getId().toString());
            outbox.setAggregateType("ORDER");
            outbox.setPayload(payload);
            outbox.setType("ORDER_CREATED");
            outbox.setCreatedAt(LocalDateTime.now());

            return outbox;
        }  catch (JsonProcessingException e) {
            throw new JsonConversionException("Failed to convert OrderDto to JSON payload", e);
        }
    }

    private static OrderDto orderToOrderDto(Order order){
        List<OrderItemDto> items = order.getItems().stream().map(orderItem ->
                new OrderItemDto(
                        UUID.fromString(orderItem.getProductId()),
                        orderItem.getProductName(),
                        orderItem.getQuantity(),
                        orderItem.getPrice(),
                        orderItem.getTax(),
                        orderItem.getDiscount(),
                        orderItem.getTotalPrice()
                )
        ).collect(Collectors.toList());

        return new OrderDto(order.getId().toString(), order.getTotalAmount(), items);
    }

}

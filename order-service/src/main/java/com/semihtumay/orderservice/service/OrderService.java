package com.semihtumay.orderservice.service;

import com.semihtumay.orderservice.client.ProductServiceClient;
import com.semihtumay.orderservice.dto.ProductDto;
import com.semihtumay.orderservice.dto.request.CreateOrderRequest;
import com.semihtumay.orderservice.dto.response.CreateOrderResponse;
import com.semihtumay.orderservice.mapper.OrderMapper;
import com.semihtumay.orderservice.model.Order;
import com.semihtumay.orderservice.model.OrderItem;
import com.semihtumay.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final OutboxService outboxService;

    public OrderService(OrderRepository orderRepository, ProductServiceClient productServiceClient, OutboxService outboxService) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
        this.outboxService = outboxService;
    }

    @Transactional
    public CreateOrderResponse createOrder(List<CreateOrderRequest> requests){

        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;


        for (CreateOrderRequest request : requests){
            ProductDto productDto = productServiceClient.checkStock(UUID.fromString(request.productId()), request.quantity());
            OrderItem orderItem = OrderMapper.productDtoToOrderItemDto(productDto);
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(productDto.totalPrice());
            orderItem.setOrder(order);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        requests.forEach(request -> {
            productServiceClient.decreaseStock(UUID.fromString(request.productId()), request.quantity());
        });

        orderRepository.save(order);
        outboxService.createOutbox(OrderMapper.orderToOutbox(order));

        return OrderMapper.orderToResponse(order);
    }
}

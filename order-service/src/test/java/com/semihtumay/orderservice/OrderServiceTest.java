package com.semihtumay.orderservice;

import com.semihtumay.orderservice.client.ProductServiceClient;
import com.semihtumay.orderservice.dto.ProductDto;
import com.semihtumay.orderservice.dto.request.CreateOrderRequest;
import com.semihtumay.orderservice.dto.response.CreateOrderResponse;
import com.semihtumay.orderservice.model.Order;
import com.semihtumay.orderservice.repository.OrderRepository;
import com.semihtumay.orderservice.service.OrderService;
import com.semihtumay.orderservice.service.OutboxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void should_create_order_successfully() {
        UUID product1 = UUID.randomUUID();
        UUID product2 = UUID.randomUUID();

        CreateOrderRequest request1 = new CreateOrderRequest(product1.toString(), 2);
        CreateOrderRequest request2 = new CreateOrderRequest(product2.toString(), 1);
        List<CreateOrderRequest> requests = List.of(request1, request2);

        ProductDto productDto1 = new ProductDto(
                product1.toString(),
                "Iphone-15",
                BigDecimal.valueOf(80000.00),
                101,
                BigDecimal.valueOf(0.18),
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(82600.00)
        );
        ProductDto productDto2 = new ProductDto(
                product2.toString(),
                "Iphone-16",
                BigDecimal.valueOf(100000.00),
                111,
                BigDecimal.valueOf(0.18),
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(100600.00)
        );

        when(productServiceClient.checkStock(any(UUID.class), anyInt()))
                .thenReturn(productDto1, productDto2);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(UUID.randomUUID());
                    return order;
                });

        CreateOrderResponse response = orderService.createOrder(requests);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(BigDecimal.valueOf(183200.00), response.totalAmount());
        verify(productServiceClient, times(2)).checkStock(any(UUID.class), anyInt());
        verify(productServiceClient, times(2)).decreaseStock(any(UUID.class), anyInt());
        verify(outboxService).createOutbox(any());
        verify(orderRepository).save(any(Order.class));
    }

}

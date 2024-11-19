package com.semihtumay.orderservice;

import com.semihtumay.orderservice.client.ProductServiceClient;
import com.semihtumay.orderservice.dto.ProductDto;
import com.semihtumay.orderservice.dto.request.CreateOrderRequest;
import com.semihtumay.orderservice.dto.response.CreateOrderResponse;
import com.semihtumay.orderservice.exception.InsufficientStockException;
import com.semihtumay.orderservice.repository.OrderRepository;
import com.semihtumay.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> testContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private ProductServiceClient productServiceClient;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", testContainer::getJdbcUrl);
        registry.add("spring.datasource.username", testContainer::getUsername);
        registry.add("spring.datasource.password", testContainer::getPassword);
    }

    @Test
    @Transactional
    void should_create_order_successfully() {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        CreateOrderRequest request1 = new CreateOrderRequest(productId1.toString(), 3);
        CreateOrderRequest request2 = new CreateOrderRequest(productId2.toString(), 2);

        ProductDto productDto1 = new ProductDto(
                productId1.toString(),
                "Iphone-15",
                BigDecimal.valueOf(80000.00),
                101,
                BigDecimal.valueOf(0.18),
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(82600.00)
        );
        ProductDto productDto2 = new ProductDto(
                productId2.toString(),
                "Iphone-16",
                BigDecimal.valueOf(100000.00),
                111,
                BigDecimal.valueOf(0.18),
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(100600.00)
        );

        Mockito.when(productServiceClient.checkStock(eq(productId1), eq(3))).thenReturn(productDto1);
        Mockito.when(productServiceClient.checkStock(eq(productId2), eq(2))).thenReturn(productDto2);
        Mockito.doNothing().when(productServiceClient).decreaseStock(any(UUID.class), any(Integer.class));

        CreateOrderResponse response = orderService.createOrder(List.of(request1, request2));

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(new BigDecimal("183200.0"), response.totalAmount());

        var savedOrder = orderRepository.findById(UUID.fromString(response.id()));
        assertNotNull(savedOrder.orElse(null));
        assertEquals(2, savedOrder.get().getItems().size());
        assertEquals(new BigDecimal("183200.0"), savedOrder.get().getTotalAmount());

        Mockito.verify(productServiceClient, Mockito.times(1)).checkStock(eq(productId1), eq(3));
        Mockito.verify(productServiceClient, Mockito.times(1)).checkStock(eq(productId2), eq(2));
        Mockito.verify(productServiceClient, Mockito.times(1)).decreaseStock(eq(productId1), eq(3));
        Mockito.verify(productServiceClient, Mockito.times(1)).decreaseStock(eq(productId2), eq(2));
    }

    @Test
    @Transactional
    void should_throw_exception_when_stock_is_insufficient() {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        CreateOrderRequest request1 = new CreateOrderRequest(productId1.toString(), 10);
        CreateOrderRequest request2 = new CreateOrderRequest(productId2.toString(), 2);

        Mockito.when(productServiceClient.checkStock(eq(productId1), eq(10)))
                .thenThrow(new InsufficientStockException("Insufficient stock for product ID: " + productId1));

        ProductDto productDto2 = new ProductDto(
                productId2.toString(),
                "Iphone-16",
                BigDecimal.valueOf(100000.00),
                111,
                BigDecimal.valueOf(0.18),
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(100600.00)
        );
        Mockito.when(productServiceClient.checkStock(eq(productId2), eq(2)))
                .thenReturn(productDto2);

        Mockito.doNothing().when(productServiceClient).decreaseStock(any(UUID.class), any(Integer.class));

        Exception exception = assertThrows(InsufficientStockException.class, () -> {
            orderService.createOrder(List.of(request1, request2));
        });

        assertTrue(exception.getMessage().contains("Insufficient stock for product ID"));
    }
}

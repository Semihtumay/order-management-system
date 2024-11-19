package com.semihtumay.productservice;

import com.semihtumay.productservice.exception.InsufficientStockException;
import com.semihtumay.productservice.model.Product;
import com.semihtumay.productservice.repository.ProductRepository;
import com.semihtumay.productservice.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class ProductServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> testContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", testContainer::getJdbcUrl);
        registry.add("spring.datasource.username", testContainer::getUsername);
        registry.add("spring.datasource.password", testContainer::getPassword);
    }

    private UUID productId;
    private Product product;

    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setName("Product Name");
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(10);
        product.setTax(new BigDecimal("0.18"));
        product.setDiscount(new BigDecimal("0.10"));

        productRepository.save(product);
        productId = product.getId();
    }

    @Test
    public void should_decrease_stock_with_concurrent_threads() throws InterruptedException {
        int initialQuantity = product.getQuantity();
        int decreaseAmount = 6;

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicBoolean firstThreadSuccess = new AtomicBoolean(false);
        AtomicBoolean secondThreadFailed = new AtomicBoolean(false);

        Runnable task = () -> {
            try {
                productService.decreaseStock(productId, decreaseAmount);
                firstThreadSuccess.set(true);
            } catch (InsufficientStockException e) {
                secondThreadFailed.set(true);
            } finally {
                latch.countDown();
            }
        };

        executorService.submit(task);
        executorService.submit(task);

        latch.await(5, TimeUnit.SECONDS);

        Assertions.assertTrue(firstThreadSuccess.get(), "First thread should succeed.");
        Assertions.assertTrue(secondThreadFailed.get(), "Second thread should fail due to insufficient stock.");

        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        Assertions.assertEquals(initialQuantity - decreaseAmount, updatedProduct.getQuantity(),
                "Stock should be decremented only once.");
    }

    @Test
    public void should_decrease_stock_with_concurrent_threads_both_success() throws InterruptedException {
        int initialQuantity = product.getQuantity();
        int decreaseAmount = 4;

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicBoolean firstThreadSuccess = new AtomicBoolean(false);
        AtomicBoolean secondThreadSuccess = new AtomicBoolean(false);

        Runnable task = () -> {
            try {
                productService.decreaseStock(productId, decreaseAmount);
                firstThreadSuccess.set(true);
                secondThreadSuccess.set(true);
            } catch (InsufficientStockException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        executorService.submit(task);
        executorService.submit(task);

        latch.await(5, TimeUnit.SECONDS);

        Assertions.assertTrue(firstThreadSuccess.get(), "First thread should succeed.");
        Assertions.assertTrue(secondThreadSuccess.get(), "Second thread should succeed.");

        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        Assertions.assertEquals(initialQuantity - (decreaseAmount * 2), updatedProduct.getQuantity(),
                "Stock should be decremented by both threads.");
    }
}

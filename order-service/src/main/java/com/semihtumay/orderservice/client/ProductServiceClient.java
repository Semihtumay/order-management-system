package com.semihtumay.orderservice.client;

import com.semihtumay.orderservice.dto.ProductDto;
import com.semihtumay.orderservice.exception.InsufficientStockException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ProductServiceClient {

    @GetMapping("/{id}/check-stock")
    @CircuitBreaker(name = "product-service", fallbackMethod = "checkStockFallback")
    ProductDto checkStock(@PathVariable("id") UUID productId, @RequestParam("quantity") Integer quantity);

    @PostMapping("/{id}/decrease-stock")
    @CircuitBreaker(name = "product-service", fallbackMethod = "decreaseStockFallback")
    void decreaseStock(@PathVariable("id") UUID productId, @RequestParam("quantity") Integer quantity);

    default ProductDto checkStockFallback(UUID productId, Integer quantity, Throwable throwable) {
        throw new InsufficientStockException("Fallback: Insufficient stock for product ID: " + productId);
    }
    default void decreaseStockFallback(UUID productId, Integer quantity, Throwable throwable) {
        throw new InsufficientStockException("Fallback: Insufficient stock for product ID: " + productId);
    }
}

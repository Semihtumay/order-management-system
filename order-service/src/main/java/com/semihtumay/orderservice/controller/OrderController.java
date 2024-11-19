package com.semihtumay.orderservice.controller;

import com.semihtumay.orderservice.dto.request.CreateOrderRequest;
import com.semihtumay.orderservice.dto.response.CreateOrderResponse;
import com.semihtumay.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody List<CreateOrderRequest> requests){
        return ResponseEntity.ok(orderService.createOrder(requests));
    }
}

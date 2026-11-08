package com.ecommerce.store.controller;


import com.ecommerce.store.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.store.dto.OrderResponse;



import com.ecommerce.store.dto.PageResponse;
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(auth.getName()));
    }

    @GetMapping
    public PageResponse<OrderResponse> list(Authentication auth,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return orderService.getUserOrders(auth.getName(), page, size);
    }
}
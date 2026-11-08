package com.ecommerce.store.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, String status, LocalDateTime orderDate,
                            BigDecimal totalPrice, List<OrderItemResponse> items) {}
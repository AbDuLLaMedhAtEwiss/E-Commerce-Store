package com.ecommerce.store.mapper;

import com.ecommerce.store.dto.*;
import com.ecommerce.store.entity.*;

import java.math.BigDecimal;
import java.util.List;

public final class DtoMapper {

    private DtoMapper() {}

    public static CategoryResponse toCategoryResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }

    public static ProductResponse toProductResponse(Product p) {
        CategoryResponse category = p.getCategory() == null ? null : toCategoryResponse(p.getCategory());
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getStockQuantity(), category);
    }

    public static CartItemResponse toCartItemResponse(CartItem item) {
        BigDecimal unitPrice = item.getProduct().getPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(item.getId(), item.getProduct().getId(),
                item.getProduct().getName(), unitPrice, item.getQuantity(), subtotal);
    }

    public static CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(DtoMapper::toCartItemResponse).toList();
        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), items, total);
    }

    public static OrderItemResponse toOrderItemResponse(OrderItem item) {
        BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new OrderItemResponse(item.getProduct().getId(), item.getProduct().getName(),
                item.getPrice(), item.getQuantity(), subtotal);
    }

    public static OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(DtoMapper::toOrderItemResponse).toList();
        return new OrderResponse(order.getId(), order.getStatus().name(),
                order.getOrderDate(), order.getTotalPrice(), items);
    }
}
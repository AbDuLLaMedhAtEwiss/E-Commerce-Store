package com.ecommerce.store.service;
import com.ecommerce.store.exception.*;

import java.util.Comparator;

import com.ecommerce.store.entity.*;
import com.ecommerce.store.repository.CartRepository;
import com.ecommerce.store.repository.OrderRepository;
import com.ecommerce.store.repository.ProductRepository;
import com.ecommerce.store.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;

import com.ecommerce.store.dto.OrderResponse;
import com.ecommerce.store.dto.PageResponse;
import com.ecommerce.store.mapper.DtoMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.ecommerce.store.util.Paging;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse placeOrder(String userEmail) {
        User user = findUser(userEmail);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException("Cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        BigDecimal total = BigDecimal.ZERO;

        // ترتيب ثابت للـ locks عشان نتجنب deadlock
        List<CartItem> sorted = cart.getItems().stream()
                .sorted(Comparator.comparing(i -> i.getProduct().getId()))
                .toList();

        for (CartItem cartItem : sorted) {
            Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessException("Insufficient stock for: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.getItems().add(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);
        cart.getItems().clear();
        return DtoMapper.toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(String userEmail, int page, int size) {
        User user = findUser(userEmail);
        Page<Order> orders = orderRepository.findByUser(user,
                Paging.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate")));
        return PageResponse.from(orders.map(DtoMapper::toOrderResponse));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
package com.ecommerce.store.service;

import com.ecommerce.store.dto.OrderResponse;
import com.ecommerce.store.entity.*;
import com.ecommerce.store.exception.BusinessException;
import com.ecommerce.store.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @InjectMocks private OrderService orderService;

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setEmail("a@a.com");
        return u;
    }

    private Product product(int stock) {
        Product p = new Product();
        p.setId(1L);
        p.setName("Laptop");
        p.setPrice(new BigDecimal("100.00"));
        p.setStockQuantity(stock);
        return p;
    }

    private Cart cartWith(User u, Product p, int qty) {
        Cart cart = new Cart();
        cart.setUser(u);
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(p);
        item.setQuantity(qty);
        cart.getItems().add(item);
        return cart;
    }

    @Test
    void placeOrder_success_decrementsStock_andClearsCart() {
        User u = user();
        Product p = product(5);
        Cart cart = cartWith(u, p, 2);
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(u));
        when(cartRepository.findByUser(u)).thenReturn(Optional.of(cart));
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(p));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.placeOrder("a@a.com");

        assertEquals(3, p.getStockQuantity());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, new BigDecimal("200.00").compareTo(response.totalPrice()));
        assertEquals("PENDING", response.status());
    }

    @Test
    void placeOrder_insufficientStock_throwsAndSavesNothing() {
        User u = user();
        Product p = product(1);
        Cart cart = cartWith(u, p, 2);
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(u));
        when(cartRepository.findByUser(u)).thenReturn(Optional.of(cart));
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(p));

        assertThrows(BusinessException.class, () -> orderService.placeOrder("a@a.com"));

        assertEquals(1, p.getStockQuantity());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_emptyCart_throws() {
        User u = user();
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(u));
        when(cartRepository.findByUser(u)).thenReturn(Optional.of(new Cart()));

        assertThrows(BusinessException.class, () -> orderService.placeOrder("a@a.com"));
        verify(orderRepository, never()).save(any());
    }
}
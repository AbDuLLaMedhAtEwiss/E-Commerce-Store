package com.ecommerce.store.controller;

import com.ecommerce.store.entity.Cart;
import com.ecommerce.store.service.CartService;

import jakarta.validation.Valid;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.store.dto.AddToCartRequest;
import com.ecommerce.store.dto.CartResponse;
import com.ecommerce.store.mapper.DtoMapper;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public CartResponse add(Authentication auth, @Valid @RequestBody AddToCartRequest request) {
        return cartService.addProductToCart(auth.getName(), request.productId(), request.quantity());
    }

    @GetMapping
    public CartResponse get(Authentication auth) {
        return cartService.getUserCart(auth.getName());
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse remove(Authentication auth, @PathVariable Long productId) {
        return cartService.removeProduct(auth.getName(), productId);
    }
}
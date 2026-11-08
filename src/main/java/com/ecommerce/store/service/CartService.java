package com.ecommerce.store.service;

import com.ecommerce.store.entity.Cart;
import com.ecommerce.store.entity.CartItem;
import com.ecommerce.store.entity.Product;
import com.ecommerce.store.entity.User;
import com.ecommerce.store.repository.CartRepository;
import com.ecommerce.store.repository.ProductRepository;
import com.ecommerce.store.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.store.exception.BusinessException;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.dto.CartResponse;
import com.ecommerce.store.mapper.DtoMapper;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse addProductToCart(String userEmail, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Cart cart = getOrCreateCart(userEmail);

        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst().orElse(null);

        int newQuantity = (existing == null ? 0 : existing.getQuantity()) + quantity;
        if (newQuantity > product.getStockQuantity()) {
            throw new BusinessException("Requested quantity exceeds available stock");
        }

        if (existing != null) {
            existing.setQuantity(newQuantity);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(newQuantity);
            cart.getItems().add(item);
        }
        return DtoMapper.toCartResponse(cartRepository.saveAndFlush(cart));
    }

    @Transactional
    public CartResponse getUserCart(String userEmail) {
        return DtoMapper.toCartResponse(getOrCreateCart(userEmail));
    }

    @Transactional
    public CartResponse removeProduct(String userEmail, Long productId) {
        Cart cart = getOrCreateCart(userEmail);
        boolean removed = cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        if (!removed) {
            throw new ResourceNotFoundException("Product not in cart");
        }
        return DtoMapper.toCartResponse(cart);
    }

    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }
}
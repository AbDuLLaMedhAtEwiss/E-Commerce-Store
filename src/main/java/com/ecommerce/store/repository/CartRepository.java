package com.ecommerce.store.repository;

import com.ecommerce.store.entity.Cart;
import com.ecommerce.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // نبحث عن السلة الخاصة بمستخدم معين بناءً على المستخدم نفسه
    Optional<Cart> findByUser(User user);
}
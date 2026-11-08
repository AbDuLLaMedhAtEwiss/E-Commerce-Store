package com.ecommerce.store.repository;
import com.ecommerce.store.entity.Product;

import jakarta.persistence.LockModeType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface ProductRepository extends JpaRepository<Product, Long> {
    
@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
    @Query("""
        select p from Product p
        where (:categoryId is null or p.category.id = :categoryId)
          and (:q is null or lower(p.name) like lower(concat('%', :q, '%')))
        """)
Page<Product> search(@Param("categoryId") Long categoryId,
                     @Param("q") String q,
                     Pageable pageable);

}

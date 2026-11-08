package com.ecommerce.store.service;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.ProductRequest;
import com.ecommerce.store.entity.Category;
import com.ecommerce.store.entity.Product;
import com.ecommerce.store.exception.ResourceNotFoundException;
import com.ecommerce.store.repository.CategoryRepository;
import com.ecommerce.store.repository.ProductRepository;
import com.ecommerce.store.dto.PageResponse;
import com.ecommerce.store.dto.ProductResponse;
import com.ecommerce.store.mapper.DtoMapper;
import com.ecommerce.store.util.Paging;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(Long categoryId, String q, int page, int size) {
        String query = (q == null || q.isBlank()) ? null : q.trim();
        Page<Product> result = productRepository.search(categoryId, query,
                Paging.of(page, size, Sort.by("id")));
        return PageResponse.from(result.map(DtoMapper::toProductResponse));
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return DtoMapper.toProductResponse(findEntity(id));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return DtoMapper.toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findEntity(id);
        apply(product, request);
        return DtoMapper.toProductResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.delete(findEntity(id));
    }

    private Product findEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private void apply(Product product, ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);
    }
}
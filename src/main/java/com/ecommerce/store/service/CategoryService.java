package com.ecommerce.store.service;

import com.ecommerce.store.entity.Category;
import com.ecommerce.store.repository.CategoryRepository;


import org.springframework.stereotype.Service;

import java.util.List;


import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.store.dto.CategoryRequest;
import com.ecommerce.store.exception.BusinessException;
import com.ecommerce.store.dto.CategoryResponse;
import com.ecommerce.store.mapper.DtoMapper;


@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(DtoMapper::toCategoryResponse).toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new BusinessException("Category name already exists");
        }
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        return DtoMapper.toCategoryResponse(categoryRepository.save(category));
    }
}
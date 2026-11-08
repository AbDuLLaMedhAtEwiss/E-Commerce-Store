package com.ecommerce.store.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ProductRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 255) String description,
        @NotNull @Positive @Digits(integer = 10, fraction = 2) BigDecimal price,
        @NotNull @Min(0) Integer stockQuantity,
        @NotNull Long categoryId) {}
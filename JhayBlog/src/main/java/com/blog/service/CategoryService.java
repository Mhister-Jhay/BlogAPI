package com.blog.service;

import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createNewCategory(CategoryRequest categoryRequest);

    CategoryResponse updateCategoryName(Long categoryId, CategoryRequest categoryRequest);

    void deleteCategory(Long categoryId);

    List<CategoryResponse> getAllCategories();
}

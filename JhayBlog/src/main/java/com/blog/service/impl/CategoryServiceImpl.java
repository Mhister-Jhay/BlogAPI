package com.blog.service.impl;

import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.CategoryResponse;
import com.blog.exception.ResourceAlreadyExistException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Category;
import com.blog.repository.CategoryRepository;
import com.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryResponse createNewCategory(CategoryRequest categoryRequest){
        if(categoryRepository.existsByName(categoryRequest.getName())){
            throw new ResourceAlreadyExistException("Category with name ("+ categoryRequest.getName()+") already exists");
        }
        return mapToCategoryDTO(categoryRepository.save(mapToCategory(categoryRequest)));
    }
    @Override
    public CategoryResponse updateCategoryName(Long categoryId, CategoryRequest categoryRequest){
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if(optionalCategory.isEmpty()){
            throw new ResourceNotFoundException("Category with id ("+categoryId+") does not exist");
        }
        Category category = optionalCategory.get();
        category.setName(categoryRequest.getName());
        return mapToCategoryDTO(categoryRepository.save(category));
    }
    @Override
    public void deleteCategory(Long categoryId){
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if(optionalCategory.isEmpty()){
            throw new ResourceNotFoundException("Category with id ("+categoryId+") does not exist");
        }
        categoryRepository.deleteById(categoryId);
    }
    @Override
    public List<CategoryResponse> getAllCategories(){
        return categoryRepository.findAll().stream().map(this::mapToCategoryDTO).collect(Collectors.toList());
    }
    private CategoryResponse mapToCategoryDTO(Category category){
        return modelMapper.map(category, CategoryResponse.class);
    }
    private Category mapToCategory(CategoryRequest categoryRequest){
        return modelMapper.map(categoryRequest,Category.class);
    }
}

package com.blog.repository;

import com.blog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByName(String name);
    Category findByName(String name);
    Optional<Category> findCategoryByName(String name);
}

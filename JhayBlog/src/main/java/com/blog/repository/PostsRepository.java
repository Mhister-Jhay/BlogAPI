package com.blog.repository;

import com.blog.model.Users;
import com.blog.model.Category;
import com.blog.model.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostsRepository extends JpaRepository<Posts,Long> {
    boolean existsByTitle(String title);
    Page<Posts> findAllByUser(Users users, Pageable pageable);
    Page<Posts> findAllByCategoriesContaining(Category category, Pageable pageable);
}

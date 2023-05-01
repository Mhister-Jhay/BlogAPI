package com.blog.repository;

import com.blog.model.Comment;
import com.blog.model.Posts;
import com.blog.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    boolean existsByUserAndPostAndBody(Users users, Posts posts, String body);
    Page<Comment> findAllByPost(Posts posts, Pageable pageable);
}

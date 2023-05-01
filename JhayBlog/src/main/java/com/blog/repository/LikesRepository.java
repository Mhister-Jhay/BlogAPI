package com.blog.repository;

import com.blog.model.Likes;
import com.blog.model.Posts;
import com.blog.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
    List<Likes> findAllByPost(Posts posts);
    boolean existsByUserAndPost(Users users, Posts posts);
    Likes findByUserAndPost(Users users, Posts posts);
    Page<Likes> findAllByPost(Posts posts, Pageable pageable);
    void deleteByUserAndPost(Users users, Posts posts);
}

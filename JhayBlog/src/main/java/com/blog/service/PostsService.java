package com.blog.service;

import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.PageResponse;
import com.blog.dto.response.PostsResponse;
import com.blog.dto.request.PostRequest;

public interface PostsService {
    PostsResponse createNewPost(PostRequest postRequest);

    PostsResponse addCategoryToPost(Long postId, CategoryRequest categoryRequest);

    PostsResponse removeCategoryFromPost(Long postId, String categoryName);

    PostsResponse removeCategoryAdmin(Long postId, String categoryName);

    PageResponse getAllPost(int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse getAllPostByUser(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);

    PageResponse getAllPostByCategory(int pageNo, int pageSize, String sortBy, String sortDir, String categoryName);

    PostsResponse getSinglePost(Long postId);

    PostsResponse editUserPosts(Long postId, PostRequest postRequest);

    void userDeletePost(Long postId);

    void adminDeletePost(Long postId);

    void superDeletePost(Long postId);
}

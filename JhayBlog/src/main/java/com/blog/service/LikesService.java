package com.blog.service;

import com.blog.dto.response.PostsResponse;
import com.blog.dto.response.PageResponse;

public interface LikesService {
    PostsResponse likePost(Long postId);

    PostsResponse unlikePost(Long postId);

    PageResponse getAllLikes(int pageNo, int pageSize, String sortBy, String sortDir, Long postId);
}

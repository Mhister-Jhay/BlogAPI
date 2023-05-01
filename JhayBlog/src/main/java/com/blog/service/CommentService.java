package com.blog.service;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.dto.response.PageResponse;
import com.blog.dto.response.PostsResponse;
import org.springframework.transaction.annotation.Transactional;

public interface CommentService {
    PostsResponse createNewComment(Long postId, CommentRequest commentRequest);

    PostsResponse deleteComment(Long postId, Long commentId);

    CommentResponse editComment(Long commentId, CommentRequest commentRequest);

    PageResponse viewComments(int pageNo, int pageSize, String sortBy, String sortDir, Long postId);
}

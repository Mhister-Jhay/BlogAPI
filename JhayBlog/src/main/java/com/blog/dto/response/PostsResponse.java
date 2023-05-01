package com.blog.dto.response;

import com.blog.dto.request.CategoryRequest;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostsResponse {
    private Long id;
    private String title;
    private String caption;
    private String content;
    private Set<CategoryRequest> categories;
    private UserResponse user;
    private Long likeCount;
    private Long commentCount;
}

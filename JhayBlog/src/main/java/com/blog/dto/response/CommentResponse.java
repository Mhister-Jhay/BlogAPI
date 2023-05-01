package com.blog.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    @NotBlank(message = "Body must be Blank")
    @Size(min = 20,message = "Body must contain at least 20 characters")
    private String body;
    private UserResponse user;
    private PostsResponse post;
}

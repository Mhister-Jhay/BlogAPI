package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequest {
    @NotBlank(message = "Title must be Blank")
    @Size(min = 10,message = "Title must contain at least 10 characters")
    private String title;
    @NotBlank(message = "Caption must be Blank")
    @Size(min = 20,message = "Caption must contain at least 20 characters")
    private String caption;
    @NotBlank(message = "Content must be Blank")
    @Size(min = 20,message = "Content must contain at least 20 characters")
    private String content;
}

package com.blog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequest {
    @NotBlank(message = "Name must be Blank")
    private String fullName;
    @NotBlank(message = "Username must be Blank")
    private String username;
    @Email
    @NotBlank(message = "Email must be Blank")
    private String email;
    @NotBlank(message = "Password must be Blank")
    private String password;
}

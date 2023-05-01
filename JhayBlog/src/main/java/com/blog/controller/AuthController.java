package com.blog.controller;

import com.blog.dto.response.UserResponse;
import com.blog.dto.response.JWTAuthResponse;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.request.SignUpRequest;
import com.blog.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.blog.constant.UserDetails.getLoggedInUser;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping("/sign-up")
    public ResponseEntity<String> registerNewUser(@Valid @RequestBody SignUpRequest signUpRequest){
        userServiceImpl.registerUser(signUpRequest);
        return new ResponseEntity<>("User Registered Successfully", HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JWTAuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        return new ResponseEntity<>(userServiceImpl.authenticateUser(loginRequest),HttpStatus.OK);
    }
}

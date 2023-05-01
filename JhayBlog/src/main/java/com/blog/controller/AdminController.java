package com.blog.controller;

import com.blog.dto.response.PostsResponse;
import com.blog.dto.response.UserResponse;
import com.blog.service.impl.UserServiceImpl;
import com.blog.service.impl.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    private final UserServiceImpl userServiceImpl;
    private final PostServiceImpl postServiceImpl;
    @PatchMapping("/users/{userId}")
    public ResponseEntity<UserResponse> editUserStatus(@PathVariable Long userId,
                                                       @RequestParam(value = "status") String status){
        return new ResponseEntity<>(userServiceImpl.editUserStatus(userId,status),HttpStatus.OK);
    }
    @DeleteMapping("/posts/{postId}/category")
    public ResponseEntity<PostsResponse> removeCategoryFromPost(@RequestParam("categoryName") String categoryName,
                                                           @PathVariable Long postId){
        return new ResponseEntity<>(postServiceImpl.removeCategoryAdmin(postId,categoryName), HttpStatus.OK);
    }
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId){
        postServiceImpl.adminDeletePost(postId);
        return new ResponseEntity<>("Post with id ("+postId+") deleted successfully", HttpStatus.OK);
    }
}

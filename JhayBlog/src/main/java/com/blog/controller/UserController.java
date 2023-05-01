package com.blog.controller;

import com.blog.constant.PageConstant;
import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.*;
import com.blog.dto.request.CategoryRequest;
import com.blog.dto.request.PostRequest;
import com.blog.service.impl.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.blog.constant.UserDetails.getLoggedInUser;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final CategoryServiceImpl categoryServiceImpl;
    private final PostServiceImpl postServiceImpl;
    private final LikesServiceImpl likesServiceImpl;
    private final CommentServiceImpl commentServiceImpl;

    // Category Function
    // View All Categories
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        return new ResponseEntity<>(categoryServiceImpl.getAllCategories(), HttpStatus.OK);
    }

    // User Function

    // View A single User
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getSingleUser(@PathVariable Long userId){
        return new ResponseEntity<>(userServiceImpl.getSingleUser(userId), HttpStatus.OK);
    }

    // Post Function

    // Create A new post
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/posts")
    public ResponseEntity<PostsResponse> createNewPost(@Valid @RequestBody PostRequest postRequest){
        return new ResponseEntity<>(postServiceImpl.createNewPost(postRequest), HttpStatus.CREATED);
    }

    // Add Categories to Post
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/posts/categories{postId}")
    public ResponseEntity<PostsResponse> addCategoryToPost(@Valid @RequestBody CategoryRequest categoryRequest,
                                                           @PathVariable Long postId){
        return new ResponseEntity<>(postServiceImpl.addCategoryToPost(postId,categoryRequest), HttpStatus.OK);
    }

    // Remove Categories From Post
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/posts/categories{postId}")
    public ResponseEntity<PostsResponse> removeCategoryFromPost(@PathVariable Long postId,
                                                                @RequestParam("categoryName") String categoryName){
        return new ResponseEntity<>(postServiceImpl.removeCategoryFromPost(postId,categoryName), HttpStatus.OK);
    }

    // View All Posts
    @GetMapping("/posts")
    public ResponseEntity<PageResponse> getAllPosts(
            @RequestParam(value = "pageNo",defaultValue = PageConstant.DEFAULT_PAGE_NO,required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = PageConstant.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = PageConstant.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = PageConstant.DEFAULT_SORT_DIR, required = false) String sortDir){
        return new ResponseEntity<>(postServiceImpl.getAllPost(pageNo,pageSize,sortBy,sortDir),HttpStatus.OK);
    }

    // View All Post By Category
    @GetMapping("/posts/categories")
    public ResponseEntity<PageResponse> getAllPostsByCategory(
            @RequestParam(value = "pageNo",defaultValue = PageConstant.DEFAULT_PAGE_NO,required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = PageConstant.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = PageConstant.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = PageConstant.DEFAULT_SORT_DIR, required = false) String sortDir,
            @RequestParam("categoryName") String categoryName){
        return new ResponseEntity<>(postServiceImpl.getAllPostByCategory(pageNo,pageSize,sortBy,sortDir,categoryName),HttpStatus.OK);
    }

    // View All Post by a particular User
    @GetMapping("/posts/users/{userId}")
    public ResponseEntity<PageResponse> getAllPostsByUser(
            @RequestParam(value = "pageNo",defaultValue = PageConstant.DEFAULT_PAGE_NO,required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = PageConstant.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = PageConstant.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = PageConstant.DEFAULT_SORT_DIR, required = false) String sortDir,
            @PathVariable Long userId){
        getLoggedInUser();
        return new ResponseEntity<>(postServiceImpl.getAllPostByUser(pageNo,pageSize,sortBy,sortDir,userId),HttpStatus.OK);
    }

    // View A Single Post
    @GetMapping("/posts/post/{postId}")
    public ResponseEntity<PostsResponse> getSinglePost(@PathVariable Long postId){
        return new ResponseEntity<>(postServiceImpl.getSinglePost(postId), HttpStatus.OK);
    }

    // Edit a post title, description or category
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostsResponse> editUserPost(@PathVariable Long postId,
                                                      @Valid @RequestBody PostRequest postRequest){
        return new ResponseEntity<>(postServiceImpl.editUserPosts(postId,postRequest),HttpStatus.OK);
    }

    // Delete A post
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId){
        postServiceImpl.userDeletePost(postId);
        return new ResponseEntity<>("Post with id ("+postId+") deleted successfully", HttpStatus.OK);
    }

    // Like function

    // Like a post
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostsResponse> likePost(@PathVariable Long postId) {
        return new ResponseEntity<>(likesServiceImpl.likePost(postId), HttpStatus.OK);
    }

    // Unlike a post
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("posts/{postId}/unlike")
    public ResponseEntity<PostsResponse> unlikePost(@PathVariable Long postId){
        return new ResponseEntity<>(likesServiceImpl.unlikePost(postId), HttpStatus.OK);
    }

    // view all post likes
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<PageResponse> viewAllLikes(
            @RequestParam(value = "pageNo",defaultValue = PageConstant.DEFAULT_PAGE_NO,required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = PageConstant.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = PageConstant.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = PageConstant.DEFAULT_SORT_DIR, required = false) String sortDir,
            @PathVariable Long postId) {
        return new ResponseEntity<>(likesServiceImpl.getAllLikes(pageNo, pageSize, sortBy, sortDir, postId), HttpStatus.OK);
    }

    // Comment Function

    // Create a new comment
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<PostsResponse> createComment(@PathVariable Long postId, @RequestBody CommentRequest commentRequest){
        return new ResponseEntity<>(commentServiceImpl.createNewComment(postId,commentRequest),HttpStatus.CREATED);
    }

    // Delete Comment
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<PostsResponse> deleteComment(@PathVariable Long postId, @PathVariable Long commentId){
        return new ResponseEntity<>(commentServiceImpl.deleteComment(postId,commentId), HttpStatus.OK);
    }

    // Edit Comment
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping("/posts/comments/{commentId}/edit")
    public ResponseEntity<CommentResponse> editComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest){
        return new ResponseEntity<>(commentServiceImpl.editComment(commentId,commentRequest), HttpStatus.OK);
    }

    // View Comments
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<PageResponse> viewComments(
            @RequestParam(value = "pageNo",defaultValue = PageConstant.DEFAULT_PAGE_NO,required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = PageConstant.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = PageConstant.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = PageConstant.DEFAULT_SORT_DIR, required = false) String sortDir,
            @PathVariable Long postId){
        return new ResponseEntity<>(commentServiceImpl.viewComments(pageNo, pageSize, sortBy, sortDir, postId), HttpStatus.OK);
    }
}

package com.blog.controller;

import com.blog.constant.PageConstant;
import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.CategoryResponse;
import com.blog.dto.response.PageResponse;
import com.blog.service.impl.UserServiceImpl;
import com.blog.service.impl.CategoryServiceImpl;
import com.blog.service.impl.PostServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/superAdmin")
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
@RequiredArgsConstructor
public class SuperAdminController {
    private final CategoryServiceImpl categoryServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final PostServiceImpl postServiceImpl;

    // Super Admin - User Functions

    // View All Users
    @GetMapping("/users")
    public ResponseEntity<PageResponse> getAllUsers(
            @RequestParam(value = "pageNo",defaultValue = PageConstant.DEFAULT_PAGE_NO, required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = PageConstant.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = PageConstant.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = PageConstant.DEFAULT_SORT_DIR,required = false) String sortDir){
        return new ResponseEntity<>(userServiceImpl.getAllUsers(pageNo,pageSize,sortBy,sortDir), HttpStatus.OK);
    }

    // View All Users By their status
    @GetMapping("/users/status")
    public ResponseEntity<PageResponse> getAllUsersByStatus(
            @RequestParam(value = "pageNo",defaultValue = PageConstant.DEFAULT_PAGE_NO,required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = PageConstant.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = PageConstant.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = PageConstant.DEFAULT_SORT_DIR, required = false) String sortDir,
            @RequestParam(value = "status") String status){
        return new ResponseEntity<>(userServiceImpl.getAllUsersByStatus(pageNo,pageSize,sortBy,sortDir,status), HttpStatus.OK);
    }
    // Delete A User
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        userServiceImpl.deleteUser(userId);
        return new ResponseEntity<>("Roles Deleted Successfully, Proceed to Delete User", HttpStatus.OK);
    }

    // Category Functions

    //Create New Category
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createNewCategory(@Valid @RequestBody CategoryRequest categoryRequest){
        return new ResponseEntity<>(categoryServiceImpl.createNewCategory(categoryRequest), HttpStatus.CREATED);
    }

    // Update Category
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                      @RequestBody CategoryRequest categoryRequest){
        return new ResponseEntity<>(categoryServiceImpl.updateCategoryName(categoryId,categoryRequest), HttpStatus.OK);
    }
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){
        categoryServiceImpl.deleteCategory(categoryId);
        return new ResponseEntity<>("Category with id ("+categoryId+") is deleted successfully",HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId){
        postServiceImpl.superDeletePost(postId);
        return new ResponseEntity<>("Post with id ("+postId+") is deleted successfully",HttpStatus.OK);
    }

}

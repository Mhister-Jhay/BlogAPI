package com.blog.service;

import com.blog.dto.response.UserResponse;
import com.blog.dto.response.PageResponse;

public interface UserService {
    void deleteUser(Long userId);

    PageResponse getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir);

    PageResponse getAllUsersByStatus(int pageNo, int pageSize, String sortBy, String sortDir, String status);


    UserResponse getSingleUser(Long userId);

    UserResponse editUserStatus(Long userId, String status);


    UserResponse findByEmail(String email);
}

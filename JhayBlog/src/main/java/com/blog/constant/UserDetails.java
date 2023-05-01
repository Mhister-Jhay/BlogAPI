package com.blog.constant;

import com.blog.exception.UnAuthorizedException;
import com.blog.model.Users;
import com.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public final class UserDetails {
    private static UserRepository userRepository;

    @Autowired
    public UserDetails(UserRepository userRepository) {
        UserDetails.userRepository = userRepository;
    }

    public static Users getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated())
        {
            String username = authentication.getName();
            System.out.println("Authorized user " +username);
            return userRepository.findByEmail(username).orElseThrow();
        } else {
            throw new UnAuthorizedException("No authentication provided");
        }
    }
}

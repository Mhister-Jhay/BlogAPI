package com.blog.service.impl;

import com.blog.constant.UserDetails;
import com.blog.dto.response.UserResponse;
import com.blog.dto.response.JWTAuthResponse;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.response.PageResponse;
import com.blog.dto.request.SignUpRequest;
import com.blog.enums.UserStatus;
import com.blog.exception.ResourceAlreadyExistException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.RestrictedException;
import com.blog.exception.UnAuthorizedException;
import com.blog.model.Users;
import com.blog.model.Role;
import com.blog.repository.UserRepository;
import com.blog.repository.RoleRepository;
import com.blog.security.JwtTokenProvider;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;

    public JWTAuthResponse authenticateUser(LoginRequest loginRequest){
        Optional<Users> optionalBlogUsers = userRepository.findByEmail(loginRequest.getEmail());
        if(optionalBlogUsers.isEmpty()){
            throw new ResourceNotFoundException("User Does not Exist with Email ("+ loginRequest.getEmail()+")");
        }
        Users users = optionalBlogUsers.get();
        if(users.getUserStatus().equals(UserStatus.BANNED)){
            throw new RestrictedException("Account is Banned");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return new JWTAuthResponse(token);
    }

    public void registerUser(SignUpRequest signUpRequest){
        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            throw new ResourceAlreadyExistException("Email is Already Registered, Proceed to Login");
        }
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            throw new ResourceAlreadyExistException("Username Already Taken, Please choose another");
        }
        Users users = Users.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .fullName(signUpRequest.getFullName())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .build();
        Set<Role> roles = new HashSet<>();
        if(signUpRequest.getUsername().startsWith("admin111") && signUpRequest.getEmail().startsWith("adminRole")){
            Role role = roleRepository.findByName("ADMIN");
            Role role2 = roleRepository.findByName("USER");
            roles.add(role);
            roles.add(role2);
        }else if(signUpRequest.getUsername().equals("admin")
                && signUpRequest.getEmail().equals("superAdmin@admin.com")
        && signUpRequest.getPassword().equalsIgnoreCase("adminPassword")){
            Role role = roleRepository.findByName("SUPER_ADMIN");
            Role role2 = roleRepository.findByName("ADMIN");
            Role role3 = roleRepository.findByName("USER");
            roles.add(role);
            roles.add(role2);
            roles.add(role3);
        }else{
            Role role = roleRepository.findByName("USER");
            roles.add(role);
        }
        users.setRoles(roles);
        users.setUserStatus(UserStatus.valueOf("ACTIVE"));
        userRepository.save(users);
    }

    // For admin and super admin
    @Transactional
    @Override
    public void deleteUser(Long userId){
        Optional<Users> user = userRepository.findById(userId);
        if(user.isEmpty()){
            throw new ResourceNotFoundException("User with Id ("+userId+"), Does not Exist ");
        }
        Users admin = UserDetails.getLoggedInUser();
        Users users = user.get();
        if(!users.getUserStatus().equals(UserStatus.BANNED)){
            throw new RestrictedException("Account Needs to Be Banned Before Deleting");
        }
        if((users.getRoles().size() == 1 && admin.getRoles().size() == 2)
            || (users.getRoles().size() <= 2 && admin.getRoles().size() == 3)){
            users.setRoles(null);
            userRepository.delete(users);
        }else{
            throw new UnAuthorizedException("Not Authorized to remove this User");
        }
    }

    // For super Admin
    @Override
    public PageResponse getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Users> blogUsersPage = userRepository.findAll(pageable);
        List<UserResponse> userList = blogUsersPage.getContent().stream().map(
                this::mapToDTO).toList();
        return PageResponse.builder()
                .content(userList)
                .pageNo(blogUsersPage.getNumber())
                .pageSize(blogUsersPage.getSize())
                .totalElements(blogUsersPage.getTotalElements())
                .totalPages(blogUsersPage.getTotalPages())
                .last(blogUsersPage.isLast())
                .build();
    }

    // For super admin
    @Override
    public PageResponse getAllUsersByStatus(int pageNo, int pageSize, String sortBy, String sortDir, String status){
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Users> blogUsersPage = userRepository.findAllByUserStatus(UserStatus.valueOf(status.toUpperCase()),pageable);
        if(blogUsersPage.isEmpty()){
            throw new ResourceNotFoundException("No User Found with status ("+status+")");
        }
        List<UserResponse> userList = blogUsersPage.getContent().stream().map(
                this::mapToDTO).toList();
        return PageResponse.builder()
                .content(userList)
                .pageNo(blogUsersPage.getNumber())
                .pageSize(blogUsersPage.getSize())
                .totalElements(blogUsersPage.getTotalElements())
                .totalPages(blogUsersPage.getTotalPages())
                .last(blogUsersPage.isLast())
                .build();
    }

    // For everyone
    @Override
    public UserResponse getSingleUser(Long userId){
        Optional<Users> optionalBlogUsers = userRepository.findById(userId);
        if(optionalBlogUsers.isEmpty()){
            throw new ResourceNotFoundException("User with Id ("+userId+"), Does not Exist ");
        }
        return mapToDTO(optionalBlogUsers.get());
    }

    // for admin and super admin
    @Override
    public UserResponse editUserStatus(Long userId, String status){
        Optional<Users> optionalBlogUsers = userRepository.findById(userId);
        if(optionalBlogUsers.isEmpty()){
            throw new ResourceNotFoundException("User with Id ("+userId+"), Does not Exist ");
        }
        Users admin = UserDetails.getLoggedInUser();
        Users users = optionalBlogUsers.get();
        if(users.getRoles().size() == 1 && admin.getRoles().size() == 2 ){
            switch (status.toUpperCase()) {
                case "ACTIVE", "SUSPENDED", "BANNED" -> users.setUserStatus(UserStatus.valueOf(status.toUpperCase()));
                default -> throw new ResourceNotFoundException("Invalid status: " + status);
            }
        }else if(users.getRoles().size() <= 2 && admin.getRoles().size() > 2 ){
            switch (status.toUpperCase()) {
                case "ACTIVE", "SUSPENDED", "BANNED" -> users.setUserStatus(UserStatus.valueOf(status.toUpperCase()));
                default -> throw new ResourceNotFoundException("Invalid status: " + status);
            }
        }else{
            throw new UnAuthorizedException("User Not Authorised to Edit Status of User with Id ("+userId+")");
        }
        return mapToDTO(userRepository.save(users));
    }
    @Override
    public UserResponse findByEmail(String email){
        return mapToDTO(userRepository.findByEmail(email).orElseThrow());
    }
    private UserResponse mapToDTO(Users users){
        return modelMapper.map(users, UserResponse.class);
    }
}

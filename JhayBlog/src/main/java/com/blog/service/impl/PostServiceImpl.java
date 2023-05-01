package com.blog.service.impl;

import com.blog.constant.UserDetails;
import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.PageResponse;
import com.blog.dto.response.PostsResponse;
import com.blog.dto.request.PostRequest;
import com.blog.enums.UserStatus;
import com.blog.exception.ResourceAlreadyExistException;
import com.blog.exception.UnAuthorizedException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.model.Users;
import com.blog.model.Category;
import com.blog.model.Posts;
import com.blog.repository.UserRepository;
import com.blog.repository.CategoryRepository;
import com.blog.repository.PostsRepository;
import com.blog.repository.RoleRepository;
import com.blog.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostsService {
    private final ModelMapper modelMapper;
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RoleRepository roleRepository;

    // for everyone

    @Override
    public PostsResponse createNewPost(PostRequest postRequest){
        Users users = UserDetails.getLoggedInUser();
        if(users.getUserStatus().equals(UserStatus.BANNED)) {
            throw new UnAuthorizedException("Account is Banned, Cannot make post");
        }
        if(postsRepository.existsByTitle(postRequest.getTitle())){
            throw new ResourceAlreadyExistException("Post Title Already Exist, Try Using a Different Title");
        }else{
            Posts posts = Posts.builder()
                    .caption(postRequest.getCaption())
                    .user(users)
                    .title(postRequest.getTitle())
                    .content(postRequest.getContent())
                    .build();
            posts.setLikeCount(0L);
            posts.setCommentCount(0L);
            return mapToPostDTO(postsRepository.save(posts));
        }
    }

    // For everyone

    @Override
    public PostsResponse addCategoryToPost(Long postId, CategoryRequest categoryRequest){
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Post with id ("+postId+") not found");
        }
        Posts posts = optionalPosts.get();
        Users users = UserDetails.getLoggedInUser();
        if(!Objects.equals(posts.getUser().getId(), users.getId())){
            throw new UnAuthorizedException("Post Belongs to Another User");
        }
        Set<Category> categories = posts.getCategories();
        if(categoryRepository.existsByName(categoryRequest.getName().toUpperCase())){
            Category category = categoryRepository.findByName(categoryRequest.getName().toUpperCase());
            categories.add(category);
        } else{
            throw new ResourceNotFoundException(categoryRequest.getName().toUpperCase()+" does not exist");
        }
        posts.setCategories(categories);
        return mapToPostDTO(postsRepository.save(posts));
    }

    // for everyone

    @Override
    public PostsResponse removeCategoryFromPost(Long postId, String categoryName){
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Post with id ("+postId+") not found");
        }
        Posts posts = optionalPosts.get();
        Users users = UserDetails.getLoggedInUser();
        if(!Objects.equals(posts.getUser().getId(), users.getId())){
            throw new UnAuthorizedException("Post Belongs to Another User");
        }
        Set<Category> categories = posts.getCategories();
        if(categoryRepository.existsByName(categoryName.toUpperCase())){
            Category category = categoryRepository.findByName(categoryName.toUpperCase());
            categories.remove(category);
        } else{
            throw new ResourceNotFoundException(categoryName.toUpperCase()+" does not exist");
        }
        posts.setCategories(categories);
        return mapToPostDTO(postsRepository.save(posts));
    }

    // For admin and super admin
    @Override
    public PostsResponse removeCategoryAdmin(Long postId, String categoryName){
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Post with id ("+postId+") not found");
        }
        Posts posts = optionalPosts.get();
        Set<Category> categories = posts.getCategories();
        if(categoryRepository.existsByName(categoryName.toUpperCase())){
            Category category = categoryRepository.findByName(categoryName.toUpperCase());
            categories.remove(category);
        } else{
            throw new ResourceNotFoundException(categoryName.toUpperCase()+" does not exist");
        }
        posts.setCategories(categories);
        return mapToPostDTO(postsRepository.save(posts));
    }


    // for everyone
    @Override
    public PageResponse getAllPost(int pageNo, int pageSize, String sortBy, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Posts> postsPage = postsRepository.findAll(pageable);
        List<PostsResponse> postsResponses = postsPage.getContent().stream().map(this::mapToPostDTO)
                .toList();
        return PageResponse.builder()
                .pageNo(postsPage.getNumber())
                .pageSize(postsPage.getSize())
                .totalPages(postsPage.getTotalPages())
                .totalElements(postsPage.getTotalElements())
                .content(postsResponses)
                .last(postsPage.isLast())
                .build();
    }

    // For everyone
    @Override
    public PageResponse getAllPostByUser(int pageNo, int pageSize, String sortBy, String sortDir, Long userId){
        Optional<Users> optionalBlogUsers = userRepository.findById(userId);
        if(optionalBlogUsers.isEmpty()){
            throw new ResourceNotFoundException("User with id ("+userId+") does not exist");
        }
        Users users = optionalBlogUsers.get();
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Posts> postsPage = postsRepository.findAllByUser(users,pageable);
        List<PostsResponse> postsResponses = postsPage.getContent().stream().map(this::mapToPostDTO)
                .toList();
        return PageResponse.builder()
                .pageNo(postsPage.getNumber())
                .pageSize(postsPage.getSize())
                .totalPages(postsPage.getTotalPages())
                .totalElements(postsPage.getTotalElements())
                .content(postsResponses)
                .last(postsPage.isLast())
                .build();
    }

    // For everyone
    @Override
    public PageResponse getAllPostByCategory(int pageNo, int pageSize, String sortBy, String sortDir, String categoryName){
        Optional<Category> optionalCategory = categoryRepository.findCategoryByName(categoryName.toUpperCase());
        if(optionalCategory.isEmpty()){
            throw new ResourceNotFoundException("Category with name ("+categoryName+") does not exist");
        }
        Category category = optionalCategory.get();
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Posts> postsPage = postsRepository.findAllByCategoriesContaining(category,pageable);
        List<PostsResponse> postsResponses = postsPage.getContent().stream().map(this::mapToPostDTO)
                .toList();
        return PageResponse.builder()
                .pageNo(postsPage.getNumber())
                .pageSize(postsPage.getSize())
                .totalPages(postsPage.getTotalPages())
                .totalElements(postsPage.getTotalElements())
                .content(postsResponses)
                .last(postsPage.isLast())
                .build();
    }

    // For everyone
    @Override
    public PostsResponse getSinglePost(Long postId){
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Post with id ("+postId+") does not exist");
        }
        return mapToPostDTO(optionalPosts.get());
    }

    // For everyone
    @Override
    public PostsResponse editUserPosts(Long postId, PostRequest postRequest){
        Users users = UserDetails.getLoggedInUser();
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Post with id ("+postId+") does not exist");
        }
        if(!Objects.equals(optionalPosts.get().getUser().getId(), users.getId())){
            throw new UnAuthorizedException("User is Unauthorized to Edit Post, Post created by another User");
        }
        Posts posts = optionalPosts.get();
        posts.setTitle(postRequest.getTitle());
        posts.setCaption(postRequest.getCaption());
        posts.setContent(postRequest.getContent());

        return mapToPostDTO(postsRepository.save(posts));
    }

    // for everyone
    @Transactional
    @Override
    public void userDeletePost(Long postId){
        Users users = UserDetails.getLoggedInUser();
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Post with id ("+postId+") does not exist");
        }
        if(!Objects.equals(optionalPosts.get().getUser().getId(), users.getId())){
            throw new UnAuthorizedException("User is Unauthorized to Delete Post, Post created by another User");
        }
        Posts posts = optionalPosts.get();
        posts.setCategories(null);
        posts.setUser(null);
        postsRepository.deleteById(optionalPosts.get().getId());
    }

    // For admin and SuperAdmin
    @Override
    public void adminDeletePost(Long postId){
        Users users = UserDetails.getLoggedInUser();
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Post with id ("+postId+") does not exist");
        }
        if(!Objects.equals(optionalPosts.get().getUser().getId(), users.getId()) || users.getRoles().size() <= 1){
            throw new UnAuthorizedException("User is Unauthorized to Delete Post, Post created by another User");
        }
        postsRepository.deleteById(optionalPosts.get().getId());
    }
    @Override
    public void superDeletePost(Long postId){
        Users users = UserDetails.getLoggedInUser();
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Post with id ("+postId+") does not exist");
        }
        if(!Objects.equals(optionalPosts.get().getUser().getId(), users.getId()) || users.getRoles().size() <= 2){
            throw new UnAuthorizedException("User is Unauthorized to Delete Post, Post created by another User");
        }
        postsRepository.deleteById(optionalPosts.get().getId());
    }
    private PostsResponse mapToPostDTO(Posts posts){
        return modelMapper.map(posts, PostsResponse.class);
    }
}

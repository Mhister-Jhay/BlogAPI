package com.blog.service.impl;

import com.blog.constant.UserDetails;
import com.blog.dto.response.LikesResponse;
import com.blog.dto.response.PostsResponse;
import com.blog.dto.response.PageResponse;
import com.blog.exception.ResourceAlreadyExistException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.UnAuthorizedException;
import com.blog.model.Likes;
import com.blog.model.Posts;
import com.blog.model.Users;
import com.blog.repository.LikesRepository;
import com.blog.repository.PostsRepository;
import com.blog.repository.UserRepository;
import com.blog.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PostsRepository postsRepository;
    private final LikesRepository likesRepository;

    @Override
    public PostsResponse likePost(Long postId){
        Users users = UserDetails.getLoggedInUser();
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Posts with id ("+postId+") not found");
        }
        Posts posts= optionalPosts.get();
        if(likesRepository.existsByUserAndPost(users,posts)){
            throw new ResourceAlreadyExistException("Duplicate Likes Not Allowed");
        }else{
            Set<Likes> likes = posts.getLikes();
            Likes like = likesRepository.save(Likes.builder()
                    .post(posts)
                    .user(users)
                    .build());
            likes.add(like);
            posts.setLikes(likes);
            posts.setLikeCount(posts.getLikeCount()+1);
            postsRepository.save(posts);
            return mapToPostDTO(postsRepository.save(posts));
        }
    }
    @Transactional
    @Override
    public PostsResponse unlikePost(Long postId){
        Users users = UserDetails.getLoggedInUser();
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Posts with id ("+postId+") not found");
        }
        Posts posts= optionalPosts.get();
        Set<Likes> likes = posts.getLikes();
        if(likesRepository.existsByUserAndPost(users,posts)){
            Likes like = likesRepository.findByUserAndPost(users,posts);
            likes.remove(like);
            like.setPost(null);
            like.setUser(null);
            likesRepository.delete(like);
            posts.setLikes(likes);
            posts.setLikeCount(posts.getLikeCount() - 1);
            return mapToPostDTO(postsRepository.save(posts));
        }else{
            return mapToPostDTO(posts);
        }
    }

    @Override
    public PageResponse getAllLikes(int pageNo, int pageSize, String sortBy, String sortDir, Long postId){
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);

        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Posts with id ("+postId+") not found");
        }
        Posts posts= optionalPosts.get();
        Users users = UserDetails.getLoggedInUser();
        if(!posts.getUser().getId().equals(users.getId()) && posts.getUser().getRoles().size() == 1){
            throw new UnAuthorizedException("Accessed Denied, Likes hidden by post owner");
        }
        Page<Likes> likesPage = likesRepository.findAllByPost(posts,pageable);
        List<LikesResponse> likesResponses = likesPage.getContent().stream().map(this::mapToLikeResponse)
                .toList();
        return PageResponse.builder()
                .pageNo(likesPage.getNumber())
                .pageSize(likesPage.getSize())
                .totalPages(likesPage.getTotalPages())
                .totalElements(likesPage.getTotalElements())
                .content(likesResponses)
                .last(likesPage.isLast())
                .build();
    }

    private PostsResponse mapToPostDTO(Posts posts){
        return modelMapper.map(posts, PostsResponse.class);
    }
    private LikesResponse mapToLikeResponse(Likes likes){
        return modelMapper.map(likes, LikesResponse.class);
    }
}

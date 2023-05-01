package com.blog.service.impl;

import com.blog.constant.UserDetails;
import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.dto.response.PageResponse;
import com.blog.dto.response.PostsResponse;
import com.blog.exception.ResourceAlreadyExistException;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.UnAuthorizedException;
import com.blog.model.Comment;
import com.blog.model.Posts;
import com.blog.model.Users;
import com.blog.repository.CommentRepository;
import com.blog.repository.PostsRepository;
import com.blog.repository.UserRepository;
import com.blog.service.CommentService;
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
public class CommentServiceImpl implements CommentService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PostsRepository postsRepository;
    private final CommentRepository commentRepository;

    @Override
    public PostsResponse createNewComment(Long postId, CommentRequest commentRequest){
        Users users = UserDetails.getLoggedInUser();
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Posts with id ("+postId+") not found");
        }
        Posts posts= optionalPosts.get();
        Set<Comment> comments = posts.getComments();
        if(commentRepository.existsByUserAndPostAndBody(users,posts,commentRequest.getBody())){
            throw new ResourceAlreadyExistException("Duplicate Comments Not Allowed");
        }else{
            Comment comment = commentRepository.save(Comment.builder()
                    .post(posts)
                    .user(users)
                    .body(commentRequest.getBody())
                    .build());
            comments.add(comment);
            posts.setCommentCount(posts.getCommentCount()+1);
            posts.setComments(comments);
            postsRepository.save(posts);
            return mapToPostDTO(postsRepository.save(posts));
        }
    }
    @Transactional
    @Override
    public PostsResponse deleteComment(Long postId, Long commentId){
        Users users = UserDetails.getLoggedInUser();
        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if(optionalPosts.isEmpty()){
            throw new ResourceNotFoundException("Posts with id ("+postId+") not found");
        }
        Posts posts= optionalPosts.get();
        Set<Comment> comments = posts.getComments();
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if(optionalComment.isEmpty()){
            throw new ResourceNotFoundException("Comment with id ("+commentId+") not found");
        }
        Comment comment = optionalComment.get();
        if(!comment.getPost().equals(posts) && !comment.getUser().equals(users)){
            throw new UnAuthorizedException("Not Authorized to Delete, Comment made by Another User");
        }else{
            comments.remove(comment);
            comment.setPost(null);
            comment.setUser(null);
            commentRepository.delete(comment);
            posts.setComments(comments);
            posts.setCommentCount(posts.getCommentCount()-1);
            return mapToPostDTO(postsRepository.save(posts));
        }
    }

    @Override
    public CommentResponse editComment(Long commentId, CommentRequest commentRequest){
        Users users = UserDetails.getLoggedInUser();
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if(optionalComment.isEmpty()){
            throw new ResourceNotFoundException("Comment with id ("+commentId+") not found");
        }
        Comment comment = optionalComment.get();
        if(!comment.getUser().equals(users)){
            throw new UnAuthorizedException("Edit Access Denied,Comment was made by another User");
        }
        comment.setBody(commentRequest.getBody());
        return mapToCommentResponse(commentRepository.save(comment));
    }

    @Override
    public PageResponse viewComments(int pageNo, int pageSize, String sortBy, String sortDir, Long postId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Optional<Posts> optionalPosts = postsRepository.findById(postId);
        if (optionalPosts.isEmpty()) {
            throw new ResourceNotFoundException("Posts with id (" + postId + ") not found");
        }
        Posts posts = optionalPosts.get();
        Users users = UserDetails.getLoggedInUser();
        if (!posts.getUser().getId().equals(users.getId()) && posts.getUser().getRoles().size() == 1) {
            throw new UnAuthorizedException("Accessed Denied, Likes hidden by post owner");
        }
        Page<Comment> commentPage = commentRepository.findAllByPost(posts,pageable);
        List<CommentResponse> commentResponses = commentPage.getContent().stream().map(this::mapToCommentResponse)
                .toList();
        return PageResponse.builder()
                .pageNo(commentPage.getNumber())
                .pageSize(commentPage.getSize())
                .totalPages(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .content(commentResponses)
                .last(commentPage.isLast())
                .build();
    }
    private PostsResponse mapToPostDTO(Posts posts){
        return modelMapper.map(posts, PostsResponse.class);
    }
    private CommentResponse mapToCommentResponse(Comment comment){
        return modelMapper.map(comment, CommentResponse.class);
    }



}

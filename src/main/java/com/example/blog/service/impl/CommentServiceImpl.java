package com.example.blog.service.impl;

import com.example.blog.dto.CommentDto;
import com.example.blog.exception.BlogException;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ModelMapper mapper;

    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {
        log.info("creating new comment");
        Comment comment = mapToEntity(commentDto);

        // pobranie posta po id
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // ustawienie posta w komentarzu
        comment.setPost(post);

        // zapis nowego komentarza do bazy
        Comment newComment =  commentRepository.save(comment);

        return mapToDTO(newComment);
    }

    @Override
    @Cacheable(cacheNames = "commentsCache", key = "#postId")
    public List<CommentDto> getCommentsByPostId(long postId) {
        // pobranie komentarzy po id
        log.info("get list of comments by postId : {}", postId);
        List<Comment> comments = commentRepository.findByPostId(postId);

        // konwersja listy komentarzy modelu na listę komentarzy dtos
        return comments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {
        // pobranie posta po id
        log.info("get post by id : {}", postId);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // pobranie komentarza po id
        log.info("get comment by id : {}", commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment", "id", commentId));

        if(!comment.getPost().getId().equals(post.getId())){
            log.error("Comment id: {} does not belongs to post {}", commentId, postId);
            throw new BlogException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        return mapToDTO(comment);
    }

    @Override
    public CommentDto updateComment(Long postId, long commentId, CommentDto commentRequest) {
        // pobranie posta wg id
        log.info("get post to update by id : {}", postId);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // pobranie komentarza wg id
        log.info("get comment to update by id : {}", commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment", "id", commentId));

        if(!comment.getPost().getId().equals(post.getId())){
            log.error("Comment id: {} does not belongs to post {}", commentId, postId);
            throw new BlogException(HttpStatus.BAD_REQUEST, "Comment does not belongs to post");
        }

        comment.setName(commentRequest.getName());
        comment.setEmail(commentRequest.getEmail());
        comment.setContent(commentRequest.getContent());

        Comment updatedComment = commentRepository.save(comment);
        return mapToDTO(updatedComment);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        // pobranie posta po id
        log.info("get post to delete by id : {}", postId);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));

        // pobranie komentarza po id
        log.info("get comment to delete by id : {}", commentId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new ResourceNotFoundException("Comment", "id", commentId));

        if(!comment.getPost().getId().equals(post.getId())){
            log.error("Comment id: {} does not belongs to post {}", commentId, postId);
            throw new BlogException(HttpStatus.BAD_REQUEST, "Comment does not belongs to post");
        }

        commentRepository.delete(comment);
    }

    private CommentDto mapToDTO(Comment comment){
        return mapper.map(comment, CommentDto.class);
    }

    private Comment mapToEntity(CommentDto commentDto){
        return mapper.map(commentDto, Comment.class);
    }
}

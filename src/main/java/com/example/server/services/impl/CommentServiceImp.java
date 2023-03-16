package com.example.server.services.impl;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.models.*;
import com.example.server.repository.CommentLikeRepository;
import com.example.server.services.CommentService;
import com.example.server.payload.request.CommentRequestDto;
import com.example.server.payload.response.ResponseHandler;
import com.example.server.repository.CommentRepository;
import com.example.server.repository.PostRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final AuthenticatedUser authenticatedUser;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;

    public ResponseEntity<?> writeComment(HttpServletRequest request,
                                          CommentRequestDto commentDto
    ){
        Optional<User> currentUser = authenticatedUser.getCurrentUser(request);
        Optional<Post> curPost = postRepository.findById(commentDto.getPost_id());

        if(curPost.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,"post not found");
        }

        Comment comment = new Comment();
        comment.setAuthor(currentUser.get());
        comment.setText(commentDto.getText());

        curPost.get().getComments().add(comment);
        postRepository.save(curPost.get());

        return ResponseHandler.generateResponse("comment created Succefully",
                HttpStatus.CREATED,
                null);
    }

    private Comment getCommentById(Long commentId){
        Optional<Comment> comment =commentRepository.findById(commentId);
        if(comment.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,
                    "comment not found");
        }
        return comment.get();
    }

    @Override
    public Comment deleteComment(HttpServletRequest servletRequest, Long commentId) {

        Comment comment = checkUserAuthorization(servletRequest, commentId);
        commentRepository.deleteById(comment.getId());
        return comment;
    }

    @Override
    public Comment updateComment(HttpServletRequest servletRequest, Long commentId,String text){
        Comment comment = checkUserAuthorization(servletRequest, commentId);
        comment.setText(text);
        commentRepository.save(comment);

        return comment;
    }

    @Override
    @Transactional
    public CommentLike likeComment(HttpServletRequest request, Long commentId, byte like_type){
            if(like_type<=0||like_type>7){
                throw new CustomErrorException("Like Error (out of range)");
            }
            Optional<User> currUser  = authenticatedUser.getCurrentUser(request);

            Comment savedComment = this.getCommentById(commentId);

            CommentLike like = ifUserLikedComment(currUser.get().getId(), savedComment);

            if (like!=null){
                if(like.getType()==like_type){ // already like using same reaction ,remove it
                    removeLikeOnPost(currUser.get().getId(), commentId);
                }else{ //update like
                    like.setType(like_type);
                    commentLikeRepository.save(like);
                    return like;
                }
            }else {
                CommentLike newLike = new CommentLike();
                newLike.setLiker(currUser.get());
                newLike.setComment(savedComment);
                newLike.setType(like_type);

                commentLikeRepository.save(newLike);
                return newLike;
            }

            return new  CommentLike();
    }

    private void removeLikeOnPost(Long userId, Long commentId) {
        commentLikeRepository.deleteLikeOnComment(userId, commentId);
    }

    private CommentLike ifUserLikedComment(Long userId, Comment savedComment) {
        CommentLike like =  savedComment.getLikedComments()
                .stream()
                .filter(lik ->lik.getLiker().getId().equals(userId))
                .findAny().orElse(null);
        return like ;
    }

    private Comment checkUserAuthorization(HttpServletRequest servletRequest,Long commentId){
        Optional<User>author = authenticatedUser.getCurrentUser(servletRequest);
        Comment comment = this.getCommentById(commentId);

        if(!comment.getAuthor().getId().equals(author.get().getId())){
            throw new CustomErrorException(HttpStatus.FORBIDDEN,
                    "You are not authorized to delete this comment");
        }
        return comment;
    }
}

package com.example.server.services.impl;

import com.example.server.exceptions.CustomErrorException;
import com.example.server.models.*;
import com.example.server.payload.response.CommentsResponseDto;
import com.example.server.payload.response.UserResponceDto;
import com.example.server.repository.CommentLikeRepository;
import com.example.server.services.CommentService;
import com.example.server.payload.request.CommentRequestDto;
import com.example.server.payload.response.ResponseHandler;
import com.example.server.repository.CommentRepository;
import com.example.server.repository.PostRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final AuthenticatedUser authenticatedUser;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserService userService;

    public ResponseEntity<?> writeComment(HttpServletRequest request,
                                          CommentRequestDto commentDto
    ){
        User currentUser = userService.getCurrentAuthenticatedUser(request);
        //Optional<User> currentUser = authenticatedUser.getCurrentUser(request);
        Optional<Post> curPost = postRepository.findById((long)commentDto.getPost_id());

        if(curPost.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,"post not found");
        }

        Comment comment = new Comment();
        comment.setAuthor(currentUser);
        comment.setText(commentDto.getText());


        curPost.get().getComments().add(comment);
        postRepository.save(curPost.get());
        Comment savedComment = commentRepository.save(comment);

        CommentsResponseDto responseDto  = mapCommentToCommentResponce(comment);

        return ResponseHandler.generateResponse("comment created Succefully",
                HttpStatus.CREATED,
                savedComment);
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

        try {
            Comment savedComment = this.getCommentById(commentId);
            CommentLike commentLike = ifUserLikedComment(currUser.get().getId(), savedComment);

            if (commentLike!=null){
                if(commentLike.getType()==like_type){ // already like using same reaction ,remove it
                    removeLikeOnPost(currUser.get().getId(), commentId);
                    return new CommentLike();
                }else{ //update like
                    commentLike.setType(like_type);
                    commentLikeRepository.save(commentLike);
                    return commentLike;
                    // return  String.valueOf(like_type); //will return automatically
                }
            }else {
                CommentLike newLike = new CommentLike();
                newLike.setLiker(currUser.get());
                newLike.setComment(savedComment);
                newLike.setType(like_type);

                commentLikeRepository.save(newLike);
                return newLike;
            }

        }catch (Exception ex){
            throw new CustomErrorException(ex.getMessage());
        }
    }

    @Override
    public List<CommentsResponseDto> getAllCommentsOnPost(HttpServletRequest req, Long post_id) {

        Optional<Post> post = postRepository.findById(post_id);
        if(post.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND, "post "+post_id+" not found");
        }

        System.out.println("all comments: "+post.get().getComments().size());

        Set<Comment> comments = post.get().getComments();

        List<CommentsResponseDto> allcomments = new ArrayList<>();

        User user = null ;
        if(req!=null && req.getHeader("Authorization")!=null)
            user = userService.getCurrentAuthenticatedUser(req);

        for (Comment comment:comments) {
            CommentsResponseDto commentDto = mapCommentToCommentResponce(comment);

            if(req!=null && req.getHeader("Authorization")!=null){
                CommentLike cmntLike = ifUserLikedComment(user.getId(),comment);
                if(cmntLike!=null)
                    commentDto.setMyFeed(cmntLike.getType());
            }

           // System.out.println("herrrrrrrrrrrrrrrrrrr 1");
            Map<Byte, Long> likeTypeCount = new HashMap<>();
            for (CommentLike like_ : comment.getLikedComments()) {
                likeTypeCount.put(like_.getType(),
                        likeTypeCount.getOrDefault(like_.getType(), 0L) + 1L);
            }
          //  System.out.println("herrrrrrrrrrrrrrrrrrr 2");

            commentDto.setFeeds(likeTypeCount);

            allcomments.add(commentDto);
        }
        return allcomments;
    }

    private CommentsResponseDto mapCommentToCommentResponce(Comment comment){
        //map post to postDto
        CommentsResponseDto commentDto = new CommentsResponseDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setTimestamp(comment.getTimestamp());

        //create author dto
        UserResponceDto authorDto = mapUserToUserResponce(comment.getAuthor());

        //set Author
        commentDto.setAuthor(authorDto);

        return commentDto;
    }
    private UserResponceDto mapUserToUserResponce(User user){
        //create author dto
        UserResponceDto authorDto = new UserResponceDto();
        authorDto.setId(user.getId());
        authorDto.setUsername(user.getUsername());
        authorDto.setEmail(user.getEmail());
        authorDto.setImage_url(user.getProfile().getImage_url());

        return authorDto;
    }

    private CommentLike ifUserLikedComment(Long userId, Comment savedComment) {
            CommentLike commentLike =  savedComment.getLikedComments()
                    .stream()
                    .filter(lik ->lik.getLiker().getId().equals(userId))
                    .findAny().orElse(null);
            return  commentLike;
    }
    private void removeLikeOnPost(Long user_id, Long post_id){
        commentLikeRepository.deleteLikeOnComment(user_id,post_id);
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

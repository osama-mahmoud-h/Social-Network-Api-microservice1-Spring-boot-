package com.example.server.services.impl;


import com.example.server.exceptions.CustomErrorException;
import com.example.server.models.Comment;
import com.example.server.models.CommentReplay;
import com.example.server.models.User;
import com.example.server.repository.CommentRepository;
import com.example.server.repository.CommentsReplayRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.services.CommentsReplayService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentsReplayServiceImp implements CommentsReplayService {
    private final CommentRepository commentRepository;
    private final AuthenticatedUser authenticatedUser;
    private final CommentsReplayRepository commentsReplayRepository;

    @Override
    public CommentReplay replayComment(HttpServletRequest request, Long commentId, String text){
        Pair<Comment,User> commentUserPair = afterCheckUserAuthorizationOnComment(request,commentId);

        CommentReplay commentReplay = new CommentReplay();
        commentReplay.setComment(commentUserPair.getFirst());
        commentReplay.setAuthor(commentUserPair.getSecond());
        commentReplay.setText(text);
        commentsReplayRepository.save(commentReplay);

        return commentReplay;
    }

    @Override
    @Transactional
    public boolean deleteReplayOnComment(HttpServletRequest request, Long replayId){
        Pair<CommentReplay,User> replayUserPair = afterCheckUserAuthorizationOnReplay(request,replayId);
        commentsReplayRepository.deleteById(replayUserPair.getFirst().getId());
        return true;
    }

    @Override
    public CommentReplay updateReplayOnComment(HttpServletRequest request ,Long replayId , String text ){

        Pair<CommentReplay,User> replayUserPair = afterCheckUserAuthorizationOnReplay(request,replayId);

        replayUserPair.getFirst().setText(text);

        commentsReplayRepository.save(replayUserPair.getFirst());

        return replayUserPair.getFirst();
    }




    private Comment getCommentById(Long commentId){
        Optional<Comment> comment =commentRepository.findById(commentId);
        if(comment.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,
                    "comment not found");
        }
        return comment.get();
    }
    private CommentReplay getCommentReplayById(Long repalayId){
        Optional<CommentReplay> replay =commentsReplayRepository.findById(repalayId);
        if(replay.isEmpty()){
            throw new CustomErrorException(HttpStatus.NOT_FOUND,
                    "replay not found");
        }
        return replay.get();
    }
    private Pair<Comment,User> afterCheckUserAuthorizationOnComment(HttpServletRequest servletRequest, Long commentId){
        Optional<User>author = authenticatedUser.getCurrentUser(servletRequest);
        Comment comment = this.getCommentById(commentId);

        if(!comment.getAuthor().getId().equals(author.get().getId())){
            throw new CustomErrorException(HttpStatus.FORBIDDEN,
                    "You are not authorized to delete this comment");
        }
        return new Pair<>(comment,author.get());
    }

    private Pair<CommentReplay,User> afterCheckUserAuthorizationOnReplay(HttpServletRequest servletRequest, Long replayId){
        Optional<User>author = authenticatedUser.getCurrentUser(servletRequest);
        CommentReplay replay = this.getCommentReplayById(replayId);

        if(!replay.getAuthor().getId().equals(author.get().getId())){
            throw new CustomErrorException(HttpStatus.FORBIDDEN,
                    "You are not authorized to delete this replay");
        }
        return new Pair<>(replay,author.get());
    }
}

@Data
@AllArgsConstructor
class Pair<A, B> {
    private  A first;
    private  B second;
}

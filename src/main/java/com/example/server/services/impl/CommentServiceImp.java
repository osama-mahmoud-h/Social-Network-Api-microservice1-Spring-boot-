package com.example.server.services.impl;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.services.CommentService;
import com.example.server.models.Comment;
import com.example.server.models.Post;
import com.example.server.models.User;
import com.example.server.payload.request.CommentRequestDto;
import com.example.server.payload.response.ResponseHandler;
import com.example.server.repository.CommentRepository;
import com.example.server.repository.PostRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private final CommentRepository commentRepository;
    private final AuthenticatedUser authenticatedUser;
    private final PostRepository postRepository;

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
}

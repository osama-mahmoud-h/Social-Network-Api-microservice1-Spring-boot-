package com.example.server.utils;

import com.example.server.models.Comment;
import com.example.server.models.Post;
import com.example.server.models.User;
import com.example.server.payload.response.CommentsResponseDto;
import com.example.server.payload.response.PostResponceDto;
import com.example.server.payload.response.UserResponceDto;
import org.springframework.stereotype.Service;

@Service
public class ModelToDtoMapper {
    public PostResponceDto mapPostToPostResponce(Post post){
        //map post to postDto
        PostResponceDto  postResponceDto = new PostResponceDto();
        postResponceDto.setId(post.getId());
        postResponceDto.setText(post.getText());
        postResponceDto.setImages_url(post.getImages_url());
        postResponceDto.setVedio_url(post.getVedio_url());
        postResponceDto.setFile_url(post.getFile_url());
        //postResponceDto.setLikes(post.getLikesCount());
        //create author dto
        UserResponceDto authorDto = mapUserToUserResponce(post.getAuthor());

        //set Author
        postResponceDto.setAuthor(authorDto);

        return postResponceDto;
    }
    public CommentsResponseDto mapCommentToCommentResponce(Comment comment){
        //map post to postDto
        CommentsResponseDto commentDto = new CommentsResponseDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());

        //create author dto
        UserResponceDto authorDto = mapUserToUserResponce(comment.getAuthor());

        //set Author
        commentDto.setAuthor(authorDto);

        return commentDto;
    }

    public UserResponceDto mapUserToUserResponce(User user){
        //create author dto
        UserResponceDto authorDto = new UserResponceDto();
        authorDto.setId(user.getId());
        authorDto.setUsername(user.getUsername());
        authorDto.setEmail(user.getEmail());
        authorDto.setImage_url(user.getProfile().getImage_url());

        return authorDto;
    }
}

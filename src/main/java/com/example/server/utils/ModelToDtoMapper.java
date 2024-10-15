package com.example.server.utils;

import com.example.server.model.AppUser;
import com.example.server.model.Comment;
import com.example.server.model.Post;
import com.example.server.dto.response.CommentsResponseDto;
import com.example.server.dto.response.PostResponceDto;
import com.example.server.dto.response.AppUserResponseDto;
import org.springframework.stereotype.Service;

@Service
public class ModelToDtoMapper {
    public PostResponceDto mapPostToPostResponce(Post post){
//        //map post to postDto
//        PostResponceDto  postResponceDto = new PostResponceDto();
//        postResponceDto.setId(post.getId());
//        postResponceDto.setText(post.getText());
//        postResponceDto.setImages_url(post.getImages_url());
//        postResponceDto.setVedio_url(post.getVedio_url());
//        postResponceDto.setFile_url(post.getFile_url());
//        //postResponceDto.setLikes(post.getLikesCount());
//        //create author dto
//        AppUserResponseDto authorDto = mapUserToUserResponce(post.getAuthor());
//
//        //set Author
//        postResponceDto.setAuthor(authorDto);
//
//        return postResponceDto;
        return null;
    }
    public CommentsResponseDto mapCommentToCommentResponce(Comment comment){
//        //map post to postDto
//        CommentsResponseDto commentDto = new CommentsResponseDto();
//        commentDto.setId(comment.getId());
//        commentDto.setText(comment.getText());
//
//        //create author dto
//        AppUserResponseDto authorDto = mapUserToUserResponce(comment.getAuthor());
//
//        //set Author
//        commentDto.setAuthor(authorDto);
//
//        return commentDto;
        return null;
    }

    public AppUserResponseDto mapUserToUserResponce(AppUser user){
//        //create author dto
//        AppUserResponseDto authorDto = new AppUserResponseDto();
//        authorDto.setId(user.getId());
//        authorDto.setUsername(user.getUsername());
//        authorDto.setEmail(user.getEmail());
//        authorDto.setImage_url(user.getProfile().getImage_url());
//
//        return authorDto;
        return null;
    }
}

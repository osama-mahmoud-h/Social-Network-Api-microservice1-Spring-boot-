package com.app.server.service.impl;

import com.app.server.model.*;
import com.app.server.dto.request.ProfileRequestDto;
import com.app.server.dto.response.CommentsResponseDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.AppUserResponseDto;
//import com.example.server.repository.FollowerRepository;
import com.app.server.repository.ProfileRepository;
import com.app.server.repository.AppUserRepository;
import com.app.server.service.ProfileService;
import com.app.server.service.UserService;
import com.app.server.utils.fileStorage.FilesStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;
   // private final AuthenticatedUser authenticatedUser;
    private final FilesStorageService filesStorageService;
    private final AppUserRepository appUserRepository;
   // private final FollowerRepository followerRepository;

    @Override
    public boolean uploadImage(HttpServletRequest httpServletRequest, MultipartFile image) {

        return false;
    }

    @Override
    public boolean updateBio(HttpServletRequest httpServletRequest, String bio) {

        return true;
    }

    @Override
    public boolean updateAbout(HttpServletRequest httpServletRequest, String about) {

        return true;
    }

    @Override
    public boolean updateEducation(HttpServletRequest httpServletRequest, String education) {
        return false;
    }

    @Override
    public boolean updateSkills(HttpServletRequest httpServletRequest, String[] skills) {
        return false;
    }

    @Override
    public List<Post> getUserPosts(HttpServletRequest servletRequest){
        return null;
    }

    @Override
    public List<Post> getUserStaredPosts(HttpServletRequest httpServletRequest){
        return null;
    }

    @Override
    public List<AppUserResponseDto> getFollowers(HttpServletRequest servletRequest){
        return null;
    }



    @Override
    public List<AppUserResponseDto> getFollowing(HttpServletRequest servletRequest){
        return null;
    }

    @Override
    @Transactional
    public String follow(HttpServletRequest servletRequest, Long user_id){
        return null;

    }

    @Override
    public boolean isFollowing(Long followerId, Long followedId){
        return false;
    }

    @Override
    public Profile getProfile(Long user_id) {
        return null;
    }

    @Override
    public Profile updateProfile(HttpServletRequest httpServletRequest,
                                 ProfileRequestDto profileDto
    ){
//        Optional<User> user  = authenticatedUser.getCurrentUser(httpServletRequest);
//
//        Profile profile= user.get().getProfile();
//
//        profile.setBio(profileDto.getBio());
//        profile.setAboutUser(profileDto.getAboutUser());
//       // profile.getSkills().addAll(profileDto.getSiklls());
//        profile.setEducation(profileDto.getEducation());
//
//        profileRepository.save(profile);

        return null;
    }
    
    @Override
    public List<PostResponseDto> allPosts(HttpServletRequest httpServletRequests){
//        Optional<User> user = authenticatedUser.getCurrentUser(httpServletRequests);
//       // getProfile(user.get().getId());
//        Set<Post>posts = user.get().getPosts();
//        List<PostResponceDto> allPosts = new ArrayList<>();
//        for (Post post : posts) {
//            PostResponceDto postDto = this.mapPostToPostResponce(post);
//            allPosts.add(postDto);
//        }
        return null;
    }



    private PostResponseDto mapPostToPostResponce(Post post){
//        //map post to postDto
//        PostResponceDto  postResponceDto = new PostResponceDto();
//        postResponceDto.setId(post.getId());
//        postResponceDto.setText(post.getText());
//        postResponceDto.setImages_url(post.getImages_url());
//        postResponceDto.setVedio_url(post.getVedio_url());
//        postResponceDto.setFile_url(post.getFile_url());
//      //  postResponceDto.setLikes(post.getLikesCount());
//        //create author dto
//        AppUserResponseDto authorDto = mapUserToUserResponce(post.getAuthor());
//
//        //set Author
//        postResponceDto.setAuthor(authorDto);

        return null;
    }
    private CommentsResponseDto mapCommentToCommentResponce(Comment comment){
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

    private AppUserResponseDto mapUserToUserResponce(User user){
        //create author dto
//        AppUserResponseDto authorDto = new AppUserResponseDto();
//        authorDto.setId(user.getId());
//        authorDto.setUsername(user.getUsername());
//        authorDto.setEmail(user.getEmail());
//        authorDto.setImage_url(user.getProfile().getImage_url());

        return null;
    }



}

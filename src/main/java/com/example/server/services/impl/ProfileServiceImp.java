package com.example.server.services.impl;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.models.*;
import com.example.server.payload.request.ProfileRequestDto;
import com.example.server.payload.response.CommentsResponseDto;
import com.example.server.payload.response.PostResponceDto;
import com.example.server.payload.response.UserResponceDto;
import com.example.server.repository.FollowerRepository;
import com.example.server.repository.ProfileRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.services.FilesStorageService;
import com.example.server.services.ProfileService;
import com.example.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    private final FilesStorageService filesStorageService;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    @Override
    public boolean uploadImage(HttpServletRequest httpServletRequest, MultipartFile image) {

        Optional<User> user = authenticatedUser.getCurrentUser(httpServletRequest);

        String image_url = "uploads/";
        if(!image.isEmpty()){
            if(!image.getContentType().startsWith("image")){
                throw new CustomErrorException("not valid image");
            }
            String randomString = String.valueOf(Math.random());
            image_url +=  randomString+image.getOriginalFilename();
            //upload image to server
            filesStorageService.save(image,randomString);

            Profile profile = getProfile(user.get().getId());
            profile.setImage_url(image_url);

            profileRepository.save(profile);
            return true;
        }

        return false;
    }

    @Override
    public boolean updateBio(HttpServletRequest httpServletRequest, String bio) {
        Optional<User> curUser = authenticatedUser.getCurrentUser(httpServletRequest);
        Profile profile = getProfile(curUser.get().getId());
        profile.setBio(bio);
        profileRepository.save(profile);
        return true;
    }

    @Override
    public boolean updateAbout(HttpServletRequest httpServletRequest, String about) {
        Optional<User> curUser = authenticatedUser.getCurrentUser(httpServletRequest);
        Profile profile = getProfile(curUser.get().getId());
        profile.setAboutUser(about);
        profileRepository.save(profile);
        return true;
    }

    @Override
    public boolean updateEducation(HttpServletRequest httpServletRequest, String education) {
        Optional<User> curUser = authenticatedUser.getCurrentUser(httpServletRequest);
        Profile profile = getProfile(curUser.get().getId());
        profile.setEducation(education);
        profileRepository.save(profile);
        return true;
    }

    @Override
    public boolean updateSkills(HttpServletRequest httpServletRequest, String[] skills) {
        Optional<User> curUser = authenticatedUser.getCurrentUser(httpServletRequest);
        Profile profile = getProfile(curUser.get().getId());

        String[] skillsArray = new String[skills.length];
        int index = 0;
        for (String skill : skills){
            skillsArray[index++] = skill;
            System.out.println("sikll: "+skill);
        }
        profile.setSkills(skillsArray);

        profileRepository.save(profile);
        return true;
    }

    @Override
    public List<Post> getUserPosts(HttpServletRequest servletRequest){
        Optional<User> curUser = authenticatedUser.getCurrentUser(servletRequest);
       // Collection<Post> posts = curUser.get().getPosts();
        return null;
    }

    @Override
    public List<Post> getUserStaredPosts(HttpServletRequest httpServletRequest){
        return null;
    }

    @Override
    public List<UserResponceDto> getFollowers(HttpServletRequest servletRequest){
        Optional<User> curUser = authenticatedUser.getCurrentUser(servletRequest);
        Set<Follower> followers = curUser.get().getFollowers();
        List<UserResponceDto> followerUsers = new ArrayList<>();

        for (Follower follower : followers) {
            UserResponceDto userDto = this.mapUserToUserResponce(follower.getFollower());
            followerUsers.add(userDto);
        }

      return followerUsers;
    }



    @Override
    public List<UserResponceDto> getFollowing(HttpServletRequest servletRequest){
        Optional<User> curUser = authenticatedUser.getCurrentUser(servletRequest);
        Set<Follower> following = curUser.get().getFollowing();
        List<UserResponceDto> followedUsers = new ArrayList<>();

        for (Follower follower : following) {
            UserResponceDto userDto = this.mapUserToUserResponce(follower.getFollowed());
            followedUsers.add(userDto);
        }
        return followedUsers;
    }

    @Override
    @Transactional
    public String follow(HttpServletRequest servletRequest, Long user_id){
        Optional<User> curUser = authenticatedUser.getCurrentUser(servletRequest);
        Optional<User> followed = userRepository.findUserById(user_id);

        if(followed.isEmpty()){
            throw new CustomErrorException("user not found");
        }
        String follow = "followed";

        if(isFollowing(curUser.get().getId(), followed.get().getId())){
            followerRepository.unFollow(curUser.get().getId(),followed.get().getId());
            follow = "unfollowed";
        }
        else {
            Follower follower = new Follower();

            follower.setFollower(curUser.get());
            follower.setFollowed(followed.get());

            followerRepository.save(follower);

        }
        return follow;

    }

    @Override
    public boolean isFollowing(Long followerId, Long followedId){
        return followerRepository.isFollow(followerId, followedId) !=null;
    }

    @Override
    public Profile getProfile(Long user_id) {
        Optional<User> user = userRepository.findUserById(user_id);
        Profile profile = user.get().getProfile();
        return profile;
    }

    @Override
    public Profile updateProfile(HttpServletRequest httpServletRequest,
                                 ProfileRequestDto profileDto
    ){
        Optional<User> user  = authenticatedUser.getCurrentUser(httpServletRequest);

        Profile profile= user.get().getProfile();

        profile.setBio(profileDto.getBio());
        profile.setAboutUser(profileDto.getAboutUser());
       // profile.getSkills().addAll(profileDto.getSiklls());
        profile.setEducation(profileDto.getEducation());

        profileRepository.save(profile);

        return profile;
    }
    
    @Override
    public List<PostResponceDto> allPosts(HttpServletRequest httpServletRequests){
        Optional<User> user = authenticatedUser.getCurrentUser(httpServletRequests);
       // getProfile(user.get().getId());
        Set<Post>posts = user.get().getPosts();
        List<PostResponceDto> allPosts = new ArrayList<>();
        for (Post post : posts) {
            PostResponceDto postDto = this.mapPostToPostResponce(post);
            allPosts.add(postDto);
        }
        return allPosts;
    }



    private PostResponceDto mapPostToPostResponce(Post post){
        //map post to postDto
        PostResponceDto  postResponceDto = new PostResponceDto();
        postResponceDto.setId(post.getId());
        postResponceDto.setText(post.getText());
        postResponceDto.setImages_url(post.getImages_url());
        postResponceDto.setVedio_url(post.getVedio_url());
        postResponceDto.setFile_url(post.getFile_url());
      //  postResponceDto.setLikes(post.getLikesCount());
        //create author dto
        UserResponceDto authorDto = mapUserToUserResponce(post.getAuthor());

        //set Author
        postResponceDto.setAuthor(authorDto);

        return postResponceDto;
    }
    private CommentsResponseDto mapCommentToCommentResponce(Comment comment){
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

    private UserResponceDto mapUserToUserResponce(User user){
        //create author dto
        UserResponceDto authorDto = new UserResponceDto();
        authorDto.setId(user.getId());
        authorDto.setUsername(user.getUsername());
        authorDto.setEmail(user.getEmail());
        authorDto.setImage_url(user.getProfile().getImage_url());

        return authorDto;
    }



}

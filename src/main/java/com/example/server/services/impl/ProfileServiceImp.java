package com.example.server.services.impl;

import com.example.server.exceptions.CustomErrorException;
import com.example.server.mappers.UserMapper;
import com.example.server.models.*;
import com.example.server.payload.request.profile.ContactInfoDto;
import com.example.server.payload.request.profile.EducationRequestDto;
import com.example.server.payload.request.profile.SocialRequestDto;
import com.example.server.payload.response.CommentsResponseDto;
import com.example.server.payload.response.PostResponceDto;
import com.example.server.payload.response.profile.ProfileResponseDto;
import com.example.server.payload.response.UserResponceDto;
import com.example.server.repository.FollowerRepository;
import com.example.server.repository.ProfileRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.services.FilesStorageService;
import com.example.server.services.PostService;
import com.example.server.services.ProfileService;
import com.example.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    private final FilesStorageService filesStorageService;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final PostService postService;
    private final UserMapper userMapper;

    @Override
    public String uploadImage(HttpServletRequest httpServletRequest, MultipartFile image) {

        Optional<User> user = authenticatedUser.getCurrentUser(httpServletRequest);

        String image_url = "uploads/images/";
        if(!image.isEmpty()){
            if(!image.getContentType().startsWith("image")){
                throw new CustomErrorException("not valid image");
            }
            UUID randomUUID = UUID.randomUUID();
            String randomString = randomUUID.toString();

            Optional<String> extension = getExtensionByStringHandling(image.getOriginalFilename());

            if(extension.isEmpty()){
                throw new CustomErrorException("not valid image");
            }

            image_url +=  randomString+"."+extension.get();

            //upload image to server
            filesStorageService.save(image,image_url);

            Profile profile = getProfile(user.get().getId());
            profile.setImage_url(image_url);

            profileRepository.save(profile);
            return image_url;
        }

        throw new CustomErrorException(HttpStatus.BAD_REQUEST,"empty image");
    }

    @Override
    public String uploadCoverImage(HttpServletRequest httpServletRequest, MultipartFile image) {

        Optional<User> user = authenticatedUser.getCurrentUser(httpServletRequest);

        String image_url = "uploads/images/";
        if(!image.isEmpty()){
            if(!image.getContentType().startsWith("image")){
                throw new CustomErrorException("not valid image");
            }

            UUID randomUUID = UUID.randomUUID();
            String randomString = randomUUID.toString();

            Optional<String> extension = getExtensionByStringHandling(image.getOriginalFilename());

            if(extension.isEmpty()){
                throw new CustomErrorException("not valid image");
            }

            image_url +=  randomString+"."+extension.get();

            //upload image to server
            filesStorageService.save(image,image_url);

            Profile profile = getProfile(user.get().getId());
            profile.setCoverImageUrl(image_url);

            profileRepository.save(profile);
            return image_url;
        }

        throw new CustomErrorException(HttpStatus.BAD_REQUEST,"empty image");
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
    public boolean updateEducation(HttpServletRequest httpServletRequest, EducationRequestDto education) {
        Optional<User> curUser = authenticatedUser.getCurrentUser(httpServletRequest);
        Profile profile = getProfile(curUser.get().getId());
        profile.setEducation(education);
        profileRepository.save(profile);
        return true;
    }


    @Override
    public boolean updateSkills(HttpServletRequest httpServletRequest, String newSkill) {
        Optional<User> curUser = authenticatedUser.getCurrentUser(httpServletRequest);
        Profile profile = getProfile(curUser.get().getId());

        if(profile.getSkills()==null){
            Set<String> newSkills = new HashSet<>();
            profile.setSkills(newSkills);
        }
        profile.getSkills().add(newSkill);

        profileRepository.save(profile);

        return true;
    }

//    @Override
//    public List<Post> getUserPosts(HttpServletRequest servletRequest){
//        Optional<User> curUser = authenticatedUser.getCurrentUser(servletRequest);
//       // Collection<Post> posts = curUser.get().getPosts();
//        return null;
//    }

    @Override
    public List<Post> getUserStaredPosts(HttpServletRequest httpServletRequest){
        return null;
    }

    @Override
    public List<UserResponceDto> getFollowers(Long userId ){
        User curUser = userService.getUserById(userId);
        Set<Follower> followers = curUser.getFollowers();
        List<UserResponceDto> followerUsers = new ArrayList<>();

        for (Follower follower : followers) {
            UserResponceDto userDto = this.mapUserToUserResponce(follower.getFollower());
            followerUsers.add(userDto);
        }

      return followerUsers;
    }



    @Override
    public List<UserResponceDto> getFollowing(Long userId){
        User curUser = userService.getUserById(userId);
        Set<Follower> following = curUser.getFollowing();
        List<UserResponceDto> followedUsers = new ArrayList<>();

        for (Follower follower : following) {
            UserResponceDto userDto = this.mapUserToUserResponce(follower.getFollowed());
            followedUsers.add(userDto);
        }
        return followedUsers;
    }

    @Override
    public Set<UserResponceDto> getFollowersAndFollowing(Long userId){
        Set<UserResponceDto>all = new HashSet<>();
        List<UserResponceDto> followers = getFollowers(userId);
        List<UserResponceDto> following = getFollowing(userId);

        all.addAll(followers);
        all.addAll(following);
        return all;
    }

    @Override
    @Transactional
    public String follow(HttpServletRequest servletRequest, Long user_id){
        Optional<User> curUser = authenticatedUser.getCurrentUser(servletRequest);
        Optional<User> followed = userRepository.findUserById(user_id);

        if(followed.isEmpty()){
            throw new CustomErrorException("user not found");
        }
        if(curUser.get().getId().equals(followed.get().getId())){
            throw new CustomErrorException("you cannot follow yourself");
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


    private Profile getProfile(Long user_id) {
        Optional<User> user = userRepository.findUserById(user_id);
        Profile profile = user.get().getProfile();
        return profile;
    }

    @Override
    public ProfileResponseDto getProfileDto(HttpServletRequest req,Long user_id){

        User user = userService.getUser(user_id);

        Profile profile = getProfile(user_id);

        //TODO: profile mapper
        //map profile to profileDto
        ProfileResponseDto profileDto = new ProfileResponseDto();

        profileDto.setId(profile.getId());
        profileDto.setUser(mapUserToUserResponce(user));
        profileDto.setBio(profile.getBio());
        profileDto.setUserPosts(allPosts(req,user_id));
        profileDto.setImage_url(profile.getImage_url());
        profileDto.setCoverImage_url(profile.getCoverImageUrl());

        //TODO: map education to educationResponseDto
        profileDto.setEducation( profile.getEducation());
        profileDto.setContactInfo(profileDto.getContactInfo());
        profileDto.setSkills(profile.getSkills());
        profileDto.setContactInfo(profile.getContactInfo());
        profileDto.setSocialLinks(profile.getLinks());

        return profileDto;
    }

//    @Override
//    public Profile updateProfile(HttpServletRequest httpServletRequest,
//                                 ProfileRequestDto profileDto
//    ){
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
//
//        return profile;
//    }
    
    @Override
    public List<PostResponceDto> allPosts(HttpServletRequest req, Long user_id){

        Optional<User> user = authenticatedUser.getCurrentUser(req);
       // getProfile(user.get().getId());
        Set<Post>posts = user.get().getPosts();

        List<PostResponceDto> allPosts = new ArrayList<>();
        for (Post post : posts) {
            PostResponceDto postDto = this.mapPostToPostResponce(post);

            if(req!=null && req.getHeader("Authorization")!=null ) { // for not athuenticated users
                PostLike like = postService.ifILikedThisPost(req, post.getId());
                postDto.setMyFeed(like.getType());
            }
            // map every reaction to its count on this post
            Map<Byte, Long> likeTypeCount = new HashMap<>();
            for (PostLike like_ : post.getLikedPosts()) {
                likeTypeCount.put(like_.getType(),
                        likeTypeCount.getOrDefault(like_.getType(), 0L) + 1L);
            }
            postDto.setFeeds(likeTypeCount);

            allPosts.add(postDto);
        }

        Collections.reverse(allPosts);
        return allPosts;
    }

    @Override
    public boolean addSocialLink(HttpServletRequest req, SocialRequestDto social){
        User user = userService.getCurrentAuthenticatedUser(req);
        Profile profile = getProfile(user.getId());

        Map<String,String>links = profile.getLinks();

        if(links==null){
            links = new HashMap<>();
        }
        links.put(social.getName(),social.getUrl());

        profile.setLinks(links);
        profileRepository.save(profile);

        return true;
    }

    @Override
    public boolean updateSocialLink(HttpServletRequest req, SocialRequestDto social){
        User user = userService.getCurrentAuthenticatedUser(req);
        Profile profile = getProfile(user.getId());

        if(profile.getLinks()==null){
            throw new CustomErrorException(HttpStatus.BAD_REQUEST,"social name doesn't exists");
        }
        profile.getLinks().put(social.getName(),social.getUrl());

        profileRepository.save(profile);

        return true;
    }

    @Override
    @Transactional
    public Boolean deleteSocial (HttpServletRequest req, String name){
        User user = userService.getCurrentAuthenticatedUser(req);
        Profile profile = getProfile(user.getId());

        if(profile.getLinks()==null || ! profile.getLinks().containsKey(name)){
             throw new CustomErrorException(HttpStatus.BAD_REQUEST,"social name doesn't exists");
        }

        profile.getLinks().remove(name);

        profileRepository.save(profile);

        return true;
    }

    @Override
    public ContactInfoDto updateContactInfo(HttpServletRequest req, ContactInfoDto contactDto){

        User user = userService.getCurrentAuthenticatedUser(req);

        Profile profile = getProfile(user.getId());

        profile.setContactInfo(contactDto);
        profileRepository.save(profile);

        return contactDto;
    }

    private PostResponceDto mapPostToPostResponce(Post post){
        //map post to postDto
        PostResponceDto  postResponceDto = new PostResponceDto();
        postResponceDto.setId(post.getId());
        postResponceDto.setText(post.getText());
        postResponceDto.setImages_url(post.getImages_url());
        postResponceDto.setVedio_url(post.getVedio_url());
        postResponceDto.setFile_url(post.getFile_url());
        postResponceDto.setTimestamp(post.getTimestamp());

        if(post.getComments()!=null){
            postResponceDto.setComments_count((long) post.getComments().size());
        }
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

    private Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }


}

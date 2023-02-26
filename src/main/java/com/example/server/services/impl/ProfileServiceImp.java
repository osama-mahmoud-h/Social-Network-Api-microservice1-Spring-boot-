package com.example.server.services.impl;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.models.Follower;
import com.example.server.models.Post;
import com.example.server.models.Profile;
import com.example.server.models.User;
import com.example.server.payload.request.ProfileRequestDto;
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
    public Set<Follower> getFollowers(HttpServletRequest servletRequest){
        Optional<User> curUser = authenticatedUser.getCurrentUser(servletRequest);
        Set<Follower> followers = curUser.get().getFollowers();

      return followers;
    }



    @Override
    public Set<Follower> getFollowing(HttpServletRequest servletRequest){
        Optional<User> curUser = authenticatedUser.getCurrentUser(servletRequest);
        Set<Follower> following = curUser.get().getFollowing();

        return following;
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




}

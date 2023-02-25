package com.example.server.services;

import com.example.server.models.Follower;
import com.example.server.models.Post;
import com.example.server.models.Profile;
import com.example.server.models.User;
import com.example.server.payload.request.ProfileRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Service
public interface ProfileService {

    boolean uploadImage(HttpServletRequest httpServletRequest, MultipartFile image);

     String updateBio(HttpServletRequest httpServletRequest, String bio);

     String updateAbout(HttpServletRequest httpServletRequest, String bio);

     String updateSkills(HttpServletRequest httpServletRequest, String bio) ;


     List<Post> getUserPosts(HttpServletRequest httpServletRequest);

     List<Post> getUserStaredPosts(HttpServletRequest httpServletRequest);


     Set<Follower> getFollowers(HttpServletRequest servletRequest);

     List<User> getFollowing(HttpServletRequest servletRequest);


    boolean follow(HttpServletRequest servletRequest, Long user_id);

    Profile getProfile(Long userid);

    Profile updateProfile(HttpServletRequest httpServletRequest, ProfileRequestDto profileDto);
}

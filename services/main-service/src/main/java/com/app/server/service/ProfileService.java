package com.app.server.service;

import com.app.server.model.Post;
import com.app.server.model.Profile;
import com.app.server.dto.request.ProfileRequestDto;
import com.app.server.dto.response.PostResponseDto;
import com.app.server.dto.response.AppUserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProfileService {

    boolean uploadImage(HttpServletRequest httpServletRequest, MultipartFile image);

     boolean updateBio(HttpServletRequest httpServletRequest, String bio);

     boolean updateAbout(HttpServletRequest httpServletRequest, String bio);

    boolean updateSkills(HttpServletRequest httpServletRequest, String[] skills);

    List<Post> getUserPosts(HttpServletRequest httpServletRequest);

     List<Post> getUserStaredPosts(HttpServletRequest httpServletRequest);

     List<AppUserResponseDto> getFollowers(HttpServletRequest servletRequest);

    List<AppUserResponseDto> getFollowing(HttpServletRequest servletRequest);

    String follow(HttpServletRequest servletRequest, Long user_id);

    boolean isFollowing(Long followerId, Long followedId);

    Profile getProfile(Long userid);

    Profile updateProfile(HttpServletRequest httpServletRequest, ProfileRequestDto profileDto);

    boolean updateEducation(HttpServletRequest httpServletRequest, String education);

    List<PostResponseDto> allPosts(HttpServletRequest httpServletRequests);
}

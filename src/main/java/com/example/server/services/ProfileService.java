package com.example.server.services;

import com.example.server.models.Post;
import com.example.server.payload.request.profile.ContactInfoDto;
import com.example.server.payload.request.profile.EducationRequestDto;
import com.example.server.payload.request.profile.SocialRequestDto;
import com.example.server.payload.response.PostResponceDto;
import com.example.server.payload.response.profile.ProfileResponseDto;
import com.example.server.payload.response.UserResponceDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Service
public interface ProfileService {

    String uploadImage(HttpServletRequest httpServletRequest, MultipartFile image);

    String uploadCoverImage(HttpServletRequest httpServletRequest, MultipartFile image);

    boolean updateBio(HttpServletRequest httpServletRequest, String bio);

     boolean updateAbout(HttpServletRequest httpServletRequest, String bio);

    boolean updateEducation(HttpServletRequest httpServletRequest, EducationRequestDto education);

    //List<Post> getUserPosts(HttpServletRequest httpServletRequest);

   // List<Post> getUserPosts(HttpServletRequest servletRequest);

    boolean updateSkills(HttpServletRequest httpServletRequest, String newSkill);

    List<Post> getUserStaredPosts(HttpServletRequest httpServletRequest);

    List<UserResponceDto> getFollowers(Long userId);

    List<UserResponceDto> getFollowing(Long userId);

    Set<UserResponceDto> getFollowersAndFollowing(Long userId);

    String follow(HttpServletRequest servletRequest, Long user_id);

    boolean isFollowing(Long followerId, Long followedId);

    //Profile getProfile(Long userid);

    ProfileResponseDto getProfileDto(HttpServletRequest req, Long user_id);

    //Profile updateProfile(HttpServletRequest httpServletRequest, ProfileRequestDto profileDto);

    //boolean updateEducation(HttpServletRequest httpServletRequest, String education);

   // List<PostResponceDto> allPosts(HttpServletRequest httpServletRequests);

    List<PostResponceDto> allPosts(
            HttpServletRequest req,
            Long user_id
    );

    boolean addSocialLink(HttpServletRequest req, SocialRequestDto social);

    boolean updateSocialLink(HttpServletRequest req, SocialRequestDto social);

    Boolean deleteSocial(HttpServletRequest req, String name);

    ContactInfoDto updateContactInfo(HttpServletRequest req, ContactInfoDto contactDto);
}

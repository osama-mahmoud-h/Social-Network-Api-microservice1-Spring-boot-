package com.example.server.services.impl;

import com.example.server.Exceptions.CustomErrorException;
import com.example.server.models.Profile;
import com.example.server.models.User;
import com.example.server.payload.request.ProfileRequestDto;
import com.example.server.repository.ProfileRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.services.FilesStorageService;
import com.example.server.services.ProfileService;
import com.example.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    private final FilesStorageService filesStorageService;

    @Override
    public boolean uploadImage(HttpServletRequest httpServletRequest, MultipartFile image) {

        Optional<User> user = authenticatedUser.getCurrentUser(httpServletRequest);

        String image_url = "uploads/images/";
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
    public String addBio(HttpServletRequest httpServletRequest, String bio) {
        return null;
    }

    @Override
    public String addAbout(HttpServletRequest httpServletRequest, String bio) {
        return null;
    }

    @Override
    public Profile getProfile(Long userid) {
        User user = userService.getUser(userid);

        Profile profile = user.getProfile();
       // profile.getUser().setPassword("");

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

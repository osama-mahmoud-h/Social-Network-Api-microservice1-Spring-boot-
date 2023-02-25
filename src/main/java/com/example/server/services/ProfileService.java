package com.example.server.services;

import com.example.server.models.Profile;
import com.example.server.payload.request.ProfileRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Service
public interface ProfileService {

    boolean uploadImage(HttpServletRequest httpServletRequest, MultipartFile image);

    String addBio(HttpServletRequest httpServletRequest, String bio);

    String addAbout(HttpServletRequest httpServletRequest, String bio);

    Profile getProfile(Long userid);

    Profile updateProfile(HttpServletRequest httpServletRequest, ProfileRequestDto profileDto);
}

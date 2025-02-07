package com.app.server.controller;


import com.app.server.dto.response.ResponseHandler;
import com.app.server.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(HttpServletRequest httpServletRequest,
                                         @RequestParam("image")MultipartFile image
    ){
        boolean imageUloaded = profileService.uploadImage(httpServletRequest,image);
        return ResponseHandler.generateResponse(
                imageUloaded ? "image Uploaded successfully" : "error while uploading image",
                imageUloaded ? HttpStatus.OK : HttpStatus.BAD_REQUEST,
                imageUloaded ? true : false
        );
    }

    @PutMapping("/update/education")
    public ResponseEntity<?> updateEducation(HttpServletRequest httpServletRequest,
                                    @RequestParam String education
    ){
        return ResponseHandler.generateResponse("education updated successfully",
                HttpStatus.OK,
                profileService.updateEducation(httpServletRequest,education)
        );
    }

    @PutMapping("/update/about")
    public ResponseEntity<?> updateAbout(HttpServletRequest httpServletRequest,
                                    @RequestParam String about
    ){
        return ResponseHandler.generateResponse("about user updated successfully",
                HttpStatus.OK,
                profileService.updateAbout(httpServletRequest,about)
        );
    }

    @PutMapping("/update/bio")
    public ResponseEntity<?> updateBio(HttpServletRequest httpServletRequest,
                                    @RequestParam String bio
    ){
        return ResponseHandler.generateResponse("bio updated successfully",
                HttpStatus.OK,
                profileService.updateBio(httpServletRequest,bio)
        );
    }
    @PutMapping("/update/skills")
    public ResponseEntity<?> updateSkills(HttpServletRequest httpServletRequest,
                                       @RequestParam  String[] skills
    ){
        //System.out.println("siklls: "+ Arrays.toString(skills));
        return ResponseHandler.generateResponse("skills updated successfully",
                HttpStatus.OK,
                profileService.updateSkills(httpServletRequest,skills)
        );
    }

    @GetMapping("/{userid}")
    public ResponseEntity<?> getProfile(@PathVariable("userid") Long userId){
        return ResponseHandler.generateResponse("Profile get successfully",
                HttpStatus.OK,
                profileService.getProfile(userId)
        );
    }

    @GetMapping("/follow/{user_id}")
    public ResponseEntity<?> followUser(HttpServletRequest servletRequest,@PathVariable("user_id") Long userId){
        String follow = profileService.follow(servletRequest,userId);
        return ResponseHandler.generateResponse(
                "user with id : "+userId+" , has been "+follow+" successfully",
                HttpStatus.OK,
                null
        );
    }

    @GetMapping("/followers/get")
    public ResponseEntity<?> getFollowers(HttpServletRequest servletRequest){
        return ResponseHandler.generateResponse("followers get successfully",
                HttpStatus.OK,
                profileService.getFollowers(servletRequest));

    }

    @GetMapping("/following/get")
    public ResponseEntity<?> getFollowing(HttpServletRequest servletRequest){
        return ResponseHandler.generateResponse("following get successfully",
                HttpStatus.OK,
                profileService.getFollowing(servletRequest));

    }

    @GetMapping("posts/all")
    public ResponseEntity<?>getPosts(HttpServletRequest servletRequest){
        return ResponseHandler.generateResponse("posts get successfully",
                HttpStatus.OK,
                profileService.allPosts(servletRequest));
    }



}

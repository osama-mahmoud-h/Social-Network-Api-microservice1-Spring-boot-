package com.example.server.controllers;

import com.example.server.payload.request.profile.SocialRequestDto;
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.services.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

private final AuthenticatedUser authenticatedUser ;
private final TestService testService;

  @GetMapping( value = "/user")
  public String userAccess(HttpServletRequest request) {
    Map<?,?> userData = authenticatedUser.userData(request);
    return authenticatedUser.userData(request).toString();
  }

  @GetMapping("/mod")
  @PreAuthorize("hasRole('MODERATOR')")
  public String moderatorAccess() {
    return "Moderator Board.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }

  @PostMapping("/string")
  public ResponseEntity<?> getResponse(@RequestParam String val){
    System.out.println(val);
    return  ResponseEntity.ok().body(val);
  }

  @PostMapping("/name/save")
  public ResponseEntity<?> addTestName(@RequestBody SocialRequestDto names){
    return  ResponseEntity.ok().body(testService.saveName(names));
  }

  @PostMapping("/tag/save")
  public ResponseEntity<?> addTag(String name){

    return  ResponseEntity.ok().body(testService.addTag(name));
  }



}

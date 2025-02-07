package com.app.server.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

  @GetMapping( value = "/user")
  @PreAuthorize("hasRole('USER')")
  public String userAccess(@AuthenticationPrincipal UserDetails userDetails) {
//    Map<?,?> userData = authenticatedUser.userData(request);
//    return authenticatedUser.userData(request).toString();
    return "i am user " + userDetails.getUsername();
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
}

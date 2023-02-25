package com.example.server.controllers;

import com.example.server.security.jwt.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

@Autowired
private AuthenticatedUser authenticatedUser ;

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
}

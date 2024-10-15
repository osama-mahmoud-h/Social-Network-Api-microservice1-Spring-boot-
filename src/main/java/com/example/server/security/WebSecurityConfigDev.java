package com.example.server.security;

import com.example.server.security.jwt.impl.JwtAuthenticationFilter;
import com.example.server.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Profile("dev")
public class WebSecurityConfigDev {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AppUserService userService;


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
      .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(request -> request
                      // .requestMatchers("/**")
                      //  .permitAll()
                      .requestMatchers("/api/v1/auth/**").permitAll()
                      .requestMatchers("/uploads/**").permitAll()
                      .requestMatchers("/**").permitAll()
                      .anyRequest()
                      .authenticated()

              )
              .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authenticationProvider(this.authenticationProvider())
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userService.userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
          throws Exception {
    return config.getAuthenticationManager();
  }


}

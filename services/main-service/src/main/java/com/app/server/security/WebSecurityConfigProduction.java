package com.app.server.security;

import com.app.server.exception.CustomAccessDeniedHandler;
import com.app.server.exception.CustomAuthenticationEntryPoint;
import com.app.server.security.jwt.impl.JwtAuthenticationFilter;
import com.app.server.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
@Profile("production")
@RequiredArgsConstructor
public class WebSecurityConfigProduction {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AppUserService userService;


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
                                         CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                         CustomAccessDeniedHandler customAccessDeniedHandler
  ) throws Exception {
      http
      .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
              .authorizeHttpRequests(request -> request
                      .requestMatchers("/swagger-ui/**").hasRole("ADMIN")
                      .requestMatchers("/uploads/**").permitAll()
                      .requestMatchers("/**").permitAll()
                      .anyRequest()
                      .authenticated()

              )
              .exceptionHandling(exceptionHandling -> exceptionHandling
                      .authenticationEntryPoint(customAuthenticationEntryPoint)
                      .accessDeniedHandler(customAccessDeniedHandler)
              )
              .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authenticationProvider(this.authenticationProvider())
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();
  }

  @Bean
  public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
    return new CustomAuthenticationEntryPoint();
  }

  @Bean
  public CustomAccessDeniedHandler customAccessDeniedHandler() {
    return new CustomAccessDeniedHandler();
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
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return  http.getSharedObject(AuthenticationManagerBuilder.class)
            .build();
  }


}

package com.example.server.services;

import com.example.server.payload.request.profile.SocialRequestDto;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface TestService {
    SocialRequestDto[] saveName(SocialRequestDto name);

    Set<SocialRequestDto> addTag(String tag);
}

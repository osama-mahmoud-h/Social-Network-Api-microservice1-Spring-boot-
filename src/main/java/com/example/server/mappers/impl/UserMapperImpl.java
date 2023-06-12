package com.example.server.mappers.impl;

import com.example.server.mappers.UserMapper;
import com.example.server.models.User;
import com.example.server.payload.response.UserResponceDto;
import org.springframework.stereotype.Service;

@Service
public class UserMapperImpl implements UserMapper {
    
    @Override
    public UserResponceDto mapUserToUserResponseDto(User user){
        //create author dto
        UserResponceDto authorDto = new UserResponceDto();
        authorDto.setId(user.getId());
        authorDto.setUsername(user.getUsername());
        authorDto.setEmail(user.getEmail());
        authorDto.setImage_url(user.getProfile().getImage_url());

        return authorDto;
    }
}

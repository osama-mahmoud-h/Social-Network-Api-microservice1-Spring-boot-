package com.example.server.mappers;

import com.example.server.models.User;
import com.example.server.payload.response.UserResponceDto;
import org.springframework.stereotype.Service;

@Service
public interface UserMapper {
    UserResponceDto mapUserToUserResponseDto(User user);
}

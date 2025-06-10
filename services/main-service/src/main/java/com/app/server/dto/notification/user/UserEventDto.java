package com.app.server.dto.notification.user;

import com.app.server.enums.UserActionType;
import com.app.server.model.Profile;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEventDto implements Serializable {
    private UserActionType actionType;
    private Long userId;
    private String username;
    private String email;
    private String profileImageUrl;
    private Profile profile;

}

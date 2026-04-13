package com.app.server.dto.notification.friendship;

import com.app.server.enums.FriendshipActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendshipEventDto implements Serializable {

    private FriendshipActionType actionType;
    private Long userId1;
    private Long userId2;
}
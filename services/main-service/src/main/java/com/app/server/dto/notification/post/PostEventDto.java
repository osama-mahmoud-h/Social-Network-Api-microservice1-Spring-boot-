package com.app.server.dto.notification.post;

import com.app.server.dto.response.PostResponseDto;
import com.app.server.enums.PostActionType;
import com.app.server.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEventDto implements Serializable {
    private PostActionType actionType;
    private Long postId;
    private Post post;
}

package com.app.server.dto.notification.comment;

import com.app.server.enums.CommentActionType;
import com.app.server.model.Comment;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEventDto  implements Serializable {
    private CommentActionType actionType;
    private Long commentId;
    private Comment comment;
}

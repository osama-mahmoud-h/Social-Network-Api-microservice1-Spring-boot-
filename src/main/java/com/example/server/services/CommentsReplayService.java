package com.example.server.services;

import com.example.server.models.CommentReplay;
import com.example.server.payload.response.CommentReplayDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public interface CommentsReplayService {
    CommentReplay replayComment(HttpServletRequest request, Long commentId, String text);

    @Transactional
    boolean deleteReplayOnComment(HttpServletRequest request, Long replayId);

    List<CommentReplayDto> getAllRepliesOnComment(Long commentId);

    CommentReplay updateReplayOnComment(HttpServletRequest request, Long replayId, String text);
}

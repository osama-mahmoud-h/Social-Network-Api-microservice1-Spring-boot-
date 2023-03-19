package com.example.server.services;

import com.example.server.models.CommentReplay;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
public interface CommentsReplayService {
    CommentReplay replayComment(HttpServletRequest request, Long commentId, String text);

    @Transactional
    boolean deleteReplayOnComment(HttpServletRequest request, Long replayId);

    CommentReplay updateReplayOnComment(HttpServletRequest request, Long replayId, String text);
}

package com.app.server.exception;

import org.springframework.http.HttpStatus;

/**
 * A comment operation the domain refuses.
 *
 * <p>Two shapes, two statuses. Acting on someone else's comment is 403, not the 404 the old
 * author-scoped queries returned — answering "Comment not found" for a comment that plainly exists
 * is what hid the swapped-argument delete bug for so long. A malformed request (empty content,
 * replying to a reply) stays 400.
 */
public class IllegalCommentOperation extends CustomRuntimeException {

    private IllegalCommentOperation(String message, HttpStatus status) {
        super(message, status);
    }

    public static IllegalCommentOperation notTheAuthor(String action) {
        return new IllegalCommentOperation(
                "Only the author can " + action + " this comment", HttpStatus.FORBIDDEN);
    }

    public static IllegalCommentOperation rejected(String message) {
        return new IllegalCommentOperation(message, HttpStatus.BAD_REQUEST);
    }
}
package com.app.server.exception;

import org.springframework.http.HttpStatus;

/**
 * Raised when a friendship transition is refused — either the friendship is not in a status
 * that allows the action, or the actor does not hold the role the action requires.
 *
 * <p>409 rather than 404: the old code answered "Friend request not found" for what were really
 * illegal transitions, which hid both bugs this aggregate fixes.
 */
public class IllegalFriendshipTransition extends CustomRuntimeException {

    public IllegalFriendshipTransition(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
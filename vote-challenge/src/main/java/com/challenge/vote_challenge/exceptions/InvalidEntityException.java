package com.challenge.vote_challenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when there is a wrong parameter with the object (usually an invalid parameter in the logic)
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEntityException extends RuntimeException {
    public InvalidEntityException(String message) {
        super(message);
    }
}

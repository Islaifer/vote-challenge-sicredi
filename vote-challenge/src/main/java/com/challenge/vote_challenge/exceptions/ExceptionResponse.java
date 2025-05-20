package com.challenge.vote_challenge.exceptions;

import java.time.LocalDateTime;

public record ExceptionResponse(LocalDateTime timestamp, String message, String details) {}

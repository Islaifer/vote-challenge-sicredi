package com.challenge.vote_challenge.exceptions.handler;

import com.challenge.vote_challenge.exceptions.ExceptionResponse;
import com.challenge.vote_challenge.exceptions.InvalidEntityException;
import com.challenge.vote_challenge.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handlerAllExceptions(Exception ex, WebRequest request){
        return getResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handlerNotFoundExceptions(Exception ex, WebRequest request){
        return getResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public final ResponseEntity<ExceptionResponse> handlerInvalidCpfExceptions(Exception ex, WebRequest request){
        return getResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ExceptionResponse> getResponse(Exception ex, WebRequest request, HttpStatus status){
        ExceptionResponse response = new ExceptionResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(response, status);
    }
}

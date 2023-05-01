package com.blog.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<Object> validationException(MethodArgumentNotValidException e,
                                                      HttpServletRequest request){
        Map<String, String> invalidErrors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            invalidErrors.put(fieldName,message);
        });
        invalidErrors.put("path",request.getRequestURI());
        invalidErrors.put("errorTime", LocalDateTime.now().toString());
     return new ResponseEntity<>(invalidErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> notFoundException(ResourceNotFoundException e,
                                                               HttpServletRequest request){
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .errorTime(LocalDateTime.now())
                .errorPath(request.getRequestURI())
                .errorMessage(e.getMessage())
                .errorStatusCode(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(exceptionResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> alreadyExistException(ResourceAlreadyExistException e,
                                                                   HttpServletRequest request){
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .errorTime(LocalDateTime.now())
                .errorPath(request.getRequestURI())
                .errorMessage(e.getMessage())
                .errorStatusCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(exceptionResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> mismatchException(UnAuthorizedException e,
                                                               HttpServletRequest request){
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .errorTime(LocalDateTime.now())
                .errorPath(request.getRequestURI())
                .errorMessage(e.getMessage())
                .errorStatusCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(exceptionResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> restrictedException(RestrictedException e,
                                                               HttpServletRequest request){
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .errorTime(LocalDateTime.now())
                .errorPath(request.getRequestURI())
                .errorMessage(e.getMessage())
                .errorStatusCode(HttpStatus.UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(exceptionResponse,HttpStatus.UNAUTHORIZED);
    }
}

package com.example.blog.exception;

import org.springframework.http.HttpStatus;

public class BlogException extends RuntimeException {

    private HttpStatus status;
    private String message;

    public BlogException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

//    public BlogException(String message, HttpStatus status, String message1) {
//        super(message);
//        this.status = status;
//        this.message = message1;
//    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

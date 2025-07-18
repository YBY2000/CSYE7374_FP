package com.project.platform.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom Exception
 */
@Getter
public class CustomException extends RuntimeException {

    private HttpStatus httpStatus;

    public CustomException(HttpStatus httpStatus, String msg) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public CustomException(String msg) {
        super(msg);
        this.httpStatus = HttpStatus.CONFLICT;
    }

}

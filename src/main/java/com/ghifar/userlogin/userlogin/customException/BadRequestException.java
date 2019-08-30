package com.ghifar.userlogin.userlogin.customException;

/*
 * this class is for throwing it there are any request is not valid or unexpected errors occurs.
 * we'll respond it with HTTP status code using @ResponseStatus
 * */
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.ghifar.userlogin.userlogin.customException;
/*
* this class is for throwing it there are any request is not valid or unexpected errors occurs.
* we'll respond it with HTTP status code using @ResponseStatus
* */
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AppException extends RuntimeException {

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}

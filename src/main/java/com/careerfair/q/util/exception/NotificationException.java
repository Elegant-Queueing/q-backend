package com.careerfair.q.util.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NotificationException extends RuntimeException {

    private final String message;
}

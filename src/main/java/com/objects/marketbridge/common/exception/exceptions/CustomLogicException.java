package com.objects.marketbridge.common.exception.exceptions;

import com.objects.marketbridge.common.utils.DateTimeHolder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@NoArgsConstructor
@Getter
public class CustomLogicException extends RuntimeException {

    private HttpStatus httpStatus;
    private ErrorCode errorCode;
    private LocalDateTime timestamp;

    @Builder
    private CustomLogicException(String message, ErrorCode errorCode,HttpStatus httpStatus, LocalDateTime timestamp) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }

    public CustomLogicException(String message) {
        super(message);
    }
    @Builder(builderMethodName = "builderWithCause")
    private CustomLogicException(String message, Throwable cause, ErrorCode errorCode,HttpStatus httpStatus, LocalDateTime timestamp) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }


    public static CustomLogicException createBadRequestError(ErrorCode errorCode, LocalDateTime timestamp) {
        return CustomLogicException.builder()
                .httpStatus(BAD_REQUEST)
                .errorCode(errorCode)
                .message(errorCode.getMessage())
                .timestamp(timestamp)
                .build();
    }

    public static CustomLogicException createBadRequestError(ErrorCode errorCode) {
        return createBadRequestError(errorCode, LocalDateTime.now());
    }

    public static CustomLogicException createBadRequestError(ErrorCode errorCode, String message, LocalDateTime timestamp) {
        return CustomLogicException.builder()
                .httpStatus(BAD_REQUEST)
                .errorCode(errorCode)
                .message(message)
                .timestamp(timestamp)
                .build();
    }

    public static CustomLogicException createBadRequestError(ErrorCode errorCode, String message) {
        return CustomLogicException.builder()
                .httpStatus(BAD_REQUEST)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public static CustomLogicException createBadRequestError(ErrorCode errorCode, DateTimeHolder dateTimeHolder) {
        return createBadRequestError(errorCode, dateTimeHolder.getTimeNow());
    }
}

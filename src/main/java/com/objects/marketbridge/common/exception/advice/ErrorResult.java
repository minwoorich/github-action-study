package com.objects.marketbridge.common.exception.advice;

import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ErrorResult {

    private int code;
    private HttpStatus status;
    private String path;
    private ErrorCode errorCode;
    private String message;
    private LocalDateTime timestamp;
    private String className;
    private String methodName;
    private String exceptionName;
    private StackTraceElement[] elements;

    @Builder
    private ErrorResult(int code, HttpStatus status, String path, ErrorCode errorCode, String message, LocalDateTime timestamp, String className, String methodName, String exceptionName, StackTraceElement[] elements) {
        this.code = code;
        this.status = status;
        this.path = path;
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = timestamp;
        this.className = className;
        this.methodName = methodName;
        this.exceptionName = exceptionName;
        this.elements = elements;
    }

    public ErrorResult.Response toResponse() {
        return Response.builder()
                .code(code)
                .status(status)
                .path(path)
                .errorCode(errorCode)
                .message(message)
                .timestamp(timestamp)
                .build();
    }

    @Getter
    @NoArgsConstructor
    public static class Response{
        private int code;
        private HttpStatus status;
        private String path;
        private ErrorCode errorCode;
        private String message;
        private LocalDateTime timestamp;

        @Builder
        private Response(int code, HttpStatus status, String path, ErrorCode errorCode, String message, LocalDateTime timestamp) {
            this.code = code;
            this.status = status;
            this.path = path;
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}

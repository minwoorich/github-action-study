package com.objects.marketbridge.common.security.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@ToString
public class ErrRes {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final int code;
    private final String status;
    private final String path;
    private final ErrorCode errorCode;
    private final String message;
    private final String timeStamp;

    @Builder(access = AccessLevel.PRIVATE)
    public ErrRes(int code, String status, String path, ErrorCode errorCode, String message) {
        this.code = code;
        this.status = status;
        this.path = path;
        this.errorCode = errorCode;
        this.message = message;
        this.timeStamp = (LocalDateTime.now()).toString();
    }

    public static ErrRes of(int code, String status, String message, String path, ErrorCode errorCode) {
        return ErrRes.builder()
                .code(code)
                .status(status)
                .path(path)
                .errorCode(errorCode)
                .message(message)
                .build();
    }
    public String convertToJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}

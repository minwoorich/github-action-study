package com.objects.marketbridge.common.exception.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.objects.marketbridge.common.exception.exceptions.CustomLogicException;
import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.common.utils.ErrorLoggerUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.INVALID_INPUT_VALUE;
import static com.objects.marketbridge.common.exception.exceptions.ErrorCode.NO_ERROR_CODE;
import static org.springframework.http.HttpStatus.*;


@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

    // String 타입 변수에 들어갈 값이 없을땐 null 대신 "none" 으로 초기화 시켜줌
    private final static String NONE = "none";

    // 비즈니스 로직 예외
    @ExceptionHandler(value = CustomLogicException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResult.Response customExHandler(CustomLogicException e, HttpServletRequest httpRequest, HandlerMethod handlerMethod) {

        ErrorResult errorResult = ErrorResult.builder()
                .code(e.getHttpStatus().value())
                .status(e.getHttpStatus())
                .path(getPath(httpRequest.getMethod(), httpRequest.getRequestURI()))
                .errorCode(e.getErrorCode())
                .message(e.getMessage())
                .timestamp(e.getTimestamp())
                .className(handlerMethod.getBeanType().getName())
                .methodName(handlerMethod.getMethod().getName())
                .exceptionName(e.getClass().getName())
                .build();

        // 로그 찍기
        ErrorLoggerUtils.warnLog(errorResult);

        return errorResult.toResponse();
    }

    // @Valid, @Validated 에러 주로 유효하지 않은 범위의 값을 입력할 경우 발생
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(BAD_REQUEST)
    public ErrorResult.Response fieldErrorExHandler(MethodArgumentNotValidException e, HttpServletRequest httpRequest, HandlerMethod handlerMethod, BindingResult bindingResult) {

        String message = "";
        if (bindingResult.hasFieldErrors()) {
            message = bindingResult.getFieldErrors().stream()
                    .map(err ->
                            getFieldErrorMessage(err.getField(), err.getDefaultMessage(), String.valueOf(err.getRejectedValue())))
                    .collect(Collectors.joining("\n"));
        }

        ErrorResult errorResult = ErrorResult.builder()
                .code(BAD_REQUEST.value())
                .status(BAD_REQUEST)
                .path(getPath(httpRequest.getMethod(), httpRequest.getRequestURI()))
                .errorCode(INVALID_INPUT_VALUE)
                .message(message)
                .timestamp(LocalDateTime.now())
                .className(handlerMethod.getBeanType().getName())
                .methodName(handlerMethod.getMethod().getName())
                .exceptionName(e.getClass().getName())
                .build();

        // 로그 찍기
        ErrorLoggerUtils.warnLog(errorResult);

        return errorResult.toResponse();
    }

    // HTTP 메시지 컨버터 에러시 발생하는 예외. ex) boolean 타입 데이터가 와야하는데 1234 가 값으로 들어올 경우
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseStatus(BAD_REQUEST)
    public ErrorResult.Response handleHttpMessageNotReadableExHandler(HttpMessageNotReadableException e, HttpServletRequest httpRequest, HandlerMethod handlerMethod) {

        String message = "";
        if (e.getCause() instanceof InvalidFormatException invalidFormatEx) {
            String fieldType = invalidFormatEx.getTargetType().getSimpleName();
            String filedName = invalidFormatEx.getValue().toString();
            message = getTypeMismatchErrorMessage(fieldType, filedName);
        }

        ErrorResult errorResult = ErrorResult.builder()
                .code(BAD_REQUEST.value())
                .status(BAD_REQUEST)
                .path(getPath(httpRequest.getMethod(), httpRequest.getRequestURI()))
                .errorCode(INVALID_INPUT_VALUE)
                .message(message)
                .timestamp(LocalDateTime.now())
                .className(handlerMethod.getBeanType().getName())
                .methodName(handlerMethod.getMethod().getName())
                .exceptionName(e.getClass().getName())
                .build();

        // 로그 찍기
        ErrorLoggerUtils.warnLog(errorResult);

        return errorResult.toResponse();
    }

    // 지원하지 않는 HTTP 메서드 호출시 발생
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    protected ErrorResult.Response handleHttpRequestMethodNotSupportedExHandler(HttpRequestMethodNotSupportedException e, HttpServletRequest httpRequest) {

        ErrorResult errorResult = ErrorResult.builder()
                .code(METHOD_NOT_ALLOWED.value())
                .status(METHOD_NOT_ALLOWED)
                .path(getPath(httpRequest.getMethod(), httpRequest.getRequestURI()))
                .errorCode(ErrorCode.METHOD_NOT_ALLOWED)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .className(NONE)
                .methodName(NONE)
                .exceptionName(e.getClass().getName())
                .build();

        // 로그 찍기
        ErrorLoggerUtils.warnLog(errorResult);

        return errorResult.toResponse();

    }

    // 파라미터의 타입이 요청의 값과 호환되지 않는 경우. ex) /test/{1} -> /test/{a}
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(NOT_FOUND)
    protected ErrorResult.Response handleMethodArgumentTypeMismatchExHandler(MethodArgumentTypeMismatchException e, HttpServletRequest httpRequest) {

        ErrorResult errorResult = ErrorResult.builder()
                .code(NOT_FOUND.value())
                .status(NOT_FOUND)
                .path(getPath(httpRequest.getMethod(), httpRequest.getRequestURI()))
                .errorCode(ErrorCode.RESOUCRE_NOT_FOUND)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .className(NONE)
                .methodName(NONE)
                .exceptionName(e.getClass().getName())
                .build();

        // 로그 찍기
        ErrorLoggerUtils.warnLog(errorResult);

        return errorResult.toResponse();
    }

    // URI 잘못 요청 했을때 -> ex) /cart -> /art
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(NOT_FOUND)
    protected ErrorResult.Response handleNoResourceFoundExHandler(NoResourceFoundException e, HttpServletRequest httpRequest) {

        ErrorResult errorResult = ErrorResult.builder()
                .code(NOT_FOUND.value())
                .status(NOT_FOUND)
                .path(getPath(httpRequest.getMethod(), httpRequest.getRequestURI()))
                .errorCode(ErrorCode.RESOUCRE_NOT_FOUND)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .className(NONE)
                .methodName(NONE)
                .exceptionName(e.getClass().getName())
                .build();

        // 로그 찍기
        ErrorLoggerUtils.warnLog(errorResult);

        return errorResult.toResponse();
    }

    // HTTP 요청 파라미터가 누락 혹은 이상있는경우.  ex) /orders?keyword=2 -> /orders?keywo=2
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(BAD_REQUEST)
    protected ErrorResult.Response handleMissingServletRequestParameterExHandler(MissingServletRequestParameterException e, HttpServletRequest httpRequest) {

        ErrorResult errorResult = ErrorResult.builder()
                .code(BAD_REQUEST.value())
                .status(BAD_REQUEST)
                .path(getPath(httpRequest.getMethod(), httpRequest.getRequestURI()))
                .errorCode(INVALID_INPUT_VALUE)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .className(NONE)
                .methodName(NONE)
                .exceptionName(e.getClass().getName())
                .build();

        // 로그 찍기
        ErrorLoggerUtils.warnLog(errorResult);

        return errorResult.toResponse();
    }

    // 서버에러
    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResult.Response serverExHandler(Exception e, HttpServletRequest httpRequest, HandlerMethod handlerMethod) {

        ErrorResult errorResult = ErrorResult.builder()
                .code(INTERNAL_SERVER_ERROR.value())
                .status(INTERNAL_SERVER_ERROR)
                .path(getPath(httpRequest.getMethod(), httpRequest.getRequestURI()))
                .errorCode(NO_ERROR_CODE)
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .className(handlerMethod.getBeanType().getName())
                .methodName(handlerMethod.getMethod().getName())
                .exceptionName(e.getClass().getName())
                .elements(e.getStackTrace())
                .build();

        // 로그 찍기
        ErrorLoggerUtils.errorLog(errorResult);

        return errorResult.toResponse();
    }

    private String getPath(String method, String uri) {
        StringBuilder sb = new StringBuilder();
        return String.valueOf(sb.append(method).append(uri));
    }

    private String getFieldErrorMessage(String rejectedField, String msg, String rejectedValue) {
        return String.format("[%s] 은/는 %s [입력한 값:%s]", rejectedField, msg, rejectedValue);
    }

    private String getTypeMismatchErrorMessage(String fieldType, String fieldName) {
        return String.format("[%s] 타입의 값이 필요합니다. [입력한 값:%s]", fieldType, fieldName);
    }
}

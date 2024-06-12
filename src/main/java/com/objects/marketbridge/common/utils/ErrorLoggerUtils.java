package com.objects.marketbridge.common.utils;

import com.objects.marketbridge.common.exception.advice.ErrorResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorLoggerUtils {
    public static void warnLog(ErrorResult errorResult) {
        log.warn(formatter("[HTTP_STATUS_CODE]"),errorResult.getCode()); // HTTP 상태 코드
        log.warn(formatter("[HTTP_STATUS]"),errorResult.getStatus()); // HTTP 상태 코드
        log.warn(formatter("[PATH]"),errorResult.getPath()); // URI
        log.warn(formatter("[ERROR_MESSAGE]"),errorResult.getMessage()); // 에러 메시지
        log.warn(formatter("[ERROR_CODE]"),errorResult.getErrorCode()); // 에러 코드
        log.warn(formatter("[TIMESTAMP]"),errorResult.getTimestamp()); // 에러 시간
        log.warn(formatter("[CLASS_NAME]"),errorResult.getClassName()); // 에러 발생한 클래스
        log.warn(formatter("[METHOD_NAME]"),errorResult.getMethodName()); // 에러 발생한 메서드
        log.warn(formatter("[EXCEPTION_NAME]"),errorResult.getExceptionName()); // 예외 인스턴스 이름
    }

    public static void errorLog(ErrorResult errorResult) {
        log.error(formatter("[HTTP_STATUS_CODE]"),errorResult.getCode()); // HTTP 상태 코드
        log.error(formatter("[HTTP_STATUS]"),errorResult.getStatus()); // HTTP 상태 코드
        log.error(formatter("[PATH]"),errorResult.getPath()); // URI
        log.error(formatter("[ERROR_MESSAGE]"),errorResult.getMessage()); // 에러 메시지
        log.error(formatter("[ERROR_CODE]"),errorResult.getErrorCode()); // 에러 코드
        log.error(formatter("[TIMESTAMP]"),errorResult.getTimestamp()); // 에러 시간
        log.error(formatter("[CLASS_NAME]"),errorResult.getClassName()); // 에러 발생한 클래스
        log.error(formatter("[METHOD_NAME]"),errorResult.getMethodName()); // 에러 발생한 메서드
        log.error(formatter("[EXCEPTION_NAME]"),errorResult.getExceptionName()); // 예외 인스턴스 이름
//        for (StackTraceElement element : errorResult.getElements()) {
//            log.error(formatter("[STACK_TRACE]"),element);
//        }
    }

    private static String formatter(String str) {

        StringBuilder sb = new StringBuilder();
        String msg = String.format("%-20s", str);
        return String.valueOf(sb.append(msg).append(" ").append("{}"));
    }
}

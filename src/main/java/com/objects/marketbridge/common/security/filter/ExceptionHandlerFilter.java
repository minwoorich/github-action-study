package com.objects.marketbridge.common.security.filter;

import com.objects.marketbridge.common.exception.exceptions.ErrorCode;
import com.objects.marketbridge.common.security.dto.ErrRes;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;


@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }  catch (InternalAuthenticationServiceException |
                  SecurityException |
                  IllegalStateException e) {
            log.warn("{} ", e.getStackTrace()[0]);

            ErrorCode errorCode = ErrorCode.INTERNAL_SECURITY_ERROR;
            setErrRes(request, response, errorCode, errorCode.getMessage());
        } catch (BadCredentialsException e) {
            log.warn("{} ", e.getStackTrace()[0]);

            ErrorCode errorCode = ErrorCode.BAD_CREDENTIALS_ERROR;
            setErrRes(request, response, errorCode, errorCode.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("{} ", e.getStackTrace()[0]);

            ErrorCode errorCode = ErrorCode.EXPIRED_JWT_ERROR;
            setErrRes(request, response, errorCode, errorCode.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("{} ", e.getStackTrace()[0]);

            ErrorCode errorCode = ErrorCode.MALFORMED_JWT_ERROR;
            setErrRes(request, response, errorCode, errorCode.getMessage());
        } catch (SignatureException e) {
            log.warn("{} ", e.getStackTrace()[0]);

            ErrorCode errorCode = ErrorCode.SIGNATURE_JWT_ERROR;
            setErrRes(request, response, errorCode, errorCode.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("{} ", e.getStackTrace()[0]);

            ErrorCode errorCode = ErrorCode.UNSUPPORTED_JWT_ERROR;
            setErrRes(request, response, errorCode, e.getMessage());
        }
    }

    private void setErrRes(
            HttpServletRequest request,
            HttpServletResponse response,
            ErrorCode errorCode,
            String message
    ) throws IOException {
        int code = HttpStatus.UNAUTHORIZED.value();
        String status = HttpStatus.UNAUTHORIZED.getReasonPhrase();
        response.setStatus(code);
        response.setContentType(APPLICATION_JSON+"; "+"charset=UTF-8");

        response.getWriter().write(
                ErrRes.of(code, status, message, request.getRequestURI(), errorCode).convertToJson()
        );
    }
}

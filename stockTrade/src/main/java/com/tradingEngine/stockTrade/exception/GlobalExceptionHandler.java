package com.tradingEngine.stockTrade.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(QueueCapacityExceededException.class)
    public ResponseEntity<ExceptionResponse> handleQueueCapacityExceededException(QueueCapacityExceededException ex,
                                                                                  HttpServletRequest request) {

        ExceptionResponse exceptionResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_ACCEPTABLE.value(),
                HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(exceptionResponse);

    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleOrderNotFoundException(OrderNotFoundException ex,
                                                          HttpServletRequest request) {

        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(EngineBusyException.class)
    public ResponseEntity<ExceptionResponse> handleEngineBusyException(OrderNotFoundException ex,
                                                                          HttpServletRequest request) {

        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlePortfolioNotFoundException(PortfolioNotFoundException ex,
                                                                              HttpServletRequest request) {
        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(InsufficientSharesException.class)
    public ResponseEntity<ExceptionResponse> handleInsufficientSharesException(InsufficientSharesException ex,
                                                                              HttpServletRequest request) {
        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                               HttpServletRequest request) {
        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(CircuitBreakerViolationException.class)
    public ResponseEntity<ExceptionResponse> handleCircuitBreakerViolationException(CircuitBreakerViolationException ex,
                                                                       HttpServletRequest request) {
        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidTradePriceException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTradePriceException(InvalidTradePriceException ex,
                                                             HttpServletRequest request) {

        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ExceptionResponse> handleInsufficientBalanceException(InsufficientBalanceException ex,
                                                                                HttpServletRequest request) {
        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(RefreshTokenNotFound.class)
    public ResponseEntity<ExceptionResponse> handleRefreshTokenNotFound(RefreshTokenNotFound ex,
                                                                        HttpServletRequest request) {

        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException ex,
                                                                           HttpServletRequest request){
        log.error("Exception Request belongs to jwtExpired: {}", ex.getMessage());

        ExceptionResponse exceptionErrorRsponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_ACCEPTABLE.value(),
                HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                "Jwt token Expired. " + ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(exceptionErrorRsponse);

    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedJwtException(MalformedJwtException ex ,
                                                                             HttpServletRequest request){

        log.error("Exception Request belongs to MalformedJwtException : {}", ex.getMessage());

        ExceptionResponse exceptionErrorRsponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ExceptionResponse> handleSignatureException(SignatureException ex,
                                                                          HttpServletRequest request){

        log.error("Exception Request belongs to SignatureException : {}", ex.getMessage());

        ExceptionResponse exceptionErrorRsponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.NO_CONTENT.value(),
                HttpStatus.NO_CONTENT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleUnsupportedJwtException(UnsupportedJwtException ex,
                                                                               HttpServletRequest request){

        log.error("Exception Request belongs to UnsupportedJwtException : {}", ex.getMessage());

        ExceptionResponse exceptionErrorRsponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exceptionErrorRsponse);

    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                                HttpServletRequest request){

        log.error("Exception Request belongs to MethodArgumentNotValidException : {}", ex.getMessage());

        Map<String,String> fieldError = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach( error -> fieldError.put(error.getField(),
                        error.getDefaultMessage()));

        ExceptionResponse exceptionErrorRsponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Required Field Validation Error." + ex.getMessage(),
                request.getRequestURI()

        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exceptionErrorRsponse);
    }

    @ExceptionHandler(OAuth2AuthorizationException.class)
    public ResponseEntity<ExceptionResponse> handleOAuth2AuthorizationException(OAuth2AuthorizationException oAuth2AuthorizationException,
                                                                       HttpServletRequest request){
        log.error("Exception Request Belongs to OAuth2Authentication: {}", oAuth2AuthorizationException.getMessage());
        ExceptionResponse apiError = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Required Field Validation Error." + oAuth2AuthorizationException.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException( AuthenticationException authenticationException,
                                                                   HttpServletRequest request){

        log.error("Exception Request Belongs to Authentication: {}", authenticationException.getMessage());
        ExceptionResponse apiError = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Required Field Validation Error." + authenticationException.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ClientAuthorizationRequiredException.class)
    public ResponseEntity<ExceptionResponse> handleClientAuthorizationRequiredException(ClientAuthorizationRequiredException clientAuthorizationRequiredException,
                                                                               HttpServletRequest request){
        log.error("Exception Request Belongs to ClientAuthorization: {}" ,clientAuthorizationRequiredException.getMessage());
        ExceptionResponse apiError = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Required Field Validation Error." + clientAuthorizationRequiredException.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex,
                                                             HttpServletRequest request) {

        ExceptionResponse errorResponse = new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}

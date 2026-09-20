package com.tradingEngine.stockTrade.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
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

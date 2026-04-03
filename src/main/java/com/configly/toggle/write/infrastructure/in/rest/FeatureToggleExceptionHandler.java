package com.configly.toggle.write.infrastructure.in.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.configly.web.ErrorCode;
import com.configly.web.ErrorResponse;
import com.configly.web.correlation.CorrelationProvider;
import com.configly.toggle.write.domain.featuretoggle.exception.CannotOperateOnArchivedFeatureToggleException;
import com.configly.toggle.write.domain.featuretoggle.exception.FeatureToggleAlreadyExistsException;
import com.configly.toggle.write.domain.featuretoggle.exception.FeatureToggleNotFoundException;
import com.configly.toggle.write.domain.featuretoggle.exception.FeatureToggleUpdateFailedException;

@RestControllerAdvice
@AllArgsConstructor
class FeatureToggleExceptionHandler {

    private final CorrelationProvider correlationProvider;

    @ExceptionHandler(exception = FeatureToggleNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(FeatureToggleNotFoundException exception) {
        var errorResponse = createErrorResponse(ErrorCode.FEATURE_TOGGLE_NOT_FOUND, exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler({FeatureToggleAlreadyExistsException.class})
    ResponseEntity<ErrorResponse> handle(FeatureToggleAlreadyExistsException exception) {
        var errorResponse = createErrorResponse(ErrorCode.FEATURE_TOGGLE_ALREADY_EXISTS, exception);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler({CannotOperateOnArchivedFeatureToggleException.class})
    ResponseEntity<ErrorResponse> handle(CannotOperateOnArchivedFeatureToggleException exception) {
        var errorResponse = createErrorResponse(ErrorCode.FEATURE_TOGGLE_ARCHIVED_OPERATION_NOT_ALLOWED, exception);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler({FeatureToggleUpdateFailedException.class})
    ResponseEntity<ErrorResponse> handle(FeatureToggleUpdateFailedException exception) {
        var errorResponse = createErrorResponse(ErrorCode.FEATURE_TOGGLE_WAS_MODIFIED_BY_ANOTHER_REQUEST, exception);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }


    private ErrorResponse createErrorResponse(ErrorCode errorCode, Exception e) {
        return ErrorResponse.from(errorCode, e, correlationProvider.current());
    }

}

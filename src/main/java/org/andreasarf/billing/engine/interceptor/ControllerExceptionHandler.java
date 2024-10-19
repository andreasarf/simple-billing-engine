package org.andreasarf.billing.engine.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.andreasarf.billing.engine.common.enums.ErrorCode;
import org.andreasarf.billing.engine.dto.BaseResponse;
import org.andreasarf.billing.engine.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BaseResponse<Void>> handleNoSuchElementException(
            NoSuchElementException e) {
        return ResponseUtil.error(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception e) {
        log.error("An error occurred", e);
        return ResponseUtil.error(ErrorCode.INTERNAL_ERROR);
    }
}

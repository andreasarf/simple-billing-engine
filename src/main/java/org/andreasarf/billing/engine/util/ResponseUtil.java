package org.andreasarf.billing.engine.util;

import lombok.experimental.UtilityClass;
import org.andreasarf.billing.engine.common.enums.ErrorCode;
import org.andreasarf.billing.engine.dto.BaseResponse;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseUtil {

  public <T> ResponseEntity<BaseResponse<T>> ok() {
    return ResponseEntity.ok(BaseResponse.ok(null));
  }

  public <T> ResponseEntity<BaseResponse<T>> ok(T data) {
    return ResponseEntity.ok(BaseResponse.ok(data));
  }

  public <T> ResponseEntity<BaseResponse<T>> invalidParam(String reason) {
    return ResponseEntity.badRequest()
        .body(BaseResponse.invalidParam(reason));
  }

  public <T> ResponseEntity<BaseResponse<T>> internalError(String reason) {
    return ResponseEntity.internalServerError()
        .body(BaseResponse.internalError(reason));
  }

    public <T> ResponseEntity<BaseResponse<T>> error(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(BaseResponse.<T>builder()
                    .errorCode(errorCode)
                    .build());
    }
}

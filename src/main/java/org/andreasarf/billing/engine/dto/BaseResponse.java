package org.andreasarf.billing.engine.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import org.andreasarf.billing.engine.common.enums.ErrorCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonPropertyOrder({ "code", "message", "error_reasons", "data"})
public class BaseResponse<T> implements Serializable {

  @JsonIgnore
  private ErrorCode errorCode;
  private List<String> errorReasons;
  private T data;

  public static <T> BaseResponse<T> ok(T data) {
    return BaseResponse.<T>builder()
        .errorCode(ErrorCode.OK)
        .data(data)
        .build();
  }

  public static <T> BaseResponse<T> invalidParam(String reason) {
    return BaseResponse.<T>builder()
        .errorCode(ErrorCode.INVALID_PARAM)
        .errorReason(reason)
        .build();
  }

  public static <T> BaseResponse<T> internalError(String reason) {
    return BaseResponse.<T>builder()
        .errorCode(ErrorCode.INTERNAL_ERROR)
        .errorReason(reason)
        .build();
  }

  @JsonGetter
  public String getCode() {
    return errorCode.getCode();
  }

  @JsonGetter
  public String getMessage() {
    return errorCode.getMessage();
  }

  public static class BaseResponseBuilder<T> {

    public BaseResponseBuilder<T> errorReason(String errorReason) {
      if (this.errorReasons == null) {
        this.errorReasons = new ArrayList<>();
      }
      this.errorReasons.add(errorReason);
      return this;
    }
  }
}

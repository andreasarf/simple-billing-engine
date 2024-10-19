package org.andreasarf.billing.engine.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  OK("2000000", "Success", 200),
  INVALID_PARAM("4000001", "Invalid parameter", 400),
  PAID_AMOUNT_NOT_MATCH("4000002", "Paid amount not match", 400),
  RESOURCE_NOT_FOUND("4040001", "Resource not found", 404),
  INTERNAL_ERROR("5000001", "Internal error", 500),
  ;

  private final String code;
  private final String message;
  private final int httpStatus;
}

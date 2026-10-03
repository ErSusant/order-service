package com.ebook.management.orderservice.exception;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {

  private Instant timestamp = Instant.now();
  private int status;
  private String message;
  private List<String> errors;

  public ErrorResponse(int status, String message, List<String> errors) {
    this.status = status;
    this.message = message;
    this.errors = errors;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public List<String> getErrors() {
    return errors;
  }
}

package com.graly.erp.base.calendar;

/**
 * base exception used for all exceptions thrown in jBPM.
 */
public class CalendarException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CalendarException() {
    super();
  }
  public CalendarException(String message, Throwable cause) {
    super(message, cause);
  }
  public CalendarException(String message) {
    super(message);
  }
  public CalendarException(Throwable cause) {
    super(cause);
  }
}

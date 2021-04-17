package com.clinica.abc.common;

public enum NextStep {
  WELCOME,

  FIND_USER,
  USER_FOUND,
  USER_NOT_FOUND,

  DECISION,
  SELECT_DATE,
  SELECT_HOUR,
  CONFIRM_APPOINTMENT,

  FAREWELL,
  DEFAULT
}

package com.clinica.abc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HourUtil {

  private static final String HOUR_REGEX = "((1[0-2]|0?[1-9]):([0-5][0-9]) ?([AaPp][Mm]))";

  public static boolean isValidHour(String hour) {
    Pattern pattern = Pattern.compile(HOUR_REGEX);
    Matcher matcher = pattern.matcher(hour);

    return matcher.matches();
  }
}

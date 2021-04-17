package com.clinica.abc.util;

import org.junit.Assert;
import org.junit.Test;

public class HourUtilTests {

  @Test
  public void shouldBeValidHour1() {
    Assert.assertTrue(HourUtil.isValidHour("08:00 am"));
  }

  @Test
  public void shouldBeValidHour2() {
    Assert.assertTrue(HourUtil.isValidHour("01:00 pm"));
  }

  @Test
  public void shouldBeInvalidHour1() {
    Assert.assertFalse(HourUtil.isValidHour("asd:fgh pm"));
  }

  @Test
  public void shouldBeInvalidHour2() {
    Assert.assertFalse(HourUtil.isValidHour("11:20"));
  }
}

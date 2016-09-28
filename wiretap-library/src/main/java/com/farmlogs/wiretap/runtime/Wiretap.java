package com.farmlogs.wiretap.runtime;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 9/27/16
 * (C) 2016 Damian Wieczorek
 */
public final class Wiretap {

  private Wiretap() {
    throw new IllegalArgumentException("No instances!");
  }

  static MethodCallListener listener;

  public static void setMethodCallListener(final MethodCallListener listener) {
    Wiretap.listener = listener;
  }

}

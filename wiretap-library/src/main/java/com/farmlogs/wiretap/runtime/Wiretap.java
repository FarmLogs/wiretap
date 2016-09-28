package com.farmlogs.wiretap.runtime;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 9/27/16
 * (C) 2016 Damian Wieczorek
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class Wiretap {

  private Wiretap() {
    throw new IllegalArgumentException("No instances!");
  }

  static WiretapListener listener;

  public static void setMethodCallListener(final WiretapListener listener) {
    Wiretap.listener = listener;
  }

}

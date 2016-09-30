package com.farmlogs.wiretap.runtime;

/**
 * The runtime entry point for Wiretap.
 *
 * The best place to initialize the wiretap listener is probably in your application's onCreate
 * or attachBaseContext.
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

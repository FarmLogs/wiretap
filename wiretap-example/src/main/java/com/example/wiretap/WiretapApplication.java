package com.example.wiretap;

import android.app.Application;

import com.farmlogs.wiretap.runtime.Wiretap;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 9/30/16
 * (C) 2016 Damian Wieczorek
 */
public class WiretapApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Wiretap.setMethodCallListener(new LoggingListener());
  }

}

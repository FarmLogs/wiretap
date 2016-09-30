package com.example.wiretap;

import android.app.Application;

import com.farmlogs.wiretap.runtime.Wiretap;

public class WiretapApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Wiretap.setMethodCallListener(new LoggingListener());
  }

}

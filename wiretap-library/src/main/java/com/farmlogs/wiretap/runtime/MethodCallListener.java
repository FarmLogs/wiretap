package com.farmlogs.wiretap.runtime;

import java.lang.reflect.Method;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 9/27/16
 * (C) 2016 Damian Wieczorek
 */
@SuppressWarnings("WeakerAccess")
public interface MethodCallListener {

  void onMethodCalled(Method method, Object receiver, Object[] arguments);
  void onMethodReturned(Method method, Object receiver, Object[] arguments, Object returnValue);
  void onMethodThrew(Method method, Object receiver, Object[] arguments, Throwable throwable);

}

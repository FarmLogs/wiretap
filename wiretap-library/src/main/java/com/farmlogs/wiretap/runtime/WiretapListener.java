package com.farmlogs.wiretap.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 9/27/16
 * (C) 2016 Damian Wieczorek
 */
@SuppressWarnings("WeakerAccess")
public interface WiretapListener {

  void onMethodCalled(Method method, Object receiver, Object[] arguments);
  void onMethodReturned(Method method, Object receiver, Object[] arguments, Object returnValue);
  void onMethodThrew(Method method, Object receiver, Object[] arguments, Throwable throwable);

  void onConstructorCalled(Constructor constructor, Object receiver, Object[] arguments);
  void onConstructorReturned(Constructor constructor, Object receiver, Object[] arguments);
  void onConstructorThrew(Constructor constructor, Object receiver, Object[] arguments, Throwable throwable);

}

package com.farmlogs.wiretap.runtime;

import java.lang.reflect.Method;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 9/27/16
 * (C) 2016 Damian Wieczorek
 */
public interface MethodCallListener {

  void onMethodCalled(Method method, Object[] arguments);

}

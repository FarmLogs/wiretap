package com.farmlogs.wiretap.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * The listener interface for Wiretap.
 *
 * To listen to method and constructor calls, implement this interface and initialize Wiretap.
 *
 * @see Wiretap
 */
@SuppressWarnings("WeakerAccess")
public interface WiretapListener {

  /**
   * Callback for the start of a {@link Method} invocation.
   *
   * @param method    the method being invoked
   * @param receiver  the receiver of the method invocation, or null if it is a static method
   * @param arguments the arguments supplied to the method
   */
  void onMethodCalled(Method method, Object receiver, Object[] arguments);


  /**
   * Callback for the end of a method invocation, if it has returned without throwing.
   * Only one of {@link #onMethodReturned} or {@link #onMethodThrew} will be called, but never both.
   *
   * @param method      the method being invoked
   * @param receiver    the receiver of the method invocation, or null if it is a static method
   * @param arguments   the arguments supplied to the method
   * @param returnValue the return value from the invocation
   */
  void onMethodReturned(Method method, Object receiver, Object[] arguments, Object returnValue);

  /**
   * Callback for the end of a method invocation, if it has thrown.
   * Only one of {@link #onMethodReturned} or {@link #onMethodThrew} will be called, but never both.
   *
   * @param method      the method being invoked
   * @param receiver    the receiver of the method invocation, or null if it is a static method
   * @param arguments   the arguments supplied to the method
   * @param throwable   the {@link Throwable} which was thrown
   */
  void onMethodThrew(Method method, Object receiver, Object[] arguments, Throwable throwable);

  /**
   * Callback for the start of a {@link Constructor} invocation.
   *
   * @param constructor the constructor being invoked
   * @param receiver    the receiver of the constructor invocation, i.e. object to be initialized
   *                    NOTE: the object has not yet been initialized
   * @param arguments   the arguments supplied to the constructor
   */
  void onConstructorCalled(Constructor constructor, Object receiver, Object[] arguments);

  /**
   * Callback for the end of a {@link Constructor} invocation, if it has returned without throwing.
   * Only one of {@link #onConstructorReturned} or {@link #onConstructorThrew} will be called,
   * but never both.
   *
   * @param constructor the constructor being invoked
   * @param receiver    the receiver of the constructor invocation, i.e. object which was constructed
   * @param arguments   the arguments supplied to the constructor
   */
  void onConstructorReturned(Constructor constructor, Object receiver, Object[] arguments);

  /**
   * Callback for the end of a {@link Constructor} invocation, if it has thrown.
   * Only one of {@link #onConstructorReturned} or {@link #onConstructorThrew} will be called,
   * but never both.
   *
   * @param constructor the constructor being invoked
   * @param receiver    the receiver of the constructor invocation (i.e. object which failed to initialize)
   *                    NOTE: the object is likely not fully initialized
   * @param arguments   the arguments supplied to the constructor
   * @param throwable   the {@link Throwable} which was thrown
   */
  void onConstructorThrew(Constructor constructor, Object receiver, Object[] arguments, Throwable throwable);

}

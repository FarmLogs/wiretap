package com.farmlogs.wiretap.runtime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

import java.lang.reflect.Method;

@SuppressWarnings({"WeakerAccess", "unused"})
@Aspect
public final class $$WiretapRuntime {

  private static volatile boolean enabled = true;

  @Pointcut("within(@com.farmlogs.wiretap.Tap *)")
  public void withinAnnotatedClass() {}

  @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
  public void methodInsideAnnotatedType() {}

  @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
  public void constructorInsideAnnotatedType() {}

  @Pointcut("execution(@com.farmlogs.wiretap.Tap * *(..)) || methodInsideAnnotatedType()")
  public void method() {}

  @Pointcut("execution(@com.farmlogs.wiretap.Tap *.new(..)) || constructorInsideAnnotatedType()")
  public void constructor() {}

  public static void setEnabled(boolean enabled) {
    $$WiretapRuntime.enabled = enabled;
  }

  @Around("method() || constructor()")
  public Object logAndExecute(ProceedingJoinPoint joinPoint) throws Throwable {
    if (!enabled) {
      return joinPoint.proceed();
    }

    final MethodCallListener listener = Wiretap.listener;
    if (listener == null) {
      return joinPoint.proceed();
    }

    final Method method = getMethod(joinPoint);
    final Object receiver = joinPoint.getThis();
    final Object[] arguments = joinPoint.getArgs();

    listener.onMethodCalled(method, receiver, arguments);
    Object returnValue;
    try {
      returnValue = joinPoint.proceed();
    } catch (Throwable throwable) {
      listener.onMethodThrew(method, receiver, arguments, throwable);
      throw throwable;
    }

    listener.onMethodReturned(method, receiver, arguments, returnValue);
    return returnValue;
  }

  private static Method getMethod(final JoinPoint joinPoint) {
    try {
      return getMethod((CodeSignature) joinPoint.getSignature());
    } catch (NoSuchMethodException e) {
      throw new AssertionError("Could not find method at join point: " + joinPoint.toShortString());
    }
  }

  private static Method getMethod(final CodeSignature codeSignature) throws NoSuchMethodException {
    final Class<?> declaringType = codeSignature.getDeclaringType();
    final String methodName = codeSignature.getName();
    final Class<?>[] parameterTypes = codeSignature.getParameterTypes();
    return declaringType.getDeclaredMethod(methodName, parameterTypes);
  }

}

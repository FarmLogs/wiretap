package com.farmlogs.wiretap.runtime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SuppressWarnings({"WeakerAccess", "unused"})
@Aspect
public final class $$WiretapRuntime {

    private static final String TAG = "WiretapRuntime";

    private static volatile boolean enabled = true;

    @Pointcut("within(@com.farmlogs.wiretap.Tap *)")
    public void withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    public void constructorInsideAnnotatedType() {
    }

    @Pointcut("execution(@com.farmlogs.wiretap.Tap * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }

    @Pointcut("execution(@com.farmlogs.wiretap.Tap *.new(..)) || constructorInsideAnnotatedType()")
    public void constructor() {
    }

    public static void setEnabled(boolean enabled) {
        $$WiretapRuntime.enabled = enabled;
    }

    @Around("method()")
    public Object executeMethodAndNotify(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!enabled) {
            return joinPoint.proceed();
        }

        final WiretapListener listener = Wiretap.listener;
        if (listener == null) {
            return joinPoint.proceed();
        }

        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final Object receiver = joinPoint.getThis();
        final Object[] arguments = joinPoint.getArgs();

        return enterMethod(listener, method, receiver, arguments, joinPoint);
    }

    @Around("constructor()")
    public Object executeConstructorAndNotify(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!enabled) {
            return joinPoint.proceed();
        }

        final WiretapListener listener = Wiretap.listener;
        if (listener == null) {
            return joinPoint.proceed();
        }

        final ConstructorSignature signature = ((ConstructorSignature) joinPoint.getSignature());
        final Constructor constructor = signature.getConstructor();
        final Object receiver = joinPoint.getThis();
        final Object[] arguments = joinPoint.getArgs();

        return enterConstructor(listener, constructor, receiver, arguments, joinPoint);
    }

    private static Object enterMethod(final WiretapListener listener,
                                      final Method method,
                                      final Object receiver,
                                      final Object[] arguments,
                                      final ProceedingJoinPoint joinPoint) throws Throwable {
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

    private static Object enterConstructor(final WiretapListener listener,
                                           final Constructor constructor,
                                           final Object receiver,
                                           final Object[] arguments,
                                           final ProceedingJoinPoint joinPoint) throws Throwable {
        listener.onConstructorCalled(constructor, receiver, arguments);
        Object returnValue;
        try {
            returnValue = joinPoint.proceed();
        } catch (Throwable throwable) {
            listener.onConstructorThrew(constructor, receiver, arguments, throwable);
            throw throwable;
        }
        listener.onConstructorReturned(constructor, receiver, arguments);
        return returnValue;
    }

}

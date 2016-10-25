package com.farmlogs.wiretap.runtime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Damian Wieczorek {@literal <damian@farmlogs.com>}
 * @since 10/25/16
 * (C) 2016
 */
final class $$WiretapRuntimeCommon {

    private $$WiretapRuntimeCommon() {
        throw new UnsupportedOperationException("No instances!");
    }

    private static volatile boolean enabled = true;

    static Object executeMethodAndNotify(ProceedingJoinPoint joinPoint) throws Throwable {
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

        return proceedWithMethod(listener, method, receiver, arguments, joinPoint);
    }

    static Object executeConstructorAndNotify(ProceedingJoinPoint joinPoint) throws Throwable {
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

        return proceedWithConstructor(listener, constructor, receiver, arguments, joinPoint);
    }

    private static Object proceedWithMethod(final WiretapListener listener,
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

    private static Object proceedWithConstructor(final WiretapListener listener,
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

package com.farmlogs.wiretap.runtime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@SuppressWarnings({"WeakerAccess", "unused"})
@Aspect
public final class $$SimpleWiretapAspect {

    private static final String TAG = "WiretapRuntime";

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

    @Around("method()")
    public Object executeMethodAndNotify(ProceedingJoinPoint joinPoint) throws Throwable {
        return $$WiretapRuntimeCommon.executeMethodAndNotify(joinPoint);
    }

    @Around("constructor()")
    public Object executeConstructorAndNotify(ProceedingJoinPoint joinPoint) throws Throwable {
        return $$WiretapRuntimeCommon.executeConstructorAndNotify(joinPoint);
    }

}

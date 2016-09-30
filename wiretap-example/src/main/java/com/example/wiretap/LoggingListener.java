package com.example.wiretap;

import android.util.Log;

import com.farmlogs.wiretap.runtime.WiretapListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;

public class LoggingListener implements WiretapListener {

  private static void logCall(final Member member,
                              final Object receiver,
                              final Object[] arguments) {
    final String logMessage = new StringBuilder("Called: (\n  name: ")
        .append(member.getName())
        .append(",\n  receiver: ")
        .append(receiver)
        .append(",\n  arguments: ")
        .append(Arrays.toString(arguments))
        .append("\n)\n")
        .toString();
    Log.v(member.getDeclaringClass().getSimpleName(), logMessage);
  }

  private static void logReturn(final Member member,
                                final Object receiver,
                                final Object[] arguments,
                                final Object returnValue) {
    final String logMessage = new StringBuilder("Returned: (\n  name: ")
        .append(member.getName())
        .append(",\n  receiver: ")
        .append(receiver)
        .append(",\n  arguments: ")
        .append(Arrays.toString(arguments))
        .append(",\n  returnValue: ")
        .append(returnValue)
        .append("\n)\n")
        .toString();
    Log.v(member.getDeclaringClass().getSimpleName(), logMessage);
  }

  private static void logThrow(final Member member,
                               final Object receiver,
                               final Object[] arguments,
                               final Throwable throwable) {
    final String logMessage = new StringBuilder("Threw: (\n  name: ")
        .append(member.getName())
        .append(",\n  receiver: ")
        .append(receiver)
        .append(",\n  arguments: ")
        .append(Arrays.toString(arguments))
        .append("\n)\n")
        .toString();
    Log.e(member.getDeclaringClass().getSimpleName(), logMessage, throwable);
  }

  @Override
  public void onMethodCalled(Method method, Object receiver, Object[] arguments) {
    logCall(method, receiver, arguments);
  }

  @Override
  public void onMethodReturned(Method method, Object receiver, Object[] arguments, Object returnValue) {
    logReturn(method, receiver, arguments, returnValue);
  }

  @Override
  public void onMethodThrew(Method method, Object receiver, Object[] arguments, Throwable throwable) {
    logThrow(method, receiver, arguments, throwable);
  }

  @Override
  public void onConstructorCalled(Constructor constructor, Object receiver, Object[] arguments) {
    logCall(constructor, receiver, arguments);
  }

  @Override
  public void onConstructorReturned(Constructor constructor, Object receiver, Object[] arguments) {
    logReturn(constructor, receiver, arguments, null);
  }

  @Override
  public void onConstructorThrew(Constructor constructor, Object receiver, Object[] arguments, Throwable throwable) {
    logThrow(constructor, receiver, arguments, throwable);
  }

}

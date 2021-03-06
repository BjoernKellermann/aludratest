/*
 * Copyright (C) 2010-2014 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.aludratest.config.AludraTestConfig;
import org.aludratest.exception.AludraTestException;
import org.aludratest.service.AludraContext;
import org.aludratest.service.AludraService;
import org.aludratest.service.ComponentId;
import org.aludratest.service.Condition;
import org.aludratest.service.Interaction;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.Verification;
import org.aludratest.testcase.AludraTestContext;
import org.aludratest.testcase.TestStatus;

/** Provides general functionality for AludraTest.
 * @author Volker Bergmann */
public class AludraTestUtil {

    /** A {@link Map} that provides null value replacements
     *  for primitive data types and their wrapper classes. */
    private static final Map<Class<?>, Object> NULL_REPLACEMENTS;

    static {
        NULL_REPLACEMENTS = new HashMap<Class<?>, Object>();
        NULL_REPLACEMENTS.put(boolean.class, false);
        NULL_REPLACEMENTS.put(byte.class, (byte) 0);
        NULL_REPLACEMENTS.put(short.class, (short) 0);
        NULL_REPLACEMENTS.put(int.class, 0);
        NULL_REPLACEMENTS.put(long.class, (long) 0);
        NULL_REPLACEMENTS.put(float.class, 0f);
        NULL_REPLACEMENTS.put(double.class, 0.);
        NULL_REPLACEMENTS.put(char.class, (char) 0);
        NULL_REPLACEMENTS.put(Boolean.class, false);
        NULL_REPLACEMENTS.put(Byte.class, (byte) 0);
        NULL_REPLACEMENTS.put(Short.class, (short) 0);
        NULL_REPLACEMENTS.put(Integer.class, 0);
        NULL_REPLACEMENTS.put(Long.class, (long) 0);
        NULL_REPLACEMENTS.put(Float.class, 0f);
        NULL_REPLACEMENTS.put(Double.class, 0.);
        NULL_REPLACEMENTS.put(Character.class, (char) 0);
        NULL_REPLACEMENTS.put(String.class, "");
    }

    /** Private constructor of utility class preventing instantiation by other classes */
    private AludraTestUtil() {
    }

    /** Returns a value of the given type which can be used to replace null values. This is useful to provide a non-null value from
     * generic invocations on methods which are expected to return a non-null value (typically a primitive data type). Main
     * purpose is its use in the {@link ControlFlowHandler} (proxy), which is allowed to skip actual method execution but has to
     * return a value that appears valid to the caller.
     * @param type
     * @return null for classes, an 'empty' or zero-like value for primitive return types */
    public static Object nullOrPrimitiveDefault(Class<?> type) {
        return NULL_REPLACEMENTS.get(type);
    }

    /** Wraps the given object with a dynamic proxy that implements a parent interface of the object and transparently add control
     * flow logic using the {@link ControlFlowHandler}.
     * @param object
     * @param interfaceType
     * @param serviceId
     * @param systemConnector
     * @param context
     * @return */
    public static <T, U extends T> T wrapWithControlFlowHandler(U object, Class<T> interfaceType,
            ComponentId<? extends AludraService> serviceId,
            SystemConnector systemConnector, AludraContext context) {
        AludraTestConfig config = context.newComponentInstance(AludraTestConfig.class);
        boolean stopOnException;
        if (Interaction.class.isAssignableFrom(interfaceType)) {
            stopOnException = config.isStopTestCaseOnInteractionException();
        } else if (Verification.class.isAssignableFrom(interfaceType)) {
            stopOnException = config.isStopTestCaseOnVerificationException();
        } else {
            stopOnException = config.isStopTestCaseOnOtherException();
        }
        if (context instanceof AludraTestContext) {
            InvocationHandler invocationHandler = new ControlFlowHandler(object, serviceId, systemConnector,
                    (AludraTestContext) context, stopOnException, !Condition.class.isAssignableFrom(interfaceType));
            return AludraTestUtil.<T> wrapWithInvocationHandler(interfaceType, invocationHandler);
        }
        return object;
    }

    /** Calls the Java dynamic proxy API to dynamically implement the given interfaceType using the given invocationHandler.
     * @param interfaceType
     * @param invocationHandler
     * @return
     * @see InvocationHandler */
    @SuppressWarnings("unchecked")
    public static <T> T wrapWithInvocationHandler(Class<T> interfaceType, InvocationHandler invocationHandler) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            return (T) Proxy.newProxyInstance(classLoader, new Class[] { interfaceType }, invocationHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Unwraps the root cause of an exception.
     * @param t the Throwable to examine
     * @return If the argument is an {@link InvocationTargetException} with a non-null cause, the method returns that cause
     *         exception, otherwise the {@link Throwable} object itself */
    public static Throwable unwrapInvocationTargetException(Throwable t) {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    /** Returns a specific the stack trace element of the current invocation stack.
     * @param levelsUp
     * @return */
    public static StackTraceElement getStackTraceElement(int levelsUp) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = levelsUp + 1;
        if (index >= stackTrace.length) {
            return null;
        }
        return stackTrace[index];
    }

    /** Provides the test status for a given exception.
     * @param t the exception to map to a status
     * @return the test status for a given exception */
    public static TestStatus getTestStatus(Throwable t) {
        if (t instanceof AludraTestException) {
            return ((AludraTestException) t).getTestStatus();
        } else {
            return TestStatus.INCONCLUSIVE;
        }
    }

}

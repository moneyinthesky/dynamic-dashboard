package io.moneyinthesky.dashboard.core.aspects;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.*;
import static java.lang.System.currentTimeMillis;

public class LogExecutionTimeInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LogExecutionTimeInterceptor.class);

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        long start = currentTimeMillis();
        methodInvocation.proceed();
        double totalTime = (currentTimeMillis() - start)/1000d;

        logger.info(buildLogMessage(methodInvocation, totalTime));
        return null;
    }

    private String buildLogMessage(MethodInvocation methodInvocation, double totalTime) {
        String declaringClass = methodInvocation.getMethod().getDeclaringClass().getSimpleName();
        String method = methodInvocation.getMethod().getName();

        String logMessage = methodInvocation.getMethod().getAnnotation(LogExecutionTime.class).value();
        return format("%s.%s: " + logMessage, declaringClass, method, totalTime);
    }
}

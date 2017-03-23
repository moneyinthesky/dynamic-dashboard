package io.moneyinthesky.dashboard.core.aspects;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static org.slf4j.LoggerFactory.getLogger;

public class LogExecutionTimeInterceptor implements MethodInterceptor {

    private static final Logger logger = getLogger(LogExecutionTimeInterceptor.class);

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        long start = currentTimeMillis();
        Object object = methodInvocation.proceed();
        double totalTime = (currentTimeMillis() - start)/1000d;

        logger.info(buildLogMessage(methodInvocation, totalTime));
        return object;
    }

    private static String buildLogMessage(MethodInvocation methodInvocation, double totalTime) {
        String declaringClass = methodInvocation.getMethod().getDeclaringClass().getSimpleName();
        String method = methodInvocation.getMethod().getName();

        return format("%s.%s: Execution time: %f", declaringClass, method, totalTime);
    }
}

package utils.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import play.Logger;

import java.util.Arrays;

/**
 * This class creates log entries, if a use case starts, succeeds or fails
 */
public class LoggingInterceptor implements MethodInterceptor {


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        // Log entry of method -> happy path is always debug
        Logger.debug(
                String.format("Invoking method %s() with parameters %s.",
                        invocation.getMethod().getName(),
                        Arrays.toString(invocation.getArguments())));

        // Log execution time
        long start = System.nanoTime();

        try {
            Object result = invocation.proceed();

            // The successful execution of an usecase should be logged for the support and is always @loglevel info
            Logger.info(
                    String.format("Invocation of method %s() with parameters %s took %.1f ms.",
                            invocation.getMethod().getName(),
                            Arrays.toString(invocation.getArguments()),
                            (System.nanoTime() - start) / 1000000.0));

            return result;

        } catch (Exception e) {
            // At this case, we don't know the "strength" of this error, so it's a warning
            Logger.warn(
                    String.format("Invoking method %s() with parameters %s has thrown an error after %.1f ms..",
                            invocation.getMethod().getName(),
                            Arrays.toString(invocation.getArguments()),
                            (System.nanoTime() - start) / 1000000.0),
                    e);
            throw e;
        }
    }
}

package utils.aop;

import com.avaje.ebean.Ebean;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * This class creates db transactions around our use cases (See interface "UseCases")
 */
public class TransactionInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        try {
            Ebean.beginTransaction();
            Object result = invocation.proceed();
            Ebean.commitTransaction();
            return result;

        } finally {
            Ebean.endTransaction();
        }
    }
}

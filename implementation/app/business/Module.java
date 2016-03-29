package business;


import utils.aop.LoggingInterceptor;
import utils.aop.TransactionInterceptor;
import com.google.inject.AbstractModule;

import static com.google.inject.matcher.Matchers.any;
import static com.google.inject.matcher.Matchers.subclassesOf;

/**
 * Created by david on 21.03.16.
 */
public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bindInterceptor(
                subclassesOf(UseCases.class),
                any(),
                new LoggingInterceptor());

        bindInterceptor(
                subclassesOf(UseCases.class),
                any(),
                new TransactionInterceptor());
    }
}

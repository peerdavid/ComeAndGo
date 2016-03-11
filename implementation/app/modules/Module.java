package modules;

import com.google.inject.AbstractModule;

import controllers.Test;
import controllers.TestImpl;

/**
 * Dependency injection module.
 */
public class Module extends AbstractModule {
   @Override
   protected void configure() {
      bind(Test.class).to(TestImpl.class);
   }
}

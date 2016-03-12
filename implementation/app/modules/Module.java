package modules;

import backend.ExampleInterface;
import com.google.inject.AbstractModule;

import backend.ExampleImpl;

/**
 * Dependency injection module.
 */
public class Module extends AbstractModule {
   @Override
   protected void configure() {
      bind(ExampleInterface.class).to(ExampleImpl.class);
   }
}

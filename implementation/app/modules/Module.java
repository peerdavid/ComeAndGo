package modules;

import backend.Configuration;
import backend.ConfigurationImpl;
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
      bind(Configuration.class).to(ConfigurationImpl.class);
   }
}

package modules;

import com.google.inject.AbstractModule;
import controllers.Test;
import controllers.TestImpl;

/**
 * Created by david on 11.03.16.
 */
public class Module extends AbstractModule {
   @Override
   protected void configure() {
      bind(Test.class).to(TestImpl.class);
   }
}

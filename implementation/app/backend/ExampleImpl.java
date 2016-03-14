package backend;

import com.google.inject.Inject;

/**
 * Example implementation, which will be injected.
 */
public class ExampleImpl implements ExampleInterface {


   @Inject
   public ExampleImpl(){
   }

   @Override
   public String getText() {
      return "Hello World";
   }

   public int add(int a, int b){
      return a + b;
   }
}

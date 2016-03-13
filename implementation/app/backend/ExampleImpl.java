package backend;

/**
 * Example implementation, which will be injected.
 */
public class ExampleImpl implements ExampleInterface {

   @Override
   public String getText() {
      return "Hello World";
   }

   public int add(int a, int b){
      if(a > 0 && b > 0) {
         return a + b;
      } else {
         return a - b;
      }
   }
}

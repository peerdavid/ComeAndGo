package backend;

/**
 * Created by david on 11.03.16.
 */
public class ExampleImpl implements ExampleInterface {

   @Override
   public String getText() {
      return "Hello World";
   }

   public int add(int a, int b){
      return a + b;
   }
}

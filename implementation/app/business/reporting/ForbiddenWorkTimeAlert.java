package business.reporting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan on 14.05.16.
 */
public class ForbiddenWorkTimeAlert {
   private String _message;
   private Type _type;
   private List<String> _arguments = new ArrayList<>();

   public ForbiddenWorkTimeAlert(String message, Type type) {
      _message = message;
      _type = type;
   }

   public enum Type {
      INFORMATION,
      WARNING,
      URGENT
   }

   public void addArguments(String... arguments) {
      for(String s : arguments) {
         _arguments.add(s);
      }
   }

   public String getMessage() {
      return _message;
   }

   public Type getType() {
      return _type;
   }

   public List<String> getArguments() {
      return _arguments;
   }
}

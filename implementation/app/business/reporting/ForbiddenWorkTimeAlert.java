package business.reporting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefan on 14.05.16.
 */
public class ForbiddenWorkTimeAlert {
   private String _message;
   private List<String> _arguments = new ArrayList<>();

   public ForbiddenWorkTimeAlert(String message) {
      this._message = message;
   }

   public void addArguments(String... arguments) {
      for(String s : arguments) {
         _arguments.add(s);
      }
   }

   public String getMessage() {
      return _message;
   }

   public List<String> getArguments() {
      return _arguments;
   }
}

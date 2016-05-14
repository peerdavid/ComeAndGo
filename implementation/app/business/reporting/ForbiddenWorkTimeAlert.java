package business.reporting;

import java.util.List;

/**
 * Created by stefan on 14.05.16.
 */
public class ForbiddenWorkTimeAlert {
   private String _message;
   private List<String> _arguments;

   public ForbiddenWorkTimeAlert(String message, List<String> arguments) {
      this._message = message;
      this._arguments = arguments;
   }

   public String getMessage() {
      return _message;
   }

   public List<String> getArguments() {
      return _arguments;
   }
}

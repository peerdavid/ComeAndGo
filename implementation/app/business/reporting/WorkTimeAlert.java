package business.reporting;

import play.Logger;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by stefan on 14.05.16.
 */
public class WorkTimeAlert {
   private String _message;
   private Type _type;
   private String[] _arguments;

   public WorkTimeAlert(String message, Type type) {
      _message = message;
      _type = type;
   }

   public void addArguments(String... arguments) {
      _arguments = arguments;
   }

   public String getMessage() {
      return _message;
   }

   public Type getType() {
      return _type;
   }

   public String[] getArguments() {
      return _arguments;
   }

    @Override
    public String toString(){
        return Messages.get(getMessage(), getArguments());
    }


   public enum Type {
      INFORMATION,
      WARNING,
      URGENT
   }
}

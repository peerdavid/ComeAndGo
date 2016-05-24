package business.reporting;

import play.i18n.Messages;

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

   public Type getType() {
      return _type;
   }

   @Override
   public String toString(){
        return Messages.get(_message, _arguments);
    }

   public enum Type {
      INFORMATION,
      WARNING,
      URGENT
   }
}

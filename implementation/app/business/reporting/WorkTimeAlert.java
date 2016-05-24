package business.reporting;

import play.i18n.Messages;

/**
 * Created by stefan on 14.05.16.
 */
public class WorkTimeAlert implements Comparable{
   private String _message;
   private Type _type;
   private String[] _arguments;

   public WorkTimeAlert(String message, Type type, String... arguments) {
      _message = message;
      _type = type;
      _arguments = arguments;
   }

   public void addArguments(String... arguments) {
      _arguments = arguments;
   }

   public Type getType() {
      return _type;
   }

   @Override
   public int compareTo(Object o) {
      if(this.getType() == Type.URGENT) {
         return -1;
      }
      if(this.getType() == Type.WARNING && ((WorkTimeAlert)o).getType() == Type.URGENT) {
         return 1;
      }
      if(this.getType() == Type.WARNING && ((WorkTimeAlert)o).getType() == Type.INFORMATION) {
         return -1;
      }
      if(this.getType() == Type.INFORMATION) {
         return 1;
      }
      return 0;
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

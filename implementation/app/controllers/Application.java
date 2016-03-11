package controllers;

import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

/**
 * Created by david on 11.03.16.
 */
public class Application extends Controller {


   private Test _test;

   @Inject
   public Application(Test test){
      _test = test;
   }


   public Result index(){
      String textFromDpendencyInjection = _test.getText();
      return ok(index.render(textFromDpendencyInjection));
   }
}

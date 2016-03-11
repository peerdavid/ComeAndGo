package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

/**
 * Created by david on 11.03.16.
 */
public class Application extends Controller {


   public Application(){

   }


   public static Result index(){
      return ok(index.render("Hello World"));
   }
}

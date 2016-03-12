package controllers;

import com.google.inject.Inject;

import backend.ExampleInterface;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

/**
 * ExampleInterface controller -> to be removed.
 */
public class Application extends Controller {


   private ExampleInterface _fromBackend;

   @Inject
   public Application(ExampleInterface test) {
      _fromBackend = test;
   }


   public Result index() {
      String textFromDpendencyInjection = _fromBackend.getText();
      return ok(index.render(textFromDpendencyInjection));
   }
}

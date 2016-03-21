package controllers;

import business.timetracking.TimeTracking;
import com.google.inject.Inject;

import domain.TimeTrack;
import domain.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.List;

/**
 * ExampleInterface controller -> to be removed.
 */
public class TimeTrackController extends Controller {


   private TimeTracking _timeTracking;


   @Inject
   public TimeTrackController(TimeTracking timeTracking) {
      _timeTracking = timeTracking;
   }


   public Result index() {
      List<TimeTrack> tracks = _timeTracking.readTimeTracks(new User(1));
      return ok(index.render(String.valueOf(tracks.size())));
   }
}

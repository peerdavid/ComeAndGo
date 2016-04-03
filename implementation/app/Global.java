import play.Application;
import play.GlobalSettings;
import play.Logger;

/**
 * Created by david on 03.04.16.
 */
public class Global extends GlobalSettings {

    public Global(){

    }


    @Override
    public void onStart(Application app){
        super.onStart(app);

        Logger.info("STARTING");
    }


    @Override
    public void onStop(Application app){
        super.onStop(app);
    }
}

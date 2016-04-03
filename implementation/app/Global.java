import business.usermanagement.AuthenticatorService;
import business.usermanagement.Module;
import business.usermanagement.authorization.SecurityRole;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.pac4j.core.authorization.RequireAnyRoleAuthorizer;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.play.ApplicationLogoutController;
import org.pac4j.play.CallbackController;
import org.pac4j.play.http.DefaultHttpActionAdapter;
import org.pac4j.play.store.PlayCacheStore;
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

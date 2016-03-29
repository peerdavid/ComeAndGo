package business.usermanagement;

import com.google.inject.AbstractModule;
import org.pac4j.core.authorization.RequireAnyRoleAuthorizer;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.play.ApplicationLogoutController;
import org.pac4j.play.CallbackController;
import org.pac4j.play.http.DefaultHttpActionAdapter;
import org.pac4j.play.store.PlayCacheStore;
import business.usermanagement.authentication.CustomUsernamePasswordAuthenticator;
import business.usermanagement.authorization.SecurityRole;

/**
 * Created by sebastian on 3/28/16.
 */
public class Module extends AbstractModule {
    @Override
    protected void configure() {
        final FormClient client = new FormClient("/login", new CustomUsernamePasswordAuthenticator());
        client.setName("default");

        final Config config = new Config(client);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>(SecurityRole.ROLE_ADMIN));
        config.addAuthorizer("user", new RequireAnyRoleAuthorizer<>(SecurityRole.ROLE_USER));
        config.setHttpActionAdapter(new DefaultHttpActionAdapter());
        bind(Config.class).toInstance(config);

        // set profile timeout to 2h instead of the 1h default
        PlayCacheStore store = new PlayCacheStore();
        store.setProfileTimeout(7200);
        config.setSessionStore(store);

        final CallbackController callbackController = new CallbackController();
        callbackController.setDefaultUrl("/");
        bind(CallbackController.class).toInstance(callbackController);

        // logout
        final ApplicationLogoutController logoutController = new ApplicationLogoutController();
        logoutController.setDefaultUrl("/");
        bind(ApplicationLogoutController.class).toInstance(logoutController);
    }
}

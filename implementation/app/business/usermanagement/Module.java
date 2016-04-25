package business.usermanagement;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import infrastructure.UserRepository;
import org.pac4j.core.authorization.RequireAnyRoleAuthorizer;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.play.ApplicationLogoutController;
import org.pac4j.play.CallbackController;
import org.pac4j.play.http.DefaultHttpActionAdapter;
import org.pac4j.play.store.PlayCacheStore;

/**
 * Created by sebastian on 3/28/16.
 */
public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserService.class).to(UserServiceImpl.class);
        bind(UserManagement.class).to(UserManagementFacade.class);
        bind(InternalUserManagement.class).to(UserServiceImpl.class);

        Injector injector = Guice.createInjector(new infrastructure.Module());
        UserRepository userRepository = injector.getInstance(UserRepository.class);

        UserService userService = new UserServiceImpl(userRepository);
        final FormClient client = new FormClient("/login", userService);
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

package business.notification;

import com.google.inject.AbstractModule;

/**
 * Created by david on 09.05.16.
 */
public class ModuleInternal extends AbstractModule {

    @Override
    protected void configure() {
        bind(NotificationSender.class).to(NotificationService.class);
    }
}

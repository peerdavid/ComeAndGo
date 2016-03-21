package business.notification;

import com.google.inject.AbstractModule;

/**
 * Created by david on 22.03.16.
 */
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(NotificationReader.class).to(NotificationReaderFacade.class);
        bind(NotificationSender.class).to(NotificationService.class);
    }
}

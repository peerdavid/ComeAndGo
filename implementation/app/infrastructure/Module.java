package infrastructure;

import com.google.inject.AbstractModule;

/**
 * Created by david on 21.03.16.
 */
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(NotificationRepository.class).to(NotificationRepositoryImpl.class);
        bind(TimeTrackingRepository.class).to(TimeTrackingRepositoryImpl.class);
        bind(InternalUserManagement.class).to(UserRepositoryImpl.class);
    }
}

package infrastructure;

import com.google.inject.AbstractModule;

/**
 * Created by david on 21.03.16.
 */
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(NotificationRepository.class).to(NotificationRepositoryImpl.class);
        bind(TimeOffRepository.class).to(TimeOffRepositoryImpl.class);
        bind(TimeTrackingRepository.class).to(TimeTrackingRepositoryImpl.class);
        bind(UserRepository.class).to(UserRepositoryImpl.class);
        bind(PayoutRepository.class).to(PayoutRepositoryImpl.class);
    }
}

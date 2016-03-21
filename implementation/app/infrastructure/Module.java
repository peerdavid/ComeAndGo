package infrastructure;

import com.google.inject.AbstractModule;

/**
 * Created by david on 21.03.16.
 */
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(TimeTrackingRepository.class).to(TimeTrackingRepositoryImpl.class);
    }
}

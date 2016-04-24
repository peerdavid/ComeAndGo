package business.timetracking;

import com.google.inject.AbstractModule;

/**
 * Created by david on 21.03.16.
 */
public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(TimeTrackingService.class).to(TimeTrackingServiceImpl.class);
        bind(TimeTracking.class).to(TimeTrackingFacade.class);
        bind(TimeOffService.class).to(TimeOffServiceImpl.class);
    }
}

package business.reporting;

import com.google.inject.AbstractModule;

/**
 * Created by david on 02.05.16.
 */
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(CollectiveAggreement.class).to(CollectiveAggreementImpl.class);
        bind(ReportingService.class).to(ReportingServiceImpl.class);
        bind(Reporting.class).to(ReportingFacade.class);
    }
}
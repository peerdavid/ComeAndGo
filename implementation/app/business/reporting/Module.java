package business.reporting;

import com.google.inject.AbstractModule;

/**
 * Created by david on 02.05.16.
 */
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(CollectiveAgreement.class).to(CollectiveAgreementImpl.class);
        bind(ReportingService.class).to(ReportingServiceImpl.class);
        bind(Reporting.class).to(ReportingFacade.class);
    }
}
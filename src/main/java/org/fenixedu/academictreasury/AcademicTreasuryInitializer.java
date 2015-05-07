package org.fenixedu.academictreasury;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.academictreasury.domain.treasury.AcademicTreasuryBridgeImpl;

@WebListener
public class AcademicTreasuryInitializer implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        final AcademicTreasuryBridgeImpl impl = new AcademicTreasuryBridgeImpl();
        impl.registerNewAcademicServiceRequestSituationHandler();
        
        TreasuryBridgeAPIFactory.registerImplementation(impl);
    }

}

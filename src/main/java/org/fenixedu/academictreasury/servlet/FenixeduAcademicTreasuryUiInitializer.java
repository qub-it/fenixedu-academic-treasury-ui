package org.fenixedu.academictreasury.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FenixeduAcademicTreasuryUiInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        setupForwardPaymentControllers();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    private void setupForwardPaymentControllers() {
    }
}
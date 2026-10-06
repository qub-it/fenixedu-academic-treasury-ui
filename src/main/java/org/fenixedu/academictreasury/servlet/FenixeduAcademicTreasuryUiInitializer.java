package org.fenixedu.academictreasury.servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

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
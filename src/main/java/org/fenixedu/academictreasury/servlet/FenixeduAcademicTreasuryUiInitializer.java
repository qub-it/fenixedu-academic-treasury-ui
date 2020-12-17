package org.fenixedu.academictreasury.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academictreasury.services.AcademicTreasuryPlataformDependentServicesFactory;
import org.fenixedu.academictreasury.ui.document.forwardpayments.implementations.onlinepaymentsgateway.sibs.SibsOnlinePaymentsGatewayForwardPaymentController;
import org.fenixedu.treasury.domain.forwardpayments.implementations.IForwardPaymentController;
import org.fenixedu.treasury.domain.forwardpayments.implementations.TPAVirtualImplementation;
import org.fenixedu.treasury.domain.forwardpayments.payline.PaylineConfiguration;
import org.fenixedu.treasury.domain.sibspaymentsgateway.integration.SibsPaymentsGateway;
import org.fenixedu.treasury.ui.document.forwardpayments.implementations.PaylineController;
import org.fenixedu.treasury.ui.document.forwardpayments.implementations.TPAVirtualController;

@WebListener
public class FenixeduAcademicTreasuryUiInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        AcademicTreasuryPlataformDependentServicesFactory.registerImplementation(new FenixEduAcademicTreasuryPlatformDependentServices());
        
        setupForwardPaymentControllers();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    private void setupForwardPaymentControllers() {
        IForwardPaymentController.registerForwardPaymentController(PaylineConfiguration.class, PaylineController.class);
        IForwardPaymentController.registerForwardPaymentController(TPAVirtualImplementation.class, TPAVirtualController.class);
        IForwardPaymentController.registerForwardPaymentController(SibsPaymentsGateway.class,
                SibsOnlinePaymentsGatewayForwardPaymentController.class);
    }
}
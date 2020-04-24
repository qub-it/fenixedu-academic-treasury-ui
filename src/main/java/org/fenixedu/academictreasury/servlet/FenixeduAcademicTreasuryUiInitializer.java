package org.fenixedu.academictreasury.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academictreasury.domain.forwardpayments.implementations.onlinepaymentsgateway.sibs.SibsOnlinePaymentsGatewayForwardImplementation;
import org.fenixedu.academictreasury.ui.document.forwardpayments.implementations.onlinepaymentsgateway.sibs.SibsOnlinePaymentsGatewayForwardPaymentController;
import org.fenixedu.treasury.domain.forwardpayments.implementations.IForwardPaymentController;
import org.fenixedu.treasury.domain.forwardpayments.implementations.PaylineImplementation;
import org.fenixedu.treasury.domain.forwardpayments.implementations.TPAVirtualImplementation;
import org.fenixedu.treasury.ui.document.forwardpayments.implementations.PaylineController;
import org.fenixedu.treasury.ui.document.forwardpayments.implementations.TPAVirtualController;

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
        IForwardPaymentController.registerForwardPaymentController(PaylineImplementation.class, PaylineController.class);
        IForwardPaymentController.registerForwardPaymentController(TPAVirtualImplementation.class, TPAVirtualController.class);
        IForwardPaymentController.registerForwardPaymentController(SibsOnlinePaymentsGatewayForwardImplementation.class, 
                SibsOnlinePaymentsGatewayForwardPaymentController.class);
    }
}
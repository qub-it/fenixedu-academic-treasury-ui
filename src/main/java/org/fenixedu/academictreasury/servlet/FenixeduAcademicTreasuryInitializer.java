package org.fenixedu.academictreasury.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academictreasury.domain.emoluments.ServiceRequestMapEntry;
import org.fenixedu.academictreasury.domain.forwardpayments.implementations.onlinepaymentsgateway.sibs.SibsOnlinePaymentsGatewayForwardImplementation;
import org.fenixedu.academictreasury.services.AcademicTreasuryPlataformDependentServicesFactory;
import org.fenixedu.academictreasury.services.FenixEduAcademicTreasuryPlatformDependentServices;
import org.fenixedu.academictreasury.ui.document.forwardpayments.implementations.onlinepaymentsgateway.sibs.SibsOnlinePaymentsGatewayForwardPaymentController;
import org.fenixedu.treasury.domain.forwardpayments.implementations.IForwardPaymentController;
import org.fenixedu.treasury.domain.forwardpayments.implementations.IForwardPaymentImplementation;
import org.fenixedu.treasury.domain.forwardpayments.implementations.PaylineImplementation;
import org.fenixedu.treasury.domain.forwardpayments.implementations.TPAVirtualImplementation;
import org.fenixedu.treasury.ui.document.forwardpayments.implementations.PaylineController;
import org.fenixedu.treasury.ui.document.forwardpayments.implementations.TPAVirtualController;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

@WebListener
public class FenixeduAcademicTreasuryInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        AcademicTreasuryPlataformDependentServicesFactory.registerImplementation(new FenixEduAcademicTreasuryPlatformDependentServices());
        
        setupListenerForServiceRequestTypeDelete();
        setupForwardPaymentControllers();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    private void setupListenerForServiceRequestTypeDelete() {
        FenixFramework.getDomainModel().registerDeletionListener(ServiceRequestType.class,
                new DeletionListener<ServiceRequestType>() {
                    @Override
                    public void deleting(ServiceRequestType serviceRequestType) {
                        for (ServiceRequestMapEntry mapEntry : serviceRequestType.getServiceRequestMapEntriesSet()) {
                            mapEntry.delete();
                        }
                    }
                });
    }
    
    private void setupForwardPaymentControllers() {
        IForwardPaymentController.registerForwardPaymentController(PaylineImplementation.class, PaylineController.class);
        IForwardPaymentController.registerForwardPaymentController(TPAVirtualImplementation.class, TPAVirtualController.class);
        IForwardPaymentController.registerForwardPaymentController(SibsOnlinePaymentsGatewayForwardImplementation.class, 
                SibsOnlinePaymentsGatewayForwardPaymentController.class);
    }
}
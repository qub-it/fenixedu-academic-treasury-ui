package org.fenixedu.academictreasury.ui.document.forwardpayments.implementations.onlinepaymentsgateway.sibs;

import static java.lang.String.format;
import static org.fenixedu.treasury.util.TreasuryConstants.isSameCountryCode;
import static org.fenixedu.treasury.util.TreasuryConstants.treasuryBundle;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.forwardpayments.implementations.onlinepaymentsgateway.sibs.SibsOnlinePaymentsGatewayForwardImplementation;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.util.AcademicTreasuryConstants;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.AdhocCustomer;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.exceptions.TreasuryDomainException;
import org.fenixedu.treasury.domain.forwardpayments.ForwardPayment;
import org.fenixedu.treasury.domain.sibsonlinepaymentsgateway.SibsBillingAddressBean;
import org.fenixedu.treasury.dto.forwardpayments.ForwardPaymentStatusBean;
import org.fenixedu.treasury.ui.TreasuryController;
import org.fenixedu.treasury.ui.document.forwardpayments.IForwardPaymentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;

import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(app = TreasuryController.class, title = "label.title.returnSibsOnlinePaymentsGatewayForwardPayment",
        accessGroup = "logged")
@RequestMapping(SibsOnlinePaymentsGatewayForwardPaymentController.CONTROLLER_URL)
public class SibsOnlinePaymentsGatewayForwardPaymentController extends AcademicTreasuryBaseController
        implements IForwardPaymentController {
    private static final String DEFAULT_ZIP_CODE = "0000-000";

    public static final String CONTROLLER_URL = "/treasury/document/forwardpayments/sibsonlinepaymentsgateway";
    private static final String JSP_PATH =
            "/academicTreasury/document/forwardpayments/forwardpayment/implementations/sibsonlinepaymentsgateway";

    private static final String PROCESS_FORWARD_PAYMENT_URI = "/processforwardpayment";

    private static final Logger logger = LoggerFactory.getLogger(SibsOnlinePaymentsGatewayForwardPaymentController.class);

    private static final String _SELECT_PHYSICAL_ADDRESS_URI = "/selectphysicaladdress";
    public static final String SELECT_PHYSICAL_ADDRESS_URL = CONTROLLER_URL + _SELECT_PHYSICAL_ADDRESS_URI;

    @Override
    public String processforwardpayment(final ForwardPayment forwardPayment, final Model model,
            final HttpServletResponse response, final HttpSession session) {
        final DebtAccount debtAccount = forwardPayment.getDebtAccount();
        final String debtAccountUrl = (String) session.getAttribute("debtAccountUrl");

        if(debtAccount.getCustomer().isAdhocCustomer()) {
            return String.format("redirect:%s/%s", CONTINUE_PROCESS_FORWARD_PAYMENT_URL, forwardPayment.getExternalId());
        }
        
        final Person person = ((PersonCustomer) debtAccount.getCustomer()).getAssociatedPerson();
        
        model.addAttribute("debtAccountUrl", debtAccountUrl);
        model.addAttribute("forwardPayment", forwardPayment);
        model.addAttribute("forwardPaymentConfiguration", forwardPayment.getForwardPaymentConfiguration());
        model.addAttribute("debtAccount", debtAccount);
        model.addAttribute("logosPage", forwardPayment.getForwardPaymentConfiguration().getLogosJspPageFile());
        model.addAttribute("physicalAddresses", 
                person.getValidAddressesForFiscalData().stream()
                .collect(Collectors.toList()));

        return jspPage(_SELECT_PHYSICAL_ADDRESS_URI);
    }

    @RequestMapping(value = _SELECT_PHYSICAL_ADDRESS_URI + "/{forwardPaymentId}", method = RequestMethod.POST)
    public String selectphysicaladdress(
            @PathVariable("forwardPaymentId") final ForwardPayment forwardPayment,
            @RequestParam("physicalAddressId") final PhysicalAddress physicalAddress, 
            final Model model,
            final HttpServletResponse response, final HttpSession session, final RedirectAttributes redirectAttributes) {

        if (physicalAddress == null) {
            addErrorMessage(AcademicTreasuryConstants.academicTreasuryBundle("error.SibsOnlinePaymentsGatewayForwardPaymentController.select.address"), model);
            return processforwardpayment(forwardPayment, model, response, session);
        }
        
        return String.format("redirect:%s/%s/%s", CONTINUE_PROCESS_FORWARD_PAYMENT_URL, 
                forwardPayment.getExternalId(), physicalAddress.getExternalId());
    }

    private static final String _CONTINUE_PROCESS_FORWARD_PAYMENT_URI = "/continueProcessForwardPayment";
    public static final String CONTINUE_PROCESS_FORWARD_PAYMENT_URL = CONTROLLER_URL + _CONTINUE_PROCESS_FORWARD_PAYMENT_URI;
    
    @RequestMapping(value = _CONTINUE_PROCESS_FORWARD_PAYMENT_URI + "/{forwardPaymentId}")
    public String continueProcessForwardPayment(@PathVariable("forwardPaymentId") final ForwardPayment forwardPayment, 
            final Model model, final HttpServletResponse response, final HttpSession session) {
        return continueProcessForwardPayment(forwardPayment, null, model, response, session);
    }
    
    @RequestMapping(value = _CONTINUE_PROCESS_FORWARD_PAYMENT_URI + "/{forwardPaymentId}/{physicalAddressId}")
    public String continueProcessForwardPayment(
            @PathVariable("forwardPaymentId") final ForwardPayment forwardPayment, 
            @PathVariable("physicalAddressId") final PhysicalAddress physicalAddress, 
            final Model model, final HttpServletResponse response, final HttpSession session) {

        checkPermissions(forwardPayment, physicalAddress);
        
        try {
            final String debtAccountUrl = (String) session.getAttribute("debtAccountUrl");

            if (forwardPayment.getCurrentState() != null && forwardPayment.getCurrentState().isRejected()) {
                throw new TreasuryDomainException("error.ForwardPayment.not.in.active.state");
            }

            if (forwardPayment.getCurrentState() != null && forwardPayment.getCurrentState().isPayed()) {
                throw new TreasuryDomainException("error.ForwardPayment.not.in.active.state");
            }

            final SibsOnlinePaymentsGatewayForwardImplementation impl =
                    (SibsOnlinePaymentsGatewayForwardImplementation) forwardPayment.getForwardPaymentConfiguration()
                            .implementation();

            final SibsBillingAddressBean billingAddressBean = new SibsBillingAddressBean();
            
            if(forwardPayment.getDebtAccount().getCustomer().isAdhocCustomer()) {
                billingAddressBean.setAddressCountryCode(forwardPayment.getDebtAccount().getCustomer().getAddressCountryCode());
                billingAddressBean.setCity(billingCity((AdhocCustomer) forwardPayment.getDebtAccount().getCustomer()));
                billingAddressBean.setAddress(forwardPayment.getDebtAccount().getCustomer().getAddress());
                billingAddressBean.setZipCode(forwardPayment.getDebtAccount().getCustomer().getZipCode());
            } else if(physicalAddress != null) {
                billingAddressBean.setAddressCountryCode(PersonCustomer.addressCountryCode(physicalAddress));
                billingAddressBean.setCity(billingCity(physicalAddress));
                billingAddressBean.setAddress(PersonCustomer.address(physicalAddress));
                billingAddressBean.setZipCode(PersonCustomer.zipCode(physicalAddress));
            } else {
                throw new AcademicTreasuryDomainException("error.SibsOnlinePaymentsGatewayForwardPaymentController.missing.physical.address");
            }
            
            if(StringUtils.isEmpty(billingAddressBean.getZipCode())) {
                billingAddressBean.setZipCode(DEFAULT_ZIP_CODE);
            }
            
            final ForwardPaymentStatusBean bean = impl.prepareCheckout(forwardPayment, billingAddressBean);

            if (!bean.isInvocationSuccess()) {
                return format("redirect:%s", forwardPayment.getForwardPaymentInsuccessUrl());
            }

            model.addAttribute("debtAccountUrl", debtAccountUrl);
            model.addAttribute("paymentScriptUrl", impl.getPaymentURL(forwardPayment));
            model.addAttribute("forwardPaymentConfiguration", forwardPayment.getForwardPaymentConfiguration());
            model.addAttribute("debtAccount", forwardPayment.getDebtAccount());
            model.addAttribute("checkoutId", forwardPayment.getSibsCheckoutId());
            model.addAttribute("shopperResultUrl", impl.getReturnURL(forwardPayment));
            model.addAttribute("paymentBrands", bean.getSibsOnlinePaymentBrands());
            model.addAttribute("logosPage", forwardPayment.getForwardPaymentConfiguration().getLogosJspPageFile());

            return jspPage(PROCESS_FORWARD_PAYMENT_URI);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);

            return String.format("redirect:%s", forwardPayment.getForwardPaymentInsuccessUrl());
        }
    }

    private void checkPermissions(final ForwardPayment forwardPayment, final PhysicalAddress physicalAddress) {
        if(forwardPayment.getDebtAccount().getCustomer().isPersonCustomer()) {
            PersonCustomer personCustomer = (PersonCustomer) forwardPayment.getDebtAccount().getCustomer();
            if(personCustomer.getAssociatedPerson() != physicalAddress.getParty()) {
                throw new SecurityException("not authorized");
            }
        }
    }

    private String billingCity(AdhocCustomer customer) {
        if(!StringUtils.isEmpty(customer.getDistrictSubdivision())) {
            return customer.getDistrictSubdivision();
        }
        
        return customer.getRegion();
    }

    private String billingCity(PhysicalAddress physicalAddress) {
        if(!StringUtils.isEmpty(PersonCustomer.districtSubdivision(physicalAddress))) {
            return PersonCustomer.districtSubdivision(physicalAddress);
        }
        
        return PersonCustomer.region(physicalAddress);
    }

    private static final String RETURN_FORWARD_PAYMENT_URI = "/returnforwardpayment";
    public static final String RETURN_FORWARD_PAYMENT_URL = CONTROLLER_URL + RETURN_FORWARD_PAYMENT_URI;

    @RequestMapping(value = RETURN_FORWARD_PAYMENT_URI + "/{forwardPaymentId}", method = RequestMethod.GET)
    public String returnforwardpayment(@PathVariable("forwardPaymentId") final ForwardPayment forwardPayment,
            @RequestParam("id") final String sibsCheckoutId, final Model model, final HttpServletResponse response,
            final HttpSession session) {
        try {
            session.setAttribute("debtAccountUrl", null);

            final SibsOnlinePaymentsGatewayForwardImplementation impl =
                    (SibsOnlinePaymentsGatewayForwardImplementation) forwardPayment.getForwardPaymentConfiguration()
                            .implementation();

            final ForwardPaymentStatusBean bean = impl.paymentStatusByCheckoutId(forwardPayment);

            // First of all save sibsTransactionId
            FenixFramework.atomic(() -> {
                forwardPayment.setSibsTransactionId(bean.getTransactionId());
            });

            if (bean.isInPayedState()) {
                FenixFramework.atomic(() -> {
                    forwardPayment.advanceToPayedState(bean.getStatusCode(), bean.getStatusMessage(), bean.getPayedAmount(),
                            bean.getTransactionDate(), bean.getTransactionId(), null, bean.getRequestBody(),
                            bean.getResponseBody(), "");
                });

                return String.format("redirect:%s", forwardPayment.getForwardPaymentSuccessUrl());
            } else {
                FenixFramework.atomic(() -> {
                    forwardPayment.reject(bean.getStatusCode(), bean.getStatusMessage(), bean.getRequestBody(),
                            bean.getResponseBody());
                });

                return String.format("redirect:%s", forwardPayment.getForwardPaymentInsuccessUrl());
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);

            return String.format("redirect:%s", forwardPayment.getForwardPaymentInsuccessUrl());
        }
    }

    private String jspPage(final String page) {
        return JSP_PATH + page;
    }

}

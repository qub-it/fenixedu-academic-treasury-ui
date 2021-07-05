package org.fenixedu.academictreasury.ui.document.forwardpayments.implementations.onlinepaymentsgateway.sibs;

import static java.lang.String.format;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.util.AcademicTreasuryConstants;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.AdhocCustomer;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.exceptions.TreasuryDomainException;
import org.fenixedu.treasury.domain.forwardpayments.ForwardPaymentRequest;
import org.fenixedu.treasury.domain.forwardpayments.implementations.IForwardPaymentController;
import org.fenixedu.treasury.domain.sibsonlinepaymentsgateway.SibsBillingAddressBean;
import org.fenixedu.treasury.domain.sibspaymentsgateway.SibsPaymentsGatewayLog;
import org.fenixedu.treasury.domain.sibspaymentsgateway.integration.SibsPaymentsGateway;
import org.fenixedu.treasury.dto.forwardpayments.ForwardPaymentStatusBean;
import org.fenixedu.treasury.ui.TreasuryController;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(app = TreasuryController.class, title = "label.title.returnSibsOnlinePaymentsGatewayForwardPayment",
        accessGroup = "logged")
@RequestMapping(SibsOnlinePaymentsGatewayForwardPaymentController.CONTROLLER_URL)
public class SibsOnlinePaymentsGatewayForwardPaymentController extends AcademicTreasuryBaseController
        implements IForwardPaymentController {

    private static final String DEFAULT_ZIP_CODE = "0000-000";
    private static final String DEFAULT_ADDRESS = "Desconhecido";

    public static final String CONTROLLER_URL = "/treasury/document/forwardpayments/sibsonlinepaymentsgateway";
    private static final String JSP_PATH =
            "/academicTreasury/document/forwardpayments/forwardpayment/implementations/sibsonlinepaymentsgateway";

    private static final String PROCESS_FORWARD_PAYMENT_URI = "/processforwardpayment";

    private static final Logger logger = LoggerFactory.getLogger(SibsOnlinePaymentsGatewayForwardPaymentController.class);

    private static final String _SELECT_PHYSICAL_ADDRESS_URI = "/selectphysicaladdress";
    public static final String SELECT_PHYSICAL_ADDRESS_URL = CONTROLLER_URL + _SELECT_PHYSICAL_ADDRESS_URI;

    @Override
    // TODO Check code Refactor/20210624-MergeWithISCTE
    // Change model arg type to java.lang.Object
    public String processforwardpayment(ForwardPaymentRequest forwardPayment, Object model, HttpServletResponse response,
            HttpSession session) {
        final DebtAccount debtAccount = forwardPayment.getDebtAccount();
        final String debtAccountUrl = (String) session.getAttribute("debtAccountUrl");

        if (debtAccount.getCustomer().isAdhocCustomer()) {
            continueProcessForwardPayment(forwardPayment, (Model) model, response, session);
        }

        final Person person = ((PersonCustomer) debtAccount.getCustomer()).getAssociatedPerson();
        SibsPaymentsGateway gateway = (SibsPaymentsGateway) forwardPayment.getDigitalPaymentPlatform();

        ((Model) model).addAttribute("debtAccountUrl", debtAccountUrl);
        ((Model) model).addAttribute("forwardPayment", forwardPayment);
        ((Model) model).addAttribute("forwardPaymentConfiguration", gateway);
        ((Model) model).addAttribute("debtAccount", debtAccount);
        ((Model) model).addAttribute("logosPage", gateway.getLogosJspPage());
        ((Model) model).addAttribute("physicalAddresses",
                person.getValidAddressesForFiscalData().stream().collect(Collectors.toList()));

        return jspPage(_SELECT_PHYSICAL_ADDRESS_URI);
    }

    @RequestMapping(value = _SELECT_PHYSICAL_ADDRESS_URI + "/{forwardPaymentId}", method = RequestMethod.POST)
    public String selectphysicaladdress(@PathVariable("forwardPaymentId") ForwardPaymentRequest forwardPayment,
            @RequestParam("physicalAddressId") PhysicalAddress physicalAddress, Model model, HttpServletResponse response,
            HttpSession session, RedirectAttributes redirectAttributes) {

        if (physicalAddress == null) {
            addErrorMessage(AcademicTreasuryConstants
                    .academicTreasuryBundle("error.SibsOnlinePaymentsGatewayForwardPaymentController.select.address"), model);
            return processforwardpayment(forwardPayment, model, response, session);
        }

        return continueProcessForwardPayment(forwardPayment, physicalAddress, model, response, session);
    }

    private static final String _CONTINUE_PROCESS_FORWARD_PAYMENT_URI = "/continueProcessForwardPayment";
    public static final String CONTINUE_PROCESS_FORWARD_PAYMENT_URL = CONTROLLER_URL + _CONTINUE_PROCESS_FORWARD_PAYMENT_URI;

    @RequestMapping(value = _CONTINUE_PROCESS_FORWARD_PAYMENT_URI + "/{forwardPaymentId}")
    public String continueProcessForwardPayment(@PathVariable("forwardPaymentId") ForwardPaymentRequest forwardPayment,
            Model model, HttpServletResponse response, HttpSession session) {
        return continueProcessForwardPayment(forwardPayment, null, model, response, session);
    }

    @RequestMapping(value = _CONTINUE_PROCESS_FORWARD_PAYMENT_URI + "/{forwardPaymentId}/{physicalAddressId}")
    public String continueProcessForwardPayment(@PathVariable("forwardPaymentId") ForwardPaymentRequest forwardPayment,
            @PathVariable("physicalAddressId") PhysicalAddress physicalAddress, Model model, HttpServletResponse response,
            HttpSession session) {

        checkPermissions(forwardPayment, physicalAddress);

        try {
            if (!forwardPayment.isInCreatedState()) {
                AcademicTreasuryDomainException e = new AcademicTreasuryDomainException(
                        "error.SibsOnlinePaymentsGatewayForwardPaymentController.invalid.state.restart.process.again.01");

                FenixFramework.atomic(() -> {
                    forwardPayment.reject("continueProcessForwardPayment", "", e.getLocalizedMessage(), "", "").logException(e);
                });
                throw e;
            }

            if (!StringUtils.isEmpty(forwardPayment.getMerchantTransactionId())) {
                AcademicTreasuryDomainException e = new AcademicTreasuryDomainException(
                        "error.SibsOnlinePaymentsGatewayForwardPaymentController.invalid.state.restart.process.again.02");
                FenixFramework.atomic(() -> {
                    forwardPayment.reject("continueProcessForwardPayment", "", e.getLocalizedMessage(), "", "").logException(e);
                });
                throw e;
            }

            String debtAccountUrl = (String) session.getAttribute("debtAccountUrl");

            if (forwardPayment.isInRejectedState()) {
                throw new TreasuryDomainException("error.ForwardPayment.not.in.active.state");
            }

            if (forwardPayment.isInPaidState()) {
                throw new TreasuryDomainException("error.ForwardPayment.not.in.active.state");
            }

            SibsPaymentsGateway impl = (SibsPaymentsGateway) forwardPayment.getDigitalPaymentPlatform();

            SibsBillingAddressBean billingAddressBean = new SibsBillingAddressBean();

            if (forwardPayment.getDebtAccount().getCustomer().isAdhocCustomer()) {
                billingAddressBean.setAddressCountryCode(forwardPayment.getDebtAccount().getCustomer().getAddressCountryCode());
                billingAddressBean.setCity(billingCity((AdhocCustomer) forwardPayment.getDebtAccount().getCustomer()));
                billingAddressBean.setAddress(forwardPayment.getDebtAccount().getCustomer().getAddress());
                billingAddressBean.setZipCode(forwardPayment.getDebtAccount().getCustomer().getZipCode());
            } else if (physicalAddress != null) {
                billingAddressBean.setAddressCountryCode(PersonCustomer.addressCountryCode(physicalAddress));
                billingAddressBean.setCity(billingCity(physicalAddress));
                billingAddressBean.setAddress(PersonCustomer.address(physicalAddress));
                billingAddressBean.setZipCode(PersonCustomer.zipCode(physicalAddress));
            } else {
                throw new AcademicTreasuryDomainException(
                        "error.SibsOnlinePaymentsGatewayForwardPaymentController.missing.physical.address");
            }

            if (StringUtils.isEmpty(billingAddressBean.getZipCode())) {
                billingAddressBean.setZipCode(DEFAULT_ZIP_CODE);
            }

            if (StringUtils.isEmpty(billingAddressBean.getAddress())) {
                billingAddressBean.setAddress(DEFAULT_ADDRESS);
            }

            if (StringUtils.isEmpty(billingAddressBean.getCity())) {
                billingAddressBean.setCity(DEFAULT_ADDRESS);
            }

            final ForwardPaymentStatusBean bean = impl.prepareCheckout(forwardPayment, billingAddressBean);

            if (!bean.isInvocationSuccess()) {
                return format("redirect:%s", forwardPayment.getForwardPaymentInsuccessUrl());
            }

            model.addAttribute("debtAccountUrl", debtAccountUrl);
            model.addAttribute("paymentScriptUrl", impl.getPaymentURL(forwardPayment));
            model.addAttribute("forwardPaymentConfiguration", forwardPayment.getDigitalPaymentPlatform());
            model.addAttribute("debtAccount", forwardPayment.getDebtAccount());
            model.addAttribute("checkoutId", forwardPayment.getCheckoutId());
            model.addAttribute("shopperResultUrl", impl.getReturnURL(forwardPayment));
            model.addAttribute("paymentBrands", bean.getSibsOnlinePaymentBrands());
            model.addAttribute("logosPage", impl.getLogosJspPage());

            return jspPage(PROCESS_FORWARD_PAYMENT_URI);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);

            return String.format("redirect:%s", forwardPayment.getForwardPaymentInsuccessUrl());
        }
    }

    private void checkPermissions(ForwardPaymentRequest forwardPayment, PhysicalAddress physicalAddress) {
        if (forwardPayment.getDebtAccount().getCustomer().isPersonCustomer()) {
            PersonCustomer personCustomer = (PersonCustomer) forwardPayment.getDebtAccount().getCustomer();
            if (personCustomer.getAssociatedPerson() != physicalAddress.getParty()) {
                throw new SecurityException("not authorized");
            }
        }
    }

    private String billingCity(AdhocCustomer customer) {
        if (!StringUtils.isEmpty(customer.getDistrictSubdivision())) {
            return customer.getDistrictSubdivision();
        }

        return customer.getRegion();
    }

    private String billingCity(PhysicalAddress physicalAddress) {
        if (!StringUtils.isEmpty(PersonCustomer.districtSubdivision(physicalAddress))) {
            return PersonCustomer.districtSubdivision(physicalAddress);
        }

        return PersonCustomer.region(physicalAddress);
    }

    private static final String RETURN_FORWARD_PAYMENT_URI = "/returnforwardpayment";
    public static final String RETURN_FORWARD_PAYMENT_URL = CONTROLLER_URL + RETURN_FORWARD_PAYMENT_URI;

    @RequestMapping(value = RETURN_FORWARD_PAYMENT_URI + "/{forwardPaymentId}", method = RequestMethod.GET)
    public String returnforwardpayment(@PathVariable("forwardPaymentId") ForwardPaymentRequest forwardPayment,
            @RequestParam("id") String sibsCheckoutId, Model model, HttpServletResponse response, HttpSession session) {
        try {
            session.setAttribute("debtAccountUrl", null);

            SibsPaymentsGateway impl = (SibsPaymentsGateway) forwardPayment.getDigitalPaymentPlatform();

            DateTime requestSendDate = new DateTime();

            final ForwardPaymentStatusBean bean = impl.paymentStatusByCheckoutId(forwardPayment);

            DateTime requestReceiveDate = new DateTime();

            // First of all save sibsTransactionId
            FenixFramework.atomic(() -> {
                forwardPayment.setTransactionId(bean.getTransactionId());
            });

            if (bean.isInPayedState()) {
                FenixFramework.atomic(() -> {
                    SibsPaymentsGatewayLog log = (SibsPaymentsGatewayLog) forwardPayment.advanceToPaidState(bean.getStatusCode(),
                            bean.getStatusMessage(), bean.getPayedAmount(), bean.getTransactionDate(), bean.getTransactionId(),
                            null, bean.getRequestBody(), bean.getResponseBody(), "");

                    log.setRequestSendDate(requestSendDate);
                    log.setRequestReceiveDate(requestReceiveDate);
                    log.setSibsGatewayTransactionId(bean.getTransactionId());
                });

                return String.format("redirect:%s", forwardPayment.getForwardPaymentSuccessUrl());
            } else {
                FenixFramework.atomic(() -> {
                    SibsPaymentsGatewayLog log = (SibsPaymentsGatewayLog) forwardPayment.reject("returnforwardpayment",
                            bean.getStatusCode(), bean.getStatusMessage(), bean.getRequestBody(), bean.getResponseBody());

                    log.setRequestSendDate(requestSendDate);
                    log.setRequestReceiveDate(requestReceiveDate);
                    log.setSibsGatewayTransactionId(bean.getTransactionId());
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

    public static IForwardPaymentController getForwardPaymentController(final ForwardPaymentRequest forwardPayment) {
        return new SibsOnlinePaymentsGatewayForwardPaymentController();
    }
}

package org.fenixedu.academictreasury.ui.customer.mbwaypaymentrequest;

import static org.fenixedu.treasury.util.TreasuryConstants.treasuryBundle;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.ui.customer.CustomerAccountingController;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.sibspaymentsgateway.MbwayRequest;
import org.fenixedu.treasury.dto.document.managepayments.PaymentReferenceCodeBean;
import org.fenixedu.treasury.services.integration.TreasuryPlataformDependentServicesFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;

@BennuSpringController(value = CustomerAccountingController.class)
@RequestMapping(CustomerAccountingMbwayPaymentRequestController.CONTROLLER_URL)
public class CustomerAccountingMbwayPaymentRequestController
        extends org.fenixedu.treasury.ui.accounting.managecustomer.MbwayPaymentRequestController {

    public static final String CONTROLLER_URL = "/academictreasury/customer/mbwaypaymentrequest";

    @Override
    protected void checkPermissions(DebtAccount debtAccount, Model model) {
        final String loggedUsername = TreasuryPlataformDependentServicesFactory.implementation().getLoggedUsername();

        final Person person = User.findByUsername(loggedUsername).getPerson();
        final String addressFiscalCountryCode = PersonCustomer.addressCountryCode(person);
        final String fiscalNumber = PersonCustomer.fiscalNumber(person);
        if (Strings.isNullOrEmpty(addressFiscalCountryCode) || Strings.isNullOrEmpty(fiscalNumber)) {
            throw new AcademicTreasuryDomainException("error.PersonCustomer.fiscalInformation.required");
        }

        if (PersonCustomer.findUnique(person, addressFiscalCountryCode, fiscalNumber).get() != debtAccount.getCustomer()) {
            addErrorMessage(treasuryBundle("error.authorization.not.allow.to.modify.settlements"), model);
            throw new SecurityException(treasuryBundle("error.authorization.not.allow.to.modify.settlements"));
        }
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @Override
    @RequestMapping(value = _CREATE_URI + "/{debtAccountId}", method = RequestMethod.GET)
    public String create(@PathVariable("debtAccountId") DebtAccount debtAccount, Model model) {
        return super.create(debtAccount, model);
    }

    private static final String _CREATEPOSTBACK_URI = "/createpostback";
    public static final String CREATEPOSTBACK_URL = CONTROLLER_URL + _CREATEPOSTBACK_URI;

    @Override
    @RequestMapping(value = _CREATEPOSTBACK_URI + "/{debtAccountId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> createpostback(@PathVariable("debtAccountId") DebtAccount debtAccount,
            @RequestParam("bean") PaymentReferenceCodeBean bean, Model model) {
        return super.createpostback(debtAccount, bean, model);
    }

    @Override
    @RequestMapping(value = _CREATE_URI + "/{debtAccountId}", method = RequestMethod.POST)
    public String createpost(@PathVariable("debtAccountId") DebtAccount debtAccount,
            @RequestParam("bean") PaymentReferenceCodeBean bean, Model model, RedirectAttributes redirectAttributes) {
        return super.createpost(debtAccount, bean, model, redirectAttributes);
    }

    private static final String _SHOW_MBWAY_PAYMENT_REQUEST_URI = "/showmbwaypaymentrequest";
    public static final String SHOW_MBWAY_PAYMENT_REQUEST_URL = CONTROLLER_URL + _SHOW_MBWAY_PAYMENT_REQUEST_URI;

    @RequestMapping(value = _SHOW_MBWAY_PAYMENT_REQUEST_URI + "/{debtAccountId}/{mbwayPaymentRequestId}")
    public String showmbwaypaymentrequest(@PathVariable("debtAccountId") DebtAccount debtAccount,
            @PathVariable("mbwayPaymentRequestId") MbwayRequest mbwayPaymentRequest, Model model) {
        return super.showmbwaypaymentrequest(debtAccount, mbwayPaymentRequest, model);
    }

    @Override
    protected String readDebtAccountUrl(DebtAccount debtAccount) {
        return String.format("%s/%s", CustomerAccountingController.READ_ACCOUNT_URL, debtAccount.getExternalId());
    }

    @Override
    protected String getCreateUrl() {
        return CREATE_URL;
    }

    @Override
    protected String getCreatePostbackUrl() {
        return CREATEPOSTBACK_URL;
    }

    @Override
    protected String getShowMbwayPaymentRequest() {
        return SHOW_MBWAY_PAYMENT_REQUEST_URL;
    }

}

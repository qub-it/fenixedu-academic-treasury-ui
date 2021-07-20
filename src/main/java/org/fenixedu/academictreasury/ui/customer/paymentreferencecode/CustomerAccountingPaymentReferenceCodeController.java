package org.fenixedu.academictreasury.ui.customer.paymentreferencecode;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.ui.customer.CustomerAccountingController;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.dto.document.managepayments.PaymentReferenceCodeBean;
import org.fenixedu.treasury.services.integration.TreasuryPlataformDependentServicesFactory;
import org.fenixedu.treasury.ui.accounting.managecustomer.PaymentReferenceCodeController;
import org.fenixedu.treasury.util.TreasuryConstants;
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
@RequestMapping(CustomerAccountingPaymentReferenceCodeController.CONTROLLER_URL)
public class CustomerAccountingPaymentReferenceCodeController extends PaymentReferenceCodeController {

    public static final String CONTROLLER_URL = "/academictreasury/customer/paymentreferencecode";

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
            addErrorMessage(TreasuryConstants.treasuryBundle("error.authorization.not.allow.to.modify.settlements"), model);
            throw new SecurityException(TreasuryConstants.treasuryBundle("error.authorization.not.allow.to.modify.settlements"));
        }
    }

    private static final String _CREATEPAYMENTCODEFORSEVERALDEBITENTRIES_URI = "/createpaymentcodeforseveraldebitentries";
    public static final String CREATEPAYMENTCODEFORSEVERALDEBITENTRIES_URL =
            CONTROLLER_URL + _CREATEPAYMENTCODEFORSEVERALDEBITENTRIES_URI;

    @Override
    @RequestMapping(value = _CREATEPAYMENTCODEFORSEVERALDEBITENTRIES_URI + "/{debtAccountId}", method = RequestMethod.GET)
    public String createPaymentCodeForSeveralDebitEntries(@PathVariable("debtAccountId") DebtAccount debtAccount, Model model) {
        return super.createPaymentCodeForSeveralDebitEntries(debtAccount, model);
    }

    @Override
    protected String _createPaymentCodeForSeveralDebitEntries(DebtAccount debtAccount, PaymentReferenceCodeBean bean,
            Model model) {
        bean.setUsePaymentAmountWithInterests(true);

        return super._createPaymentCodeForSeveralDebitEntries(debtAccount, bean, model);
    }

    private static final String _CREATEPAYMENTCODEFORSEVERALDEBITENTRIESPOSTBACK_URI =
            "/createpaymentcodeforseveraldebitentriespostback";
    public static final String CREATEPAYMENTCODEFORSEVERALDEBITENTRIESPOSTBACK_URL =
            CONTROLLER_URL + _CREATEPAYMENTCODEFORSEVERALDEBITENTRIESPOSTBACK_URI;

    @Override
    @RequestMapping(value = _CREATEPAYMENTCODEFORSEVERALDEBITENTRIESPOSTBACK_URI + "/{debtAccountId}",
            method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createPaymentCodeForSeveralDebitEntriesPostback(
            @PathVariable("debtAccountId") DebtAccount debtAccount, @RequestParam("bean") PaymentReferenceCodeBean bean,
            Model model) {
        return super.createPaymentCodeForSeveralDebitEntriesPostback(debtAccount, bean, model);
    }

    @Override
    @RequestMapping(value = _CREATEPAYMENTCODEFORSEVERALDEBITENTRIES_URI + "/{debtAccountId}", method = RequestMethod.POST)
    public String createPaymentCodeForSeveralDebitEntries(@PathVariable("debtAccountId") DebtAccount debtAccount,
            @RequestParam("bean") PaymentReferenceCodeBean bean, Model model, RedirectAttributes redirectAttributes) {
        return super.createPaymentCodeForSeveralDebitEntries(debtAccount, bean, model, redirectAttributes);
    }

    @Override
    protected String readDebtAccountUrl(final DebtAccount debtAccount) {
        return String.format("%s/%s", CustomerAccountingController.READ_ACCOUNT_URL, debtAccount.getExternalId());
    }

    @Override
    protected String getCreateUrl(DebtAccount debtAccount) {
        return CREATEPAYMENTCODEFORSEVERALDEBITENTRIES_URL + "/" + debtAccount.getExternalId();
    }

    @Override
    protected String getCreatePostbackUrl(DebtAccount debtAccount) {
        return CREATEPAYMENTCODEFORSEVERALDEBITENTRIESPOSTBACK_URL + "/" + debtAccount.getExternalId();
    }
    
}

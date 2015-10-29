package org.fenixedu.academictreasury.ui.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academictreasury.ui.AcademicTreasuryBaseController;
import org.fenixedu.academictreasury.ui.AcademicTreasuryController;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.treasury.domain.Customer;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.document.InvoiceEntry;
import org.fenixedu.treasury.domain.document.SettlementNote;
import org.fenixedu.treasury.domain.exemption.TreasuryExemption;
import org.fenixedu.treasury.domain.paymentcodes.FinantialDocumentPaymentCode;
import org.fenixedu.treasury.domain.paymentcodes.MultipleEntriesPaymentCode;
import org.fenixedu.treasury.domain.paymentcodes.PaymentCodeTarget;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

//@Component("org.fenixedu.treasury.ui.customer.viewAccounting") <-- Use for duplicate controller name disambiguation
@SpringFunctionality(app = AcademicTreasuryController.class, title = "label.title.customer.viewAccount",
        accessGroup = "activeStudents")
@RequestMapping(CustomerAccountingController.CONTROLLER_URL)
public class CustomerAccountingController extends AcademicTreasuryBaseController {
    public static final String CONTROLLER_URL = "/academictreasury/customer/viewaccount";
    private static final String READ_CUSTOMER_URI = "/customer/read/";
    public static final String READ_CUSTOMER_URL = CONTROLLER_URL + READ_CUSTOMER_URI;
    private static final String READ_ACCOUNT_URI = "/account/read/";
    public static final String READ_ACCOUNT_URL = CONTROLLER_URL + READ_ACCOUNT_URI;

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CustomerAccountingController.READ_CUSTOMER_URL;
    }

    @RequestMapping(value = READ_CUSTOMER_URI)
    public String readCustomer(Model model, RedirectAttributes redirectAttributes) {
        Customer customer = Authenticate.getUser().getPerson().getPersonCustomer();
        model.addAttribute("customer", customer);

        if (customer == null) {
            return redirect("/academicTreasury", model, redirectAttributes);
        }
        if (customer.getDebtAccountsSet().size() == 1) {
            DebtAccount debtAccount = customer.getDebtAccountsSet().iterator().next();
            return redirect(READ_ACCOUNT_URL + debtAccount.getExternalId(), model, redirectAttributes);
        }

        List<InvoiceEntry> pendingInvoiceEntries = new ArrayList<InvoiceEntry>();
        for (DebtAccount debtAccount : customer.getDebtAccountsSet()) {
            pendingInvoiceEntries.addAll(debtAccount.getPendingInvoiceEntriesSet());
        }
        model.addAttribute("pendingDocumentsDataSet", pendingInvoiceEntries);
        return "academicTreasury/customer/readCustomer";
    }

    @RequestMapping(value = READ_ACCOUNT_URI + "{oid}")
    public String readAccount(@PathVariable(value = "oid") DebtAccount debtAccount, Model model) {
        model.addAttribute("debtAccount", debtAccount);

        List<InvoiceEntry> allInvoiceEntries = new ArrayList<InvoiceEntry>();
        List<SettlementNote> paymentEntries = new ArrayList<SettlementNote>();
        List<TreasuryExemption> exemptionEntries = new ArrayList<TreasuryExemption>();
        List<InvoiceEntry> pendingInvoiceEntries = new ArrayList<InvoiceEntry>();
        allInvoiceEntries.addAll(debtAccount.getActiveInvoiceEntries().collect(Collectors.toList()));
        paymentEntries =
                SettlementNote.findByDebtAccount(debtAccount).filter(x -> x.isClosed() || x.isPreparing())
                        .filter(x -> !x.getPaymentEntriesSet().isEmpty() || !x.getReimbursementEntriesSet().isEmpty())
                        .collect(Collectors.toList());

        exemptionEntries.addAll(TreasuryExemption.findByDebtAccount(debtAccount).collect(Collectors.toList()));

        for (InvoiceEntry entry : debtAccount.getPendingInvoiceEntriesSet()) {
            if (entry.getFinantialDocument() == null) {
                pendingInvoiceEntries.add(entry);
            } else {
                if (pendingInvoiceEntries.stream().anyMatch(
                        x -> x.getFinantialDocument() != null && x.getFinantialDocument().equals(entry.getFinantialDocument()))) {
                    //if there is any entry for this document, don't add
                } else {
                    pendingInvoiceEntries.add(entry);
                }
            }
        }

        model.addAttribute("pendingDocumentsDataSet", pendingInvoiceEntries);
        model.addAttribute("allDocumentsDataSet", allInvoiceEntries);
        model.addAttribute("paymentsDataSet", paymentEntries);
        model.addAttribute("exemptionDataSet", exemptionEntries);
        
        final Set<PaymentCodeTarget> usedPaymentCodeTargets = Sets.newHashSet();
        for(final InvoiceEntry invoiceEntry : pendingInvoiceEntries) {
            if(!invoiceEntry.isDebitNoteEntry()) {
                continue;
            }
            
            usedPaymentCodeTargets.addAll(MultipleEntriesPaymentCode.findUsedByDebitEntry((DebitEntry) invoiceEntry).collect(Collectors.toSet()));
            usedPaymentCodeTargets.addAll(FinantialDocumentPaymentCode.findUsedByFinantialDocument(invoiceEntry.getFinantialDocument()).collect(Collectors.<PaymentCodeTarget>toSet()));
        }
        
        model.addAttribute("usedPaymentCodeTargets", usedPaymentCodeTargets);

        return "academicTreasury/customer/readDebtAccount";
    }
}

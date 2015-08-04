package org.fenixedu.academictreasury.services.reports.dataproviders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.InvoiceEntry;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.fenixedu.treasury.services.reports.dataproviders.AbstractDataProvider;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IFieldsExporter;
import com.qubit.terra.docs.util.IReportDataProvider;

public class RegistrationDataProvider extends AbstractDataProvider implements IReportDataProvider {

    protected static final String REGISTRATION_KEY = "registration";
    protected static final String TUITITONS_LINES_KEY = "tuititonsLines";

    private Registration registration;

    public RegistrationDataProvider(final Registration registration) {
        this.registration = registration;
        registerKey(TUITITONS_LINES_KEY, RegistrationDataProvider::handleTuititonsKey);
        registerKey(REGISTRATION_KEY, RegistrationDataProvider::handleRegistrationKey);
    }

    private static Object handleTuititonsKey(IReportDataProvider provider) {
        RegistrationDataProvider regisProvider = (RegistrationDataProvider) provider;
        Person p = regisProvider.registration.getStudent().getPerson();
        PersonCustomer customer = PersonCustomer.findUnique(p).orElse(null);
        FinantialInstitution finst =
                regisProvider.registration.getDegree().getAdministrativeOffice().getFinantialEntity().getFinantialInstitution();
        DebtAccount account = DebtAccount.findUnique(finst, customer).orElse(null);

        Set<PaymentReferenceCode> referencesCodes = new HashSet<PaymentReferenceCode>();

        List<? extends InvoiceEntry> pendingDebitEntriesSet =
                account.getPendingInvoiceEntriesSet().stream().filter(x -> x.isDebitNoteEntry()).collect(Collectors.toList());

        return null;
    }

    private static Object handleRegistrationKey(IReportDataProvider provider) {
        RegistrationDataProvider regisProvider = (RegistrationDataProvider) provider;
        return regisProvider.registration;
    }

    @Override
    public void registerFieldsAndImages(IDocumentFieldsData arg0) {
        // TODO Auto-generated method stub
        arg0.registerCollectionAsField(TUITITONS_LINES_KEY);
    }

    @Override
    public void registerFieldsMetadata(IFieldsExporter arg0) {
        // TODO Auto-generated method stub

    }

}

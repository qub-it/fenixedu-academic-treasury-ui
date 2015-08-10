package org.fenixedu.academictreasury.services.reports;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.debt.DebtAccount;

import com.qubit.terra.docs.core.DocumentGenerator;
import com.qubit.terra.docs.core.DocumentTemplateEngine;
import com.qubit.terra.docs.core.IDocumentTemplateService;

public class DocumentPrinter {
    static {
        registerService();
    }

    public static synchronized void registerService() {
        IDocumentTemplateService service = new DocumentPrinterConfiguration();
        DocumentTemplateEngine.registerServiceImplementations(service);
    }

//    private static void registerHelpers(DocumentGenerator generator) {
//        generator.registerHelper("dates", new DateHelper());
//        generator.registerHelper("lang", new LanguageHelper());
//        generator.registerHelper("numbers", new NumbersHelper());
//        generator.registerHelper("enumeration", new EnumerationHelper());
//        generator.registerHelper("strings", new StringsHelper());
//        generator.registerHelper("money", new MoneyHelper());
//    }

    //https://github.com/qub-it/fenixedu-qubdocs-reports/blob/master/src/main/java/org/fenixedu/academic/util/report/DocumentPrinter.java
    public static byte[] printRegistrationTuititionPaymentPlan(Registration registration, String outputMimeType) {

        Person p = registration.getStudent().getPerson();
        PersonCustomer customer = PersonCustomer.findUnique(p).orElse(null);
        FinantialInstitution finst =
                registration.getDegree().getAdministrativeOffice().getFinantialEntity().getFinantialInstitution();
        DebtAccount account = DebtAccount.findUnique(finst, customer).orElse(null);

        //Is this correct?!?!? Pleas check
        byte[] outputReport =
                org.fenixedu.treasury.services.reports.DocumentPrinter.printDebtAccountPaymentPlan(account, outputMimeType);

        return outputReport;
    }

    //https://github.com/qub-it/fenixedu-qubdocs-reports/blob/master/src/main/java/org/fenixedu/academic/util/report/DocumentPrinter.java
    public static byte[] printRegistrationTuititionPaymentPlanToPDF(Registration registration) {

        Person p = registration.getStudent().getPerson();
        PersonCustomer customer = PersonCustomer.findUnique(p).orElse(null);
        FinantialInstitution finst =
                registration.getDegree().getAdministrativeOffice().getFinantialEntity().getFinantialInstitution();
        DebtAccount account = DebtAccount.findUnique(finst, customer).orElse(null);

        //Is this correct?!?!? Pleas check
        byte[] outputReport =
                org.fenixedu.treasury.services.reports.DocumentPrinter
                        .printDebtAccountPaymentPlan(account, DocumentGenerator.PDF);

        return outputReport;
    }

}

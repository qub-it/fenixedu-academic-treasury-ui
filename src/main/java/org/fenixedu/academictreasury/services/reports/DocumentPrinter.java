package org.fenixedu.academictreasury.services.reports;

import java.io.InputStream;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.services.reports.dataproviders.CustomerDataProvider;
import org.fenixedu.treasury.services.reports.dataproviders.DebtAccountDataProvider;
import org.fenixedu.treasury.services.reports.dataproviders.FinantialInstitutionDataProvider;
import org.fenixedu.treasury.services.reports.helpers.DateHelper;
import org.fenixedu.treasury.services.reports.helpers.EnumerationHelper;
import org.fenixedu.treasury.services.reports.helpers.LanguageHelper;
import org.fenixedu.treasury.services.reports.helpers.MoneyHelper;
import org.fenixedu.treasury.services.reports.helpers.NumbersHelper;
import org.fenixedu.treasury.services.reports.helpers.StringsHelper;

import com.qubit.terra.docs.core.DocumentGenerator;
import com.qubit.terra.docs.core.DocumentTemplateEngine;
import com.qubit.terra.docs.core.IDocumentTemplateService;

public class DocumentPrinter {
    private static final String TEMPLATES_TUITIONS_PAYMENT_PLAN_ODT = "templates/tuitionsPaymentPlan.odt";
    static {
        registerService();
    }

    public static synchronized void registerService() {
        IDocumentTemplateService service = new DocumentPrinterConfiguration();
        DocumentTemplateEngine.registerServiceImplementations(service);
    }

    public static final String PDF = DocumentGenerator.PDF;
    public static final String ODT = DocumentGenerator.ODT;

    private static void registerHelpers(DocumentGenerator generator) {
        generator.registerHelper("dates", new DateHelper());
        generator.registerHelper("lang", new LanguageHelper());
        generator.registerHelper("numbers", new NumbersHelper());
        generator.registerHelper("enumeration", new EnumerationHelper());
        generator.registerHelper("strings", new StringsHelper());
        generator.registerHelper("money", new MoneyHelper());
    }

    //https://github.com/qub-it/fenixedu-qubdocs-reports/blob/master/src/main/java/org/fenixedu/academic/util/report/DocumentPrinter.java
    public static byte[] printRegistrationTuititionPaymentPlan(Registration registration, String outputMimeType) {

        Person p = registration.getStudent().getPerson();
        PersonCustomer customer = PersonCustomer.findUnique(p).orElse(null);
        FinantialInstitution finst =
                registration.getDegree().getAdministrativeOffice().getFinantialEntity().getFinantialInstitution();
        DebtAccount account = DebtAccount.findUnique(finst, customer).orElse(null);

        DocumentGenerator generator = null;

        InputStream resourceAsStream =
                DocumentGenerator.class.getClassLoader().getResourceAsStream(TEMPLATES_TUITIONS_PAYMENT_PLAN_ODT);

        if (resourceAsStream == null) {
            throw new RuntimeException("Template must be in class path in " + TEMPLATES_TUITIONS_PAYMENT_PLAN_ODT);
        }
        generator = DocumentGenerator.create(resourceAsStream, outputMimeType);
//          throw new TreasuryDomainException("error.ReportExecutor.document.template.not.available");
//      }

        registerHelpers(generator);
        generator.registerDataProvider(new DebtAccountDataProvider(account, null));
        generator.registerDataProvider(new CustomerDataProvider(customer));
        generator.registerDataProvider(new FinantialInstitutionDataProvider(finst));

        //... add more providers...

        byte[] outputReport = generator.generateReport();

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

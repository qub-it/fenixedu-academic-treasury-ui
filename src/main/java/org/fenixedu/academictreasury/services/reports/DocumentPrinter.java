package org.fenixedu.academictreasury.services.reports;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.debt.DebtAccount;
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
    static {
        registerService();
    }

    public static synchronized void registerService() {
        IDocumentTemplateService service = new DocumentPrinterConfiguration();
        DocumentTemplateEngine.registerServiceImplementations(service);
    }

    private static void registerHelpers(DocumentGenerator generator) {
        generator.registerHelper("dates", new DateHelper());
        generator.registerHelper("lang", new LanguageHelper());
        generator.registerHelper("numbers", new NumbersHelper());
        generator.registerHelper("enumeration", new EnumerationHelper());
        generator.registerHelper("strings", new StringsHelper());
        generator.registerHelper("money", new MoneyHelper());
    }

    //https://github.com/qub-it/fenixedu-qubdocs-reports/blob/master/src/main/java/org/fenixedu/academic/util/report/DocumentPrinter.java
    public static byte[] printRegistrationTuititionPaymentPlanToODT(Registration registration) {

//      TreasuryDocumentTemplate templateInEntity =
//              TreasuryDocumentTemplate
//                      .findByFinantialDocumentTypeAndFinantialEntity(document.getFinantialDocumentType(),
//                              document.getDebtAccount().getFinantialInstitution().getFinantialEntitiesSet().iterator().next())
//                      .filter(x -> x.isActive()).findFirst().orElse(null);
        DocumentGenerator generator = null;

//      if (templateInEntity != null) {
//          generator = DocumentGenerator.create(templateInEntity, DocumentGenerator.ODT);
//
//      } else {
        //HACK...
        generator =
                DocumentGenerator.create(
                        "F:\\O\\fenixedu\\fenixedu-treasury\\src\\main\\resources\\document_templates\\settlementNote.odt",
                        DocumentGenerator.ODT);
//          throw new TreasuryDomainException("error.ReportExecutor.document.template.not.available");
//      }
        Person p = registration.getStudent().getPerson();
        PersonCustomer customer = PersonCustomer.findUnique(p).orElse(null);
        FinantialInstitution finst =
                registration.getDegree().getAdministrativeOffice().getFinantialEntity().getFinantialInstitution();
        DebtAccount account = DebtAccount.findUnique(finst, customer).orElse(null);
        //... add more providers...

        byte[] outputReport = org.fenixedu.treasury.services.reports.DocumentPrinter.printDebtAccountPaymentPlanToODT(account);

        return outputReport;
    }

}

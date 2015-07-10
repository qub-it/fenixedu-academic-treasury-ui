package org.fenixedu.academictreasury.services;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituation;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.emoluments.ServiceRequestMapEntry;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.academictreasury.dto.academicservicerequest.AcademicServiceRequestDebitEntryBean;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.Vat;
import org.fenixedu.treasury.domain.VatType;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

import com.google.common.eventbus.Subscribe;

public class EmolumentServices {

    @Atomic
    public static Product createEmolument(final String code, final LocalizedString name, final VatType vatType,
            FinantialInstitution finantialInstitution) {
        if (AcademicTreasurySettings.getInstance().getEmolumentsProductGroup() == null) {
            throw new AcademicTreasuryDomainException("error.EmolumentServices.emoluments.product.group.not.defined");
        }

        return Product.create(AcademicTreasurySettings.getInstance().getEmolumentsProductGroup(), code, name,
                Product.defaultUnitOfMeasure(), true, vatType, Collections.singletonList(finantialInstitution));
    }

    public static Stream<Product> findEmoluments(final FinantialEntity finantialEntity) {
        if (AcademicTreasurySettings.getInstance().getEmolumentsProductGroup() == null) {
            throw new AcademicTreasuryDomainException("error.EmolumentServices.emoluments.product.group.not.defined");
        }

        return AcademicTreasurySettings.getInstance().getEmolumentsProductGroup().getProductsSet().stream()
                .filter(l -> l.getFinantialInstitutionsSet().contains(finantialEntity.getFinantialInstitution()));
    }

    @Subscribe
    public void newAcademicServiceRequestSituationEvent(final DomainObjectEvent<AcademicServiceRequest> event) {
        newAcademicServiceRequestSituationEvent(event.getInstance());
    }

    public boolean newAcademicServiceRequestSituationEvent(final AcademicServiceRequest academicServiceRequest) {

        if (!ServiceRequestType.findUnique(academicServiceRequest).isPayable()) {
            return false;
        }

        // Find configured map entry for service request type
        final ServiceRequestMapEntry serviceRequestMapEntry = ServiceRequestMapEntry.findMatch(academicServiceRequest);

        if (serviceRequestMapEntry == null) {
            return false;
        }

        if (!(academicServiceRequest.getAcademicProgram() instanceof Degree)) {
            return false;
        };

        // Check if the academicServiceRequest is ready to be charged
        if (!academicServiceRequest.getAcademicServiceRequestSituationsSet().stream()
                .map(AcademicServiceRequestSituation::getAcademicServiceRequestSituationType).collect(Collectors.toSet())
                .contains(serviceRequestMapEntry.getCreateEventOnSituation())) {

            // It is not ready
            return false;
        }

        return createAcademicServiceRequestEmolument(academicServiceRequest);
    }

    public static AcademicTreasuryEvent findAcademicTreasuryEvent(final AcademicServiceRequest academicServiceRequest) {
        return AcademicTreasuryEvent.findUnique(academicServiceRequest).orElse(null);
    }

    public static AcademicTariff findTariffForAcademicServiceRequest(final AcademicServiceRequest academicServiceRequest,
            final LocalDate when) {
        final Degree degree = (Degree) academicServiceRequest.getAcademicProgram();
        final Product product = ServiceRequestMapEntry.findProduct(academicServiceRequest);

        return academicServiceRequest.isRequestedWithCycle() ? AcademicTariff.findMatch(product, degree,
                academicServiceRequest.getRequestedCycle(), when.toDateTimeAtStartOfDay()) : AcademicTariff.findMatch(product,
                degree, when.toDateTimeAtStartOfDay());
    }

    @Atomic
    public static AcademicServiceRequestDebitEntryBean calculateForAcademicServiceRequest(
            final AcademicServiceRequest academicServiceRequest, final LocalDate debtDate) {
        if (!ServiceRequestType.findUnique(academicServiceRequest).isPayable()) {
            return null;
        }

        // Find configured map entry for service request type
        final ServiceRequestMapEntry serviceRequestMapEntry = ServiceRequestMapEntry.findMatch(academicServiceRequest);

        if (serviceRequestMapEntry == null) {
            return null;
        }

        if (!(academicServiceRequest.getAcademicProgram() instanceof Degree)) {
            return null;
        };

        // Read person customer

        if (!PersonCustomer.findUnique(academicServiceRequest.getPerson()).isPresent()) {
            PersonCustomer.create(academicServiceRequest.getPerson());
        }

        final PersonCustomer personCustomer = PersonCustomer.findUnique(academicServiceRequest.getPerson()).get();

        // Find tariff

        final AcademicTariff academicTariff = findTariffForAcademicServiceRequest(academicServiceRequest, debtDate);

        if (academicTariff == null) {
            return null;
        }

        final FinantialEntity finantialEntity = academicTariff.getFinantialEntity();
        final FinantialInstitution finantialInstitution = finantialEntity.getFinantialInstitution();

        if (!DebtAccount.findUnique(finantialInstitution, personCustomer).isPresent()) {
            DebtAccount.create(finantialInstitution, personCustomer);
        }

        final DebtAccount personDebtAccount = DebtAccount.findUnique(finantialInstitution, personCustomer).orElse(null);

        // Find or create event if does not exists
        if (findAcademicTreasuryEvent(academicServiceRequest) == null) {
            AcademicTreasuryEvent.createForAcademicServiceRequest(personDebtAccount, academicServiceRequest);
        }

        final AcademicTreasuryEvent academicTreasuryEvent = findAcademicTreasuryEvent(academicServiceRequest);

        final LocalizedString debitEntryName = academicTariff.academicServiceRequestDebitEntryName(academicTreasuryEvent);
        final LocalDate dueDate = academicTariff.dueDate(debtDate);
        final Vat vat = academicTariff.vat(debtDate);
        final BigDecimal amount = academicTariff.amountToPay(academicTreasuryEvent);

        return new AcademicServiceRequestDebitEntryBean(debitEntryName, dueDate, vat.getTaxRate(), amount);
    }

    @Atomic
    public static boolean createAcademicServiceRequestEmolument(final AcademicServiceRequest academicServiceRequest) {
        final LocalDate when = possibleDebtDateOnAcademicService(academicServiceRequest);

        return createAcademicServiceRequestEmolument(academicServiceRequest, when);
    }

    @Atomic
    public static boolean createAcademicServiceRequestEmolument(final AcademicServiceRequest academicServiceRequest,
            final LocalDate when) {

        if (!ServiceRequestType.findUnique(academicServiceRequest).isPayable()) {
            return false;
        }

        // Find configured map entry for service request type
        final ServiceRequestMapEntry serviceRequestMapEntry = ServiceRequestMapEntry.findMatch(academicServiceRequest);

        if (serviceRequestMapEntry == null) {
            return false;
        }

        if (!(academicServiceRequest.getAcademicProgram() instanceof Degree)) {
            return false;
        };

        // Read person customer

        if (!PersonCustomer.findUnique(academicServiceRequest.getPerson()).isPresent()) {
            PersonCustomer.create(academicServiceRequest.getPerson());
        }

        final PersonCustomer personCustomer = PersonCustomer.findUnique(academicServiceRequest.getPerson()).get();

        // Find tariff

        final AcademicTariff academicTariff = findTariffForAcademicServiceRequest(academicServiceRequest, when);

        if (academicTariff == null) {
            return false;
        }

        final FinantialEntity finantialEntity = academicTariff.getFinantialEntity();
        final FinantialInstitution finantialInstitution = finantialEntity.getFinantialInstitution();

        if (!DebtAccount.findUnique(finantialInstitution, personCustomer).isPresent()) {
            DebtAccount.create(finantialInstitution, personCustomer);
        }

        final DebtAccount personDebtAccount = DebtAccount.findUnique(finantialInstitution, personCustomer).orElse(null);

        // Find or create event if does not exists
        if (findAcademicTreasuryEvent(academicServiceRequest) == null) {
            AcademicTreasuryEvent.createForAcademicServiceRequest(personDebtAccount, academicServiceRequest);
        }

        final AcademicTreasuryEvent academicTresuryEvent = findAcademicTreasuryEvent(academicServiceRequest);

        if (academicTresuryEvent.isChargedWithDebitEntry()) {
            return false;
        }

        return academicTariff.createDebitEntryForAcademicServiceRequest(academicTresuryEvent) != null;
    }

    public static LocalDate possibleDebtDateOnAcademicService(final AcademicServiceRequest academicServiceRequest) {
        // Find the configured state to create debt on academic service request

        if (!ServiceRequestType.findUnique(academicServiceRequest).isPayable()) {
            return null;
        }

        // Find configured map entry for service request type
        final ServiceRequestMapEntry serviceRequestMapEntry = ServiceRequestMapEntry.findMatch(academicServiceRequest);

        if (serviceRequestMapEntry == null) {
            return null;
        }

        AcademicServiceRequestSituationType createEventOnSituation = serviceRequestMapEntry.getCreateEventOnSituation();

        if (academicServiceRequest.getSituationByType(createEventOnSituation) == null) {
            return academicServiceRequest.getRequestDate().toLocalDate();
        }

        return academicServiceRequest.getSituationByType(createEventOnSituation).getSituationDate().toLocalDate();
    }
}

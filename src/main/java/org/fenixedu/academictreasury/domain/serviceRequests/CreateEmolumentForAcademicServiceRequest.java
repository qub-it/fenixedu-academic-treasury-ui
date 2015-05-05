package org.fenixedu.academictreasury.domain.serviceRequests;

import java.util.Optional;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.academictreasury.domain.emoluments.ServiceRequestMapEntry;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.bennu.signals.DomainObjectEvent;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.debt.DebtAccount;

public class CreateEmolumentForAcademicServiceRequest {

    public void newAcademicServiceRequestSituationEvent(final DomainObjectEvent<AcademicServiceRequest> event) {
        final AcademicServiceRequest academicServiceRequest = event.getInstance();

        if (!ServiceRequestType.findUnique(academicServiceRequest).isPayed()) {
            return;
        }

        // Read person customer
        PersonCustomer personCustomer = PersonCustomer.findUnique(academicServiceRequest.getPerson()).orElse(null);

        if(personCustomer == null) {
            personCustomer = PersonCustomer.create(academicServiceRequest.getPerson());
        }

        // Find configured map entry for service request type
        final Optional<ServiceRequestMapEntry> matchMapEntryOptional = ServiceRequestMapEntry.findMatch(academicServiceRequest);

        if (!matchMapEntryOptional.isPresent()) {
            throw new AcademicTreasuryDomainException(
                    "error.CreateEmolumentForAcademicServiceRequest.cannot.find.serviceRequestMapEntry");
        }

        final ServiceRequestMapEntry serviceRequestMapEntry = matchMapEntryOptional.get();

        final AdministrativeOffice administrativeOffice = academicServiceRequest.getAdministrativeOffice();

        if (!(academicServiceRequest.getAcademicProgram() instanceof Degree)) {
            throw new AcademicTreasuryDomainException("error.CreateEmolumentForAcademicServiceRequest.only.degrees.are.supported");
        };

        // Find tariff

        final Product product = serviceRequestMapEntry.getProduct();
        final Degree degree = (Degree) academicServiceRequest.getAcademicProgram();

        final Optional<? extends AcademicTariff> academicTariffOptional =
                academicServiceRequest.isRequestedWithCycle() ? AcademicTariff.findMatch(product, degree,
                        academicServiceRequest.getRequestedCycle(), academicServiceRequest.getActiveSituationDate()) : AcademicTariff
                        .findMatch(product, degree, academicServiceRequest.getActiveSituationDate());

        if (!academicTariffOptional.isPresent()) {
            throw new AcademicTreasuryDomainException("error.CreateEmolumentForAcademicServiceRequest.cannot.find.tariff");
        }

        final AcademicTariff academicTariff = academicTariffOptional.get();

        final FinantialEntity finantialEntity = academicTariff.getFinantialEntity();
        final FinantialInstitution finantialInstitution = finantialEntity.getFinantialInstitution();
        
        DebtAccount personDebtAccount = DebtAccount.findUnique(finantialInstitution, personCustomer).orElse(null);
        
        if(personDebtAccount == null) {
            personDebtAccount = DebtAccount.create(finantialInstitution, personCustomer);
        }
        
        // Find or create event if does not exists
        AcademicTreasuryEvent academicTresuryEvent = AcademicTreasuryEvent.findUnique(academicServiceRequest).orElse(null);
        
        if(academicTresuryEvent == null) {
            academicTresuryEvent = AcademicTreasuryEvent.createForAcademicServiceRequest(academicServiceRequest);
        }
        
        // Find if debt is created
        
        
        // If not create debt

        // 
    }
}

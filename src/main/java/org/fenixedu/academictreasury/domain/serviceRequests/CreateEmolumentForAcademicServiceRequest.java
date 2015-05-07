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

import com.google.common.eventbus.Subscribe;

public class CreateEmolumentForAcademicServiceRequest {

    @Subscribe
    public void newAcademicServiceRequestSituationEvent(final DomainObjectEvent<AcademicServiceRequest> event) {
        newAcademicServiceRequestSituationEvent(event.getInstance());
    }

    public void newAcademicServiceRequestSituationEvent(final AcademicServiceRequest academicServiceRequest) {

        if (!ServiceRequestType.findUnique(academicServiceRequest).isPayed()) {
            return;
        }

        // Read person customer
        PersonCustomer personCustomer = PersonCustomer.findUnique(academicServiceRequest.getPerson()).orElse(null);

        if(personCustomer == null) {
            personCustomer = PersonCustomer.create(academicServiceRequest.getPerson());
        }

        // Find configured map entry for service request type
        final ServiceRequestMapEntry serviceRequestMapEntry = ServiceRequestMapEntry.findMatch(academicServiceRequest);

        if (serviceRequestMapEntry == null) {
            throw new AcademicTreasuryDomainException(
                    "error.CreateEmolumentForAcademicServiceRequest.cannot.find.serviceRequestMapEntry");
        }

        if (!(academicServiceRequest.getAcademicProgram() instanceof Degree)) {
            throw new AcademicTreasuryDomainException("error.CreateEmolumentForAcademicServiceRequest.only.degrees.are.supported");
        };

        // Find tariff

        final Degree degree = (Degree) academicServiceRequest.getAcademicProgram();
        final Product product = ServiceRequestMapEntry.findProduct(academicServiceRequest);

        final AcademicTariff academicTariff =
                academicServiceRequest.isRequestedWithCycle() ? AcademicTariff.findMatch(product, degree,
                        academicServiceRequest.getRequestedCycle(), academicServiceRequest.getActiveSituationDate()) : AcademicTariff
                        .findMatch(product, degree, academicServiceRequest.getActiveSituationDate());

        if (academicTariff == null) {
            throw new AcademicTreasuryDomainException("error.CreateEmolumentForAcademicServiceRequest.cannot.find.tariff");
        }

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
        if(!academicTresuryEvent.isChargedWithDebitEntry()) {
            academicTariff.createDebitEntry(personDebtAccount, academicTresuryEvent);
        }
    }
}

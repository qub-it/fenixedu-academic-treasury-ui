package org.fenixedu.academictreasury.domain.emoluments;

import java.util.stream.Stream;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.treasury.domain.Product;

import pt.ist.fenixframework.Atomic;

public class ServiceRequestMapEntry extends ServiceRequestMapEntry_Base {

    public ServiceRequestMapEntry() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestMapEntry(final Product product, final ServiceRequestType requestType,
            final AcademicServiceRequestSituationType createEventOnSituation) {
        this();

        setProduct(product);
        setServiceRequestType(requestType);
        setCreateEventOnSituation(createEventOnSituation);

        checkRules();
    }

    private void checkRules() {

        if (getProduct() == null) {
            throw new AcademicTreasuryDomainException("error.ServiceRequestMapEntry.product.required");
        }

        if (getServiceRequestType() == null) {
            throw new AcademicTreasuryDomainException("error.ServiceRequestMapEntry.serviceRequestType.required");
        }

        if (getCreateEventOnSituation() == null) {
            throw new AcademicTreasuryDomainException("error.ServiceRequestMapEntry.createEventOnSituation.required");
        }

    }

    private boolean isDeletable() {
        return true;
    }

    @Atomic
    public void delete() {
        if (!isDeletable()) {
            throw new AcademicTreasuryDomainException("error.ServiceRequestMapEntry.delete.impossible");
        }

        setServiceRequestType(null);
        setProduct(null);
        setBennu(null);
        super.deleteDomainObject();
    }

    /*---------
     * SERVICES
     * --------
     */

    public static Stream<ServiceRequestMapEntry> findAll() {
        return Bennu.getInstance().getServiceRequestMapEntriesSet().stream();
    }

    public static Stream<ServiceRequestMapEntry> find(final Product product) {
        return findAll().filter(e -> e.getProduct() == product);
    }

    public static Stream<ServiceRequestMapEntry> find(final ServiceRequestType requestType) {
        return findAll().filter(e -> e.getServiceRequestType() == requestType);
    }

    //TODOJN o nome do metodo e v ou find
    public static Stream<ServiceRequestMapEntry> v(final Product product, final ServiceRequestType requestType) {
        return find(product).filter(e -> e.getServiceRequestType() == requestType);
    }

    public static ServiceRequestMapEntry findMatch(final AcademicServiceRequest academicServiceRequest) {
        //TODOJN mudar para usar o contracto?
        final ServiceRequestType serviceRequestType = ServiceRequestType.findUnique(academicServiceRequest);

        return null;
    }

    public static Product findProduct(final AcademicServiceRequest academicServiceRequest) {
        if (findMatch(academicServiceRequest) == null) {
            throw new AcademicTreasuryDomainException("error.ServiceRequestMapEntry.cannot.find.serviceRequestMapEntry");
        }

        return findMatch(academicServiceRequest).getProduct();
    }

    @Atomic
    public static ServiceRequestMapEntry create(final Product product, final ServiceRequestType requestType,
            AcademicServiceRequestSituationType situationType) {
        return new ServiceRequestMapEntry(product, requestType, situationType);
    }

}

package org.fenixedu.academictreasury.domain.emoluments;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.spec.PSource;

import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestTypeOption;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestTypeOptionBooleanValue;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.treasury.domain.Product;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.emory.mathcs.backport.java.util.Collections;

public class ServiceRequestMapEntry extends ServiceRequestMapEntry_Base {

    public static Comparator<ServiceRequestMapEntry> COMPARE_BY_POSITIVE_OPTIONS = new Comparator<ServiceRequestMapEntry>() {

        @Override
        public int compare(final ServiceRequestMapEntry o1, final ServiceRequestMapEntry o2) {
            int c = Long.compare(o1.positiveOptionsCount(), o2.positiveOptionsCount());

            return c != 0 ? c : DomainObjectUtil.COMPARATOR_BY_ID.compare(o1, o2);
        }
    };

    public ServiceRequestMapEntry() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestMapEntry(final Product product, final ServiceRequestType requestType,
            final AcademicServiceRequestSituationType createEventOnSituation, final Set<ServiceRequestTypeOption> optionValues) {
        this();

        setProduct(product);
        setServiceRequestType(requestType);
        setCreateEventOnSituation(createEventOnSituation);

        if (optionValues != null) {
            for (final ServiceRequestTypeOption serviceRequestTypeOption : optionValues) {
                addServiceRequestTypeOptionBooleanValues(ServiceRequestTypeOptionBooleanValue.create(serviceRequestTypeOption,
                        true));
            }
        }

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

        // Find duplicates
        for (final ServiceRequestMapEntry outer : find(getServiceRequestType()).collect(Collectors.toSet())) {
            for (final ServiceRequestMapEntry inner : find(getServiceRequestType()).collect(Collectors.toSet())) {
                if (outer == inner) {
                    // Same instance
                    continue;
                }

                if (outer.positiveServiceRequestTypeOptions(getServiceRequestTypeOptionBooleanValuesSet()).count() != inner
                        .positiveServiceRequestTypeOptions(getServiceRequestTypeOptionBooleanValuesSet()).count()) {
                    continue;
                }

                if (Sets.difference(
                        outer.positiveServiceRequestTypeOptions(getServiceRequestTypeOptionBooleanValuesSet()).collect(
                                Collectors.toSet()),
                        outer.positiveServiceRequestTypeOptions(getServiceRequestTypeOptionBooleanValuesSet()).collect(
                                Collectors.toSet())).isEmpty()) {
                    throw new AcademicTreasuryDomainException("error.ServiceRequestMapEntry.duplicate.entry");
                }
            }
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

        while (!getServiceRequestTypeOptionBooleanValuesSet().isEmpty()) {
            final ServiceRequestTypeOptionBooleanValue ref = getServiceRequestTypeOptionBooleanValuesSet().iterator().next();
            ref.setServiceRequestMapEntry(null);
            ref.delete();
        }

        setServiceRequestType(null);
        setProduct(null);
        setBennu(null);
        super.deleteDomainObject();
    }

    private long positiveOptionsCount() {
        return getServiceRequestTypeOptionBooleanValuesSet().stream().filter(v -> v.getValue()).count();
    }

    private Optional<ServiceRequestTypeOptionBooleanValue> findOptionValue(final ServiceRequestTypeOption option) {
        return getServiceRequestTypeOptionBooleanValuesSet().stream().filter(v -> v.getServiceRequestTypeOption() == option)
                .findFirst();
    }

    private boolean satisfyPositiveOptions(final Set<ServiceRequestTypeOption> requestPositiveOptions) {
        final Set<ServiceRequestTypeOption> positiveOptions =
                positiveServiceRequestTypeOptions(getServiceRequestTypeOptionBooleanValuesSet()).collect(Collectors.toSet());

        for (final ServiceRequestTypeOption o : positiveOptions) {
            if (!requestPositiveOptions.contains(o)) {
                return false;
            }
        }

        return true;
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

    public static Stream<ServiceRequestMapEntry> v(final Product product, final ServiceRequestType requestType) {
        return find(product).filter(e -> e.getServiceRequestType() == requestType);
    }

    public static ServiceRequestMapEntry findMatch(final AcademicServiceRequest academicServiceRequest) {
        final ServiceRequestType serviceRequestType = ServiceRequestType.findUnique(academicServiceRequest);

        final Set<ServiceRequestTypeOption> requestPositiveOptions =
                positiveServiceRequestTypeOptions(academicServiceRequest.getServiceRequestTypeOptionBooleanValuesSet()).collect(
                        Collectors.toSet());

        final Set<ServiceRequestMapEntry> candidates = Sets.newTreeSet(Collections.reverseOrder(COMPARE_BY_POSITIVE_OPTIONS));
        for (final ServiceRequestMapEntry mapEntry : find(serviceRequestType).collect(Collectors.toSet())) {
            if (mapEntry.satisfyPositiveOptions(requestPositiveOptions)) {
                candidates.add(mapEntry);
            }
        }

        return candidates != null ? candidates.iterator().next() : null;
    }

    public static Product findProduct(final AcademicServiceRequest academicServiceRequest) {
        if (findMatch(academicServiceRequest) == null) {
            throw new AcademicTreasuryDomainException("error.ServiceRequestMapEntry.cannot.find.serviceRequestMapEntry");
        }

        return findMatch(academicServiceRequest).getProduct();
    }

    @Atomic
    public static ServiceRequestMapEntry create(final Product product, final ServiceRequestType requestType,
            AcademicServiceRequestSituationType situationType, final Set<ServiceRequestTypeOption> optionValues) {
        return new ServiceRequestMapEntry(product, requestType, situationType, optionValues);
    }

    /*--------------
     * OTHER METHODS
     * -------------
     */

    private static Stream<ServiceRequestTypeOption> positiveServiceRequestTypeOptions(
            final Set<ServiceRequestTypeOptionBooleanValue> values) {
        return values.stream().filter(v -> v.getValue()).map(v -> v.getServiceRequestTypeOption());
    }

}

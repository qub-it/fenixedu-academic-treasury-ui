package org.fenixedu.academictreasury.domain.customer;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.treasury.domain.Customer;
import org.fenixedu.treasury.domain.CustomerType;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.util.Constants;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import sun.security.jca.GetInstance;

import com.google.common.base.Strings;

public class PersonCustomer extends PersonCustomer_Base {

    private static final String STUDENT_CODE = "STUDENT";
    private static final String CANDIDACY_CODE = "CANDIDATE";

    protected PersonCustomer() {
        super();
    }

    protected PersonCustomer(final Person person) {
        this();

        setPerson(person);
        setCustomerType(getDefaultCustomerType(this));
        checkRules();
    }

    @Override
    protected void checkRules() {
        super.checkRules();

        /* Person Customer can be associated to Person with only one of two relations
         */

        if (getPerson() == null && getPersonForInactivePersonCustomer() == null) {
            throw new AcademicTreasuryDomainException("error.PersonCustomer.person.required");
        }

        if (getPerson() != null && getPersonForInactivePersonCustomer() != null) {
            throw new AcademicTreasuryDomainException("error.PersonCustomer.may.only.be.related.to.person.with.one.relation");
        }

        if (isActive() && (find(getPerson()).count() > 1)) {
            throw new AcademicTreasuryDomainException("error.PersonCustomer.person.customer.duplicated");
        }
    }

    @Override
    public String getCode() {
        return this.getExternalId();
    }

    @Override
    public String getFiscalNumber() {
        if (Strings.isNullOrEmpty(getPerson().getSocialSecurityNumber())) {
            return Customer.DEFAULT_FISCAL_NUMBER;
        }
        return getPerson().getSocialSecurityNumber();
    }

    @Override
    public String getName() {
        if (!isActive()) {
            return getPersonForInactivePersonCustomer().getName();
        }

        return getPerson().getName();
    }

    @Override
    public String getIdentificationNumber() {
        if (!isActive()) {
            return getPersonForInactivePersonCustomer().getDocumentIdNumber();
        }

        return getPerson().getDocumentIdNumber();
    }

    @Override
    public String getAddress() {
        if (!isActive()) {
            return getPersonForInactivePersonCustomer().getAddress();
        }

        return getPerson().getAddress();
    }

    @Override
    public String getDistrictSubdivision() {
        final Person person = isActive() ? getPerson() : getPersonForInactivePersonCustomer();

        if (person.getDefaultPhysicalAddress() == null) {
            return null;
        }

        return person.getDefaultPhysicalAddress().getArea();
    }

    @Override
    public String getDistrict() {
        final Person person = isActive() ? getPerson() : getPersonForInactivePersonCustomer();

        if (person.getDefaultPhysicalAddress() == null) {
            return null;
        }

        return person.getDefaultPhysicalAddress().getDistrictOfResidence();
    }

    @Override
    public String getZipCode() {
        final Person person = isActive() ? getPerson() : getPersonForInactivePersonCustomer();

        if (person.getDefaultPhysicalAddress() == null) {
            return null;
        }

        return person.getDefaultPhysicalAddress().getAreaCode();
    }

    @Override
    public String getCountryCode() {
        final Person person = isActive() ? getPerson() : getPersonForInactivePersonCustomer();

        if (person.getDefaultPhysicalAddress() == null || person.getDefaultPhysicalAddress().getCountryOfResidence() == null) {
            return null;
        }

        return person.getDefaultPhysicalAddress().getCountryOfResidence().getCode();
    }

    @Override
    public String getNationalityCountryCode() {
        final Person person = isActive() ? getPerson() : getPersonForInactivePersonCustomer();

        if (person.getCountry() != null) {
            return person.getCountry().getCode();
        }

        return null;
    }

    // TODO: Ask IST-DSI
    @Override
    public String getFiscalCountry() {
        return getNationalityCountryCode();
    }

    @Override
    public String getPaymentReferenceBaseCode() {
        return this.getCode();
    }

    @Override
    public boolean isPersonCustomer() {
        return true;
    }

    @Override
    public boolean isActive() {
        return getPerson() != null;
    }

    public boolean isBlockingAcademicalActs(final LocalDate when) {

        if (DebtAccount.find(this).map(da -> Constants.isGreaterThan(da.getTotalInDebt(), BigDecimal.ZERO))
                .reduce((a, c) -> a || c).orElse(Boolean.FALSE)) {
            return DebitEntry.find(this).map(d -> isDebitEntryBlockingAcademicalActs(d, when)).reduce((a, c) -> a || c)
                    .orElse(Boolean.FALSE);
        }

        return false;
    }

    public static boolean isDebitEntryBlockingAcademicalActs(final DebitEntry debitEntry, final LocalDate when) {
        if (debitEntry.isAnnulled()) {
            return false;
        }

        if (!debitEntry.isInDebt()) {
            return false;
        }

        if (!debitEntry.isDueDateExpired(when)) {
            return false;
        }

        if (!AcademicTreasurySettings.getInstance().isAcademicalActBlocking(debitEntry.getProduct())) {
            return false;
        }

        if (debitEntry.isAcademicalActBlockingSuspension()) {
            return false;
        }

        return true;
    }

    public void mergeWithPerson(final Person person) {

        if (getPerson() == person) {
            throw new AcademicTreasuryDomainException("error.PersonCustomer.merging.not.happening");
        }

        if (getPersonForInactivePersonCustomer() == person) {
            throw new AcademicTreasuryDomainException("error.PersonCustomer.merged.already.with.person");
        }

        if(person.getPersonCustomer() != null) {
            person.getPersonCustomer().setPersonForInactivePersonCustomer(getPerson());
        }
        
        for (final PersonCustomer personCustomer : person.getInactivePersonCustomersSet()) {
            personCustomer.setPersonForInactivePersonCustomer(getPerson());
        }

        checkRules();
    }

    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    public static Stream<? extends PersonCustomer> findAll() {
        return Customer.findAll().filter(c -> c instanceof PersonCustomer).map(PersonCustomer.class::cast);
    }

    protected static Stream<? extends PersonCustomer> find(final Person person) {
        return findAll().filter(pc -> pc.getPerson() == person);
    }

    public static Optional<? extends PersonCustomer> findUnique(final Person person) {
        return PersonCustomer.findAll().filter(pc -> pc.getPerson() == person).findFirst();
    }

    @Atomic
    public static PersonCustomer create(Person person) {
        return new PersonCustomer(person);
    }

    public static Optional<? extends PersonCustomer> findByFiscalNumber(String fiscalNumber) {
        return PersonCustomer.findAll().filter(pc -> pc.getFiscalNumber().equals(fiscalNumber)).findFirst();
    }

    public static CustomerType getDefaultCustomerType(PersonCustomer person) {
        if (person.getPerson().getStudent() != null) {
            return CustomerType.findByCode(STUDENT_CODE).findFirst().orElse(null);
        } else {
            return CustomerType.findByCode(CANDIDACY_CODE).findFirst().orElse(null);
        }
    }

    @Override
    public String getBusinessIdentification() {
        if (this.getPerson().getStudent() != null) {
            return this.getPerson().getStudent().getNumber().toString();
        }
        return this.getIdentificationNumber();
    }

}

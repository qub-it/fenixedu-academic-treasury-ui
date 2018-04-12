package org.fenixedu.academictreasury.domain.academicalAct;

import java.util.Comparator;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import pt.ist.fenixframework.FenixFramework;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;

import com.google.common.base.Strings;

public class AcademicActBlockingSuspension extends AcademicActBlockingSuspension_Base {
    
    public static Comparator<AcademicActBlockingSuspension> COMPARE_BY_BEGIN_DATE = new Comparator<AcademicActBlockingSuspension>() {
        
        @Override
        public int compare(AcademicActBlockingSuspension o1, AcademicActBlockingSuspension o2) {
            return o1.getBeginDate().compareTo(o2.getBeginDate());
        }
    };
    
    protected AcademicActBlockingSuspension() {
        super();
        
        setDomainRoot(FenixFramework.getDomainRoot());
    }
    
    protected AcademicActBlockingSuspension(final Person person, final LocalDate beginDate, final LocalDate endDate, final String reason) {
        this();
        
        setPerson(person);
        setBeginDate(beginDate);
        setEndDate(endDate);
        setReason(reason);
        
        checkRules();
    }
    
    private void checkRules() {
        if(getDomainRoot() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicActBlockingSuspension.bennu.required");
        }
        
        if(getPerson() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicActBlockingSuspension.person.required");
        }
        
        if(getBeginDate() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicActBlockingSuspension.beginDate.required");
        }
        
        if(getEndDate() == null) {
            throw new AcademicTreasuryDomainException("error.AcademicActBlockingSuspension.endDate.required");
        }
        
        if(getEndDate().isBefore(getBeginDate())) {
            throw new AcademicTreasuryDomainException("error.AcademicActBlockingSuspension.endDate.must.be.after.or.equal.beginDate");
        }
        
        if(Strings.isNullOrEmpty(getReason())) {
            throw new AcademicTreasuryDomainException("error.AcademicActBlockingSuspension.reason.required");
        }
        
    }
    
    public Interval getDateInterval() {
        return new Interval(getBeginDate().toDateTimeAtStartOfDay(), getEndDate().plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1));
    }
    
    public boolean isBlockingSuspended(final LocalDate when) {
        return getDateInterval().contains(when.toDateTimeAtStartOfDay());
    }

    @Atomic
    public void edit(final LocalDate beginDate, final LocalDate endDate, final String reason) {
        setBeginDate(beginDate);
        setEndDate(endDate);
        setReason(reason);
        
        checkRules();
    }

    private boolean isDeletable() {
        return true;
    }

    @Atomic
    public void delete() {
        if(!isDeletable()) {
            throw new AcademicTreasuryDomainException("error.AcademicActBlockingSuspension.delete.impossible");
        }
        
        setDomainRoot(null);
        setPerson(null);
        
        super.deleteDomainObject();
    }
    
    // @formatter:off
    /* --------
     * SERVICES
     * --------
     */
    // @formatter:on
    
    
    public static Stream<AcademicActBlockingSuspension> findAll() {
        return FenixFramework.getDomainRoot().getAcademicActBlockingSuspensionsSet().stream();
    }
    
    public static Stream<AcademicActBlockingSuspension> find(final Person person) {
         return person.getAcademicActBlockingSuspensionsSet().stream();
    }
    
    public static Stream<AcademicActBlockingSuspension> find(final Person person, final LocalDate when) {
        return find(person).filter(l -> l.isBlockingSuspended(when));
    }
    
    public static boolean isBlockingSuspended(final Person person, final LocalDate when) {
        return find(person, when).count() > 0;
    }
    
    @Atomic
    public static AcademicActBlockingSuspension create(final Person person, final LocalDate beginDate, final LocalDate endDate, final String reason) {
        return new AcademicActBlockingSuspension(person, beginDate, endDate, reason);
    }

}

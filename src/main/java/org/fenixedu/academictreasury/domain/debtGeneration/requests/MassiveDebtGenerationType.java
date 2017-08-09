package org.fenixedu.academictreasury.domain.debtGeneration.requests;

import java.util.stream.Stream;

import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.bennu.core.domain.Bennu;

import com.google.common.base.Strings;

public class MassiveDebtGenerationType extends MassiveDebtGenerationType_Base {
    
    public MassiveDebtGenerationType() {
        super();
        setBennu(Bennu.getInstance());
    }
    
    public MassiveDebtGenerationType(final String name, final String implementationClass) {
        this();
        
        setName(name);
        setImplementationClass(implementationClass);
        setActive(false);
        
        checkRules();
    }

    private void checkRules() {
        if(getBennu() == null) {
            throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationType.bennu.required");
        }
        
        if(Strings.isNullOrEmpty(getName())) {
            throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationType.name.required");
        }
        
        if(Strings.isNullOrEmpty(getImplementationClass())) {
            throw new AcademicTreasuryDomainException("error.MassiveDebtGenerationType.implementationClass.required");
        }
        
        // To check the class name given
        implementation();
    }
    
    public boolean isActive() {
        return getActive();
    }
    
    public boolean isExecutionRequired() {
        return implementation().isExecutionYearRequired();
    }
    
    public boolean isForAcademicTaxRequired() {
        return implementation().isForAcademicTaxRequired();
    }
    
    public boolean isDebtDateRequired() {
        return implementation().isDebtDateRequired();
    }

    public boolean isReasonRequired() {
        return implementation().isReasonRequired();
    }

    public boolean isFinantialInstitutionRequired() {
        return implementation().isFinantialInstitutionRequired();
    }
    
    public IMassiveDebtGenerationStrategy implementation() {
        Class<IMassiveDebtGenerationStrategy> clazz;
        try {
            clazz = (Class<IMassiveDebtGenerationStrategy>) Class.forName(getImplementationClass());
            return (IMassiveDebtGenerationStrategy) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // @formatter:off
    /* ********
     * SERVICES
     * ********
     */
    // @formatter:on
    
    public static Stream<MassiveDebtGenerationType> findAll() {
        return Bennu.getInstance().getMassiveDebtGenerationTypesSet().stream();
    }
    
    public static Stream<MassiveDebtGenerationType> findByClassName(final String className) {
        return findAll().filter(t -> t.getImplementationClass().equals(className));
    }
    
    public static Stream<MassiveDebtGenerationType> findAllActive() {
        return findAll().filter(m -> m.isActive());
    }
    
    public static MassiveDebtGenerationType create(final String name, final String implementationClass) {
        return new MassiveDebtGenerationType(name, implementationClass);
    }

}
